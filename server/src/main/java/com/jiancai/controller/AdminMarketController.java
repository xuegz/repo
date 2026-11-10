package com.jiancai.controller;

import com.jiancai.common.R;
import com.jiancai.entity.Market;
import com.jiancai.service.MarketService;
import com.jiancai.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台端 - 建材市场管理
 */
@RestController
@RequestMapping("/api/admin/market")
public class AdminMarketController {

    @Autowired
    private MarketService marketService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/list")
    public R<List<Market>> list(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Integer status) {
        return R.ok(marketService.adminList(keyword, status));
    }

    @GetMapping("/options")
    public R<List<Market>> options() {
        return R.ok(marketService.adminList(null, 1));
    }

    @PostMapping("/save")
    public R<Void> save(@RequestBody Market market) {
        boolean isNew = market.getId() == null;
        marketService.saveMarket(market);
        operationLogService.record("market_save", "market", market.getId(),
                (isNew ? "新增" : "编辑") + "市场「" + market.getName() + "」");
        return R.ok();
    }

    @PostMapping("/remove/{id}")
    public R<Void> remove(@PathVariable Long id) {
        Market market = marketService.getById(id);
        marketService.removeMarket(id);
        operationLogService.record("market_remove", "market", id,
                "删除市场「" + (market == null ? id : market.getName()) + "」");
        return R.ok();
    }
}
