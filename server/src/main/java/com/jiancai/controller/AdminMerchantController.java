package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Inquiry;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.entity.SysUser;
import com.jiancai.mapper.InquiryMapper;
import com.jiancai.mapper.ProductMapper;
import com.jiancai.mapper.SysUserMapper;
import com.jiancai.service.MarketService;
import com.jiancai.service.MerchantService;
import com.jiancai.service.OperationLogService;
import com.jiancai.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 平台端 - 商户管理（入驻审核、信息维护、缴费记录）
 */
@RestController
@RequestMapping("/api/admin/merchant")
public class AdminMerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MarketService marketService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private StatService statService;

    @Autowired
    private InquiryMapper inquiryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/page")
    public R<IPage<Merchant>> page(@RequestParam(defaultValue = "1") long pageNum,
                                   @RequestParam(defaultValue = "10") long pageSize,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) String market,
                                   @RequestParam(required = false) Long marketId) {
        return R.ok(merchantService.pageWithInfo(new Page<>(pageNum, pageSize), name, status, market, marketId));
    }

    /** 平台代商户建档 / 编辑 */
    @PostMapping("/save")
    public R<Void> save(@RequestBody Merchant merchant) {
        if (merchant.getId() == null) {
            merchant.setStatus(1);
            merchant.setViews(0);
            merchant.setForwards(0);
            merchant.setCalls(0);
        }
        marketService.syncMerchantMarket(merchant);
        merchantService.saveOrUpdate(merchant);
        return R.ok();
    }

    /** 审核：0=待审核 1=通过（自动开通账号） 2=拒绝 */
    @PostMapping("/audit")
    public R<Void> audit(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        Integer status = Integer.valueOf(body.get("status").toString());
        Merchant merchant = merchantService.getById(id);
        merchantService.audit(id, status);
        String actionText = status == 1 ? "通过" : status == 2 ? "拒绝" : "更新";
        operationLogService.record("merchant_audit", "merchant", id,
                actionText + "商户「" + (merchant == null ? id : merchant.getName()) + "」");
        return R.ok();
    }

    /** 重置商户登录密码，返回临时密码（商户首次登录需修改） */
    @PostMapping("/reset-password")
    public R<Map<String, Object>> resetPassword(@RequestBody Map<String, Object> body) {
        Long merchantId = Long.valueOf(body.get("merchantId").toString());
        Merchant merchant = merchantService.getById(merchantId);
        Map<String, Object> result = merchantService.resetMerchantPassword(merchantId);
        operationLogService.record("merchant_reset_password", "merchant", merchantId,
                "重置商户「" + (merchant == null ? merchantId : merchant.getName()) + "」登录密码");
        return R.ok(result);
    }

    @PostMapping("/remove/{id}")
    public R<Void> remove(@PathVariable Long id) {
        merchantService.removeById(id);
        return R.ok();
    }

    @GetMapping("/status-count")
    public R<Map<String, Long>> statusCount() {
        return R.ok(merchantService.statusCount());
    }

    /** 商户经营画像：账号状态、产品/流量/询价统计、热门产品与最近动态 */
    @GetMapping("/detail/{id}")
    public R<Map<String, Object>> detail(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        Map<String, Object> data = new HashMap<>(16);
        data.put("merchant", merchant);
        data.put("stats", statService.merchantOverview(id));
        data.put("hotProducts", statService.merchantHotProduct(id));
        data.put("recentRecords", statService.merchantRecords(id, 10));

        Map<String, Long> inquiryCounts = new HashMap<>(8);
        inquiryCounts.put("total", inquiryMapper.selectCount(Wrappers.<Inquiry>lambdaQuery()
                .eq(Inquiry::getMerchantId, id)));
        inquiryCounts.put("pending", inquiryMapper.selectCount(Wrappers.<Inquiry>lambdaQuery()
                .eq(Inquiry::getMerchantId, id).eq(Inquiry::getStatus, 0)));
        inquiryCounts.put("quoted", inquiryMapper.selectCount(Wrappers.<Inquiry>lambdaQuery()
                .eq(Inquiry::getMerchantId, id).eq(Inquiry::getStatus, 1)));
        inquiryCounts.put("follow", inquiryMapper.selectCount(Wrappers.<Inquiry>lambdaQuery()
                .eq(Inquiry::getMerchantId, id).eq(Inquiry::getStatus, 2)));
        data.put("inquiryCounts", inquiryCounts);

        Map<Long, String> productNames = productMapper.selectList(Wrappers.<Product>lambdaQuery()
                        .eq(Product::getMerchantId, id))
                .stream()
                .collect(Collectors.toMap(Product::getId,
                        p -> p.getName() == null ? "未命名产品" : p.getName(), (a, b) -> a));
        List<Inquiry> recentInquiries = inquiryMapper.selectList(Wrappers.<Inquiry>lambdaQuery()
                .eq(Inquiry::getMerchantId, id)
                .orderByDesc(Inquiry::getCreateTime)
                .last("limit 10"));
        for (Inquiry inquiry : recentInquiries) {
            inquiry.setProductName(productNames.getOrDefault(inquiry.getProductId(), "已删除产品"));
        }
        data.put("recentInquiries", recentInquiries);

        SysUser account = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getMerchantId, id)
                .last("limit 1"));
        if (account == null) {
            data.put("account", null);
        } else {
            Map<String, Object> accountInfo = new HashMap<>(4);
            accountInfo.put("username", account.getUsername());
            accountInfo.put("status", account.getStatus());
            accountInfo.put("mustChangePassword", account.getMustChangePassword());
            accountInfo.put("createTime", account.getCreateTime());
            data.put("account", accountInfo);
        }
        return R.ok(data);
    }
}
