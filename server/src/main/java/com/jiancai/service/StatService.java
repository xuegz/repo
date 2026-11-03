package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.entity.CallRecord;
import com.jiancai.entity.ForwardRecord;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.entity.ViewRecord;
import com.jiancai.mapper.CallRecordMapper;
import com.jiancai.mapper.ForwardRecordMapper;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.ProductMapper;
import com.jiancai.mapper.ViewRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 浏览 / 转发 / 电话联系 统计业务
 */
@Service
public class StatService {

    /** 目标类型：1=产品 2=商户 */
    public static final int TARGET_PRODUCT = 1;
    public static final int TARGET_MERCHANT = 2;

    @Autowired
    private ViewRecordMapper viewRecordMapper;

    @Autowired
    private ForwardRecordMapper forwardRecordMapper;

    @Autowired
    private CallRecordMapper callRecordMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    /** 记录浏览 */
    public void recordView(Integer targetType, Long targetId, String openid) {
        if (targetId == null) {
            return;
        }
        ViewRecord record = new ViewRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setOpenid(openid);
        viewRecordMapper.insert(record);
        incr(targetType, targetId, "views");
    }

    /** 记录转发 */
    public void recordForward(Integer targetType, Long targetId, String openid) {
        if (targetId == null) {
            return;
        }
        ForwardRecord record = new ForwardRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setOpenid(openid);
        forwardRecordMapper.insert(record);
        incr(targetType, targetId, "forwards");
    }

    /**
     * 记录电话联系：产品页拨号、比价列表询价、商户主页拨号均走这里
     * merchantId 为空时自动推导
     */
    public void recordCall(Integer targetType, Long targetId, Long merchantId, String openid) {
        if (targetId == null) {
            return;
        }
        Long ownerId = merchantId;
        if (ownerId == null) {
            if (Integer.valueOf(TARGET_MERCHANT).equals(targetType)) {
                ownerId = targetId;
            } else {
                Product product = productMapper.selectById(targetId);
                ownerId = product == null ? null : product.getMerchantId();
            }
        }
        if (ownerId == null) {
            return;
        }

        CallRecord record = new CallRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setMerchantId(ownerId);
        record.setOpenid(openid);
        callRecordMapper.insert(record);
        incr(targetType, targetId, "calls");
    }

    private void incr(Integer targetType, Long targetId, String column) {
        if (Integer.valueOf(TARGET_PRODUCT).equals(targetType)) {
            productMapper.update(null, Wrappers.<Product>lambdaUpdate()
                    .eq(Product::getId, targetId)
                    .setSql(column + " = " + column + " + 1"));
        } else {
            merchantMapper.update(null, Wrappers.<Merchant>lambdaUpdate()
                    .eq(Merchant::getId, targetId)
                    .setSql(column + " = " + column + " + 1"));
        }
    }

    /** 平台总览数据 */
    public Map<String, Object> overview() {
        Map<String, Object> map = new HashMap<>(16);
        map.put("merchantTotal", merchantMapper.selectCount(null));
        map.put("merchantPending", merchantMapper.selectCount(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getStatus, 0)));
        map.put("productTotal", productMapper.selectCount(null));
        map.put("productPending", productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, 0)));
        map.put("viewTotal", viewRecordMapper.selectCount(null));
        map.put("forwardTotal", forwardRecordMapper.selectCount(null));
        map.put("callTotal", callRecordMapper.selectCount(null));
        return map;
    }

    /** 商户端数据概览 */
    public Map<String, Object> merchantOverview(Long merchantId) {
        List<Long> productIds = ownProductIds(merchantId);

        Map<String, Object> map = new HashMap<>(16);
        map.put("productTotal", productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchantId)));
        map.put("productOnSale", productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchantId).eq(Product::getStatus, 1)));
        map.put("productPending", productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchantId).eq(Product::getStatus, 0)));
        map.put("viewTotal", countView(merchantId, productIds));
        map.put("forwardTotal", countForward(merchantId, productIds));
        map.put("callTotal", callRecordMapper.selectCount(Wrappers.<CallRecord>lambdaQuery()
                .eq(CallRecord::getMerchantId, merchantId)));

        Merchant merchant = merchantMapper.selectById(merchantId);
        map.put("merchantViews", merchant == null ? 0 : merchant.getViews());
        map.put("merchantCalls", merchant == null ? 0 : merchant.getCalls());
        map.put("expireTime", merchant == null ? null : merchant.getExpireTime());
        return map;
    }

    /**
     * 商户最近动态：浏览 / 转发 / 电话联系 合并后按时间倒序
     */
    public List<Map<String, Object>> merchantRecords(Long merchantId, int limit) {
        Map<Long, String> productNames = productMapper
                .selectList(Wrappers.<Product>lambdaQuery().eq(Product::getMerchantId, merchantId))
                .stream()
                .collect(Collectors.toMap(Product::getId,
                        p -> p.getName() == null ? "未命名产品" : p.getName(), (a, b) -> a));
        List<Long> productIds = productNames.isEmpty()
                ? Collections.singletonList(-1L)
                : new ArrayList<>(productNames.keySet());
        Merchant merchant = merchantMapper.selectById(merchantId);
        String merchantName = merchant == null ? "门店" : merchant.getName();

        List<Map<String, Object>> result = new ArrayList<>();

        var viewWrapper = Wrappers.<ViewRecord>lambdaQuery()
                .and(w -> w.eq(ViewRecord::getTargetType, TARGET_MERCHANT)
                        .eq(ViewRecord::getTargetId, merchantId)
                        .or().eq(ViewRecord::getTargetType, TARGET_PRODUCT)
                        .in(ViewRecord::getTargetId, productIds));
        for (ViewRecord r : viewRecordMapper.selectList(viewWrapper)) {
            result.add(toRecord("浏览", r.getTargetType(), r.getTargetId(),
                    r.getOpenid(), r.getCreateTime(), productNames, merchantName));
        }

        var forwardWrapper = Wrappers.<ForwardRecord>lambdaQuery()
                .and(w -> w.eq(ForwardRecord::getTargetType, TARGET_MERCHANT)
                        .eq(ForwardRecord::getTargetId, merchantId)
                        .or().eq(ForwardRecord::getTargetType, TARGET_PRODUCT)
                        .in(ForwardRecord::getTargetId, productIds));
        for (ForwardRecord r : forwardRecordMapper.selectList(forwardWrapper)) {
            result.add(toRecord("转发", r.getTargetType(), r.getTargetId(),
                    r.getOpenid(), r.getCreateTime(), productNames, merchantName));
        }

        for (CallRecord r : callRecordMapper.selectList(Wrappers.<CallRecord>lambdaQuery()
                .eq(CallRecord::getMerchantId, merchantId))) {
            result.add(toRecord("电话联系", r.getTargetType(), r.getTargetId(),
                    r.getOpenid(), r.getCreateTime(), productNames, merchantName));
        }

        result.sort(Comparator.comparing(
                m -> (LocalDateTime) m.get("createTime"),
                Comparator.nullsLast(Comparator.reverseOrder())));
        if (result.size() > limit) {
            return result.subList(0, limit);
        }
        return result;
    }

    /** 商户产品被浏览 TOP10 */
    public List<Product> merchantHotProduct(Long merchantId) {
        return productMapper.selectList(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchantId)
                .orderByDesc(Product::getViews)
                .last("limit 10"));
    }

    // ---------------- 内部工具 ----------------

    private List<Long> ownProductIds(Long merchantId) {
        List<Product> products = productMapper.selectList(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchantId));
        if (products.isEmpty()) {
            return Collections.singletonList(-1L);
        }
        return products.stream().map(Product::getId).collect(Collectors.toList());
    }

    private long countView(Long merchantId, List<Long> productIds) {
        return viewRecordMapper.selectCount(Wrappers.<ViewRecord>lambdaQuery()
                .and(w -> w.eq(ViewRecord::getTargetType, TARGET_MERCHANT)
                        .eq(ViewRecord::getTargetId, merchantId)
                        .or().eq(ViewRecord::getTargetType, TARGET_PRODUCT)
                        .in(ViewRecord::getTargetId, productIds)));
    }

    private long countForward(Long merchantId, List<Long> productIds) {
        return forwardRecordMapper.selectCount(Wrappers.<ForwardRecord>lambdaQuery()
                .and(w -> w.eq(ForwardRecord::getTargetType, TARGET_MERCHANT)
                        .eq(ForwardRecord::getTargetId, merchantId)
                        .or().eq(ForwardRecord::getTargetType, TARGET_PRODUCT)
                        .in(ForwardRecord::getTargetId, productIds)));
    }

    private Map<String, Object> toRecord(String action, Integer targetType, Long targetId, String openid,
                                         LocalDateTime time, Map<Long, String> productNames, String merchantName) {
        Map<String, Object> m = new HashMap<>(8);
        m.put("action", action);
        m.put("targetType", targetType);
        m.put("targetId", targetId);
        m.put("targetName", Integer.valueOf(TARGET_PRODUCT).equals(targetType)
                ? productNames.getOrDefault(targetId, "已删除产品")
                : merchantName);
        m.put("visitor", maskOpenid(openid));
        m.put("createTime", time);
        return m;
    }

    /** 访客标识脱敏：只展示后 6 位 */
    private String maskOpenid(String openid) {
        if (openid == null || openid.isBlank()) {
            return "匿名访客";
        }
        return "访客" + openid.substring(Math.max(0, openid.length() - 6));
    }
}
