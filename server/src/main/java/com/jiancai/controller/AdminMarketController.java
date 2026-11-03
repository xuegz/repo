package com.jiancai.controller;

import com.jiancai.common.R;
import com.jiancai.entity.Market;
import com.jiancai.service.MarketService;
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
        marketService.saveMarket(market);
        return R.ok();
    }

    @PostMapping("/remove/{id}")
    public R<Void> remove(@PathVariable Long id) {
        marketService.removeMarket(id);
        return R.ok();
    }
}
