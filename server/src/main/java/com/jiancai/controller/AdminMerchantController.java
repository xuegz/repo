package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.R;
import com.jiancai.entity.Merchant;
import com.jiancai.service.MarketService;
import com.jiancai.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        merchantService.audit(id, status);
        return R.ok();
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
}
