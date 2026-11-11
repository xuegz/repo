package com.jiancai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Category;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.PaymentRecord;
import com.jiancai.entity.Product;
import com.jiancai.entity.SysUser;
import com.jiancai.mapper.CategoryMapper;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.PaymentRecordMapper;
import com.jiancai.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商户业务
 */
@Service
public class MerchantService extends ServiceImpl<MerchantMapper, Merchant> {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private SystemConfigService systemConfigService;

    /** 后台人工开通/续费会员：线下收款后按年延长有效期并生成缴费记录 */
    public Map<String, Object> openMembership(Long merchantId, Integer years) {
        if (merchantId == null) {
            throw new BusinessException("缺少商户编号");
        }
        int addYears = years == null ? 1 : years;
        if (addYears < 1 || addYears > 5) {
            throw new BusinessException("开通年限需在 1-5 年之间");
        }
        Merchant merchant = getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        if (!Integer.valueOf(1).equals(merchant.getStatus())) {
            throw new BusinessException("商户未通过审核，暂不能开通会员");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime base = merchant.getExpireTime() != null && merchant.getExpireTime().isAfter(now)
                ? merchant.getExpireTime() : now;
        LocalDateTime expire = base.plusYears(addYears);
        merchant.setExpireTime(expire);
        updateById(merchant);

        PaymentRecord record = new PaymentRecord();
        record.setMerchantId(merchantId);
        record.setAmount(BigDecimal.valueOf(365L * addYears));
        record.setYears(addYears);
        record.setPayType("offline");
        record.setStatus(1);
        record.setPayTime(now);
        record.setExpireTime(expire);
        record.setCreateTime(now);
        paymentRecordMapper.insert(record);

        Map<String, Object> data = new HashMap<>(4);
        data.put("expireTime", expire);
        data.put("amount", record.getAmount());
        return data;
    }

    /** 分页并附加展示信息 */
    public IPage<Merchant> pageWithInfo(IPage<Merchant> page, String name, Integer status, String market, Long marketId) {
        IPage<Merchant> result = page(page, Wrappers.<Merchant>lambdaQuery()
                .like(name != null && !name.isBlank(), Merchant::getName, name)
                .like(market != null && !market.isBlank(), Merchant::getMarket, market)
                .eq(marketId != null, Merchant::getMarketId, marketId)
                .eq(status != null, Merchant::getStatus, status)
                .orderByDesc(Merchant::getCreateTime));
        fillInfo(result.getRecords());
        return result;
    }

    /** 填充品类名称与产品数量 */
    public void fillInfo(List<Merchant> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<Long, String> catMap = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
        for (Merchant m : list) {
            if (m.getCategoryId() != null) {
                m.setCategoryName(catMap.get(m.getCategoryId()));
            }
            Long count = productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                    .eq(Product::getMerchantId, m.getId()));
            m.setProductCount(count == null ? 0 : count.intValue());
        }
    }

    /**
     * 商户审核：通过后自动开通商户端账号（账号=手机号，初始密码 123456）
     */
    public void audit(Long id, Integer status) {
        Merchant merchant = getById(id);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        merchant.setStatus(status);
        if (Integer.valueOf(1).equals(status)) {
            if (merchant.getPhone() == null || merchant.getPhone().isBlank()) {
                throw new BusinessException("请先完善商户联系电话，否则无法开通登录账号");
            }
            // 已有账号则跳过
            long exists = userService.lambdaQuery()
                    .eq(SysUser::getMerchantId, id).count();
            if (exists == 0) {
                userService.createUser(merchant.getPhone(), "123456",
                        merchant.getContactName(), merchant.getPhone(), 2, id, true);
            }
            applyPromoMembership(merchant);
        }
        updateById(merchant);
    }

    /** 推广期赠送会员：未开通或已到期时按后台配置月数开通，不影响有效期内会员 */
    public void applyPromoMembership(Merchant merchant) {
        LocalDateTime now = LocalDateTime.now();
        if (merchant.getExpireTime() == null || !merchant.getExpireTime().isAfter(now)) {
            merchant.setExpireTime(now.plusMonths(systemConfigService.membershipMonths()));
        }
    }

    /** 把会员有效期配置同步到未开通或已到期的已通过商户，返回更新数量 */
    public int applyPromoMembershipToExisting(int months) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = now.plusMonths(months);
        return getBaseMapper().update(null, Wrappers.<Merchant>lambdaUpdate()
                .eq(Merchant::getStatus, 1)
                .and(w -> w.isNull(Merchant::getExpireTime).or().lt(Merchant::getExpireTime, now))
                .set(Merchant::getExpireTime, expire));
    }

    /** 平台重置商户密码：生成临时密码并返回给平台，商户首次登录需修改 */
    public Map<String, Object> resetMerchantPassword(Long merchantId) {
        Merchant merchant = getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        SysUser user = userService.lambdaQuery()
                .eq(SysUser::getMerchantId, merchantId)
                .last("LIMIT 1")
                .one();
        String tempPassword = randomPassword();
        if (user == null) {
            if (merchant.getPhone() == null || merchant.getPhone().isBlank()) {
                throw new BusinessException("商户未填写联系电话，无法生成登录账号");
            }
            Long userId = userService.createUser(merchant.getPhone(), tempPassword,
                    merchant.getContactName(), merchant.getPhone(), 2, merchantId, true);
            user = userService.getById(userId);
        } else {
            userService.resetPassword(user.getId(), tempPassword);
        }
        Map<String, Object> result = new HashMap<>(4);
        result.put("username", user.getUsername());
        result.put("password", tempPassword);
        return result;
    }

    /** 避开易混淆字符的 8 位临时密码 */
    private String randomPassword() {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /** 会员到期情况统计（用于平台首页） */
    public Map<String, Long> statusCount() {
        Map<String, Long> map = new HashMap<>(8);
        map.put("total", count());
        map.put("pending", count(Wrappers.<Merchant>lambdaQuery().eq(Merchant::getStatus, 0)));
        map.put("approved", count(Wrappers.<Merchant>lambdaQuery().eq(Merchant::getStatus, 1)));
        map.put("rejected", count(Wrappers.<Merchant>lambdaQuery().eq(Merchant::getStatus, 2)));
        return map;
    }
}
