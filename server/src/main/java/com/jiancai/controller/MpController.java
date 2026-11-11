package com.jiancai.controller;

import com.jiancai.common.R;
import com.jiancai.entity.Category;
import com.jiancai.entity.Market;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Notice;
import com.jiancai.service.CategoryService;
import com.jiancai.service.MarketService;
import com.jiancai.service.MemberAccessService;
import com.jiancai.service.MpHomeService;
import com.jiancai.service.SiteService;
import com.jiancai.service.WxSubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 小程序门户接口：首页聚合、品类、市场、公告、订阅消息配置（无需登录）
 *
 * 产品 / 商户 / 询价 / 收藏 / 行为上报等接口已拆分到对应的领域 Controller。
 */
@RestController
@RequestMapping("/api/mp")
@RequiredArgsConstructor
public class MpController {

    private final MpHomeService mpHomeService;
    private final CategoryService categoryService;
    private final MarketService marketService;
    private final MemberAccessService memberAccessService;
    private final SiteService siteService;
    private final WxSubscribeService wxSubscribeService;

    /** 首页聚合：轮播 + 广告位 + 品类 + 热门市场 + 推荐产品（非会员不返回商户联系方式） */
    @GetMapping("/home")
    public R<Map<String, Object>> home(@RequestParam(required = false) String openid) {
        return R.ok(mpHomeService.home(openid));
    }

    @GetMapping("/categories")
    public R<List<Category>> categories() {
        return R.ok(categoryService.listEnabledTop());
    }

    /** 建材市场列表（含入驻商户数；传 lat/lng + sort=distance 按距离排序） */
    @GetMapping("/markets")
    public R<List<Market>> markets(@RequestParam(required = false) Double lat,
                                   @RequestParam(required = false) Double lng,
                                   @RequestParam(required = false) String sort) {
        return R.ok(marketService.listEnabled(lat, lng, sort, null));
    }

    /** 市场详情：入驻商户与在售产品数 */
    @SuppressWarnings("unchecked")
    @GetMapping("/market")
    public R<Map<String, Object>> market(@RequestParam Long id,
                                         @RequestParam(required = false) Double lat,
                                         @RequestParam(required = false) Double lng,
                                         @RequestParam(required = false) String openid) {
        Map<String, Object> data = marketService.detail(id, lat, lng);
        memberAccessService.maskMerchants((List<Merchant>) data.get("merchants"),
                memberAccessService.isMember(openid));
        return R.ok(data);
    }

    /** 市场公告（启用中，取前 5 条） */
    @GetMapping("/notices")
    public R<List<Notice>> notices() {
        return R.ok(siteService.listEnabledNotice());
    }

    /** 订阅消息配置：前端据此调用 wx.requestSubscribeMessage 获取授权 */
    @GetMapping("/subscribe/config")
    public R<Map<String, Object>> subscribeConfig() {
        return R.ok(wxSubscribeService.publicConfig());
    }
}
