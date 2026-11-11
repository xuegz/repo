package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Merchant;
import com.jiancai.service.MerchantService;
import com.jiancai.service.MpShopQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序商户浏览接口：商户主页 / 商户列表 / 品牌导览分页 / 邀请码校验（无需登录）
 */
@RestController
@RequestMapping("/api/mp")
@RequiredArgsConstructor
public class MpShopController {

    private final MpShopQueryService mpShopQueryService;
    private final MerchantService merchantService;

    /** 商户详情（含在售产品） */
    @GetMapping("/merchant")
    public R<Map<String, Object>> merchant(@RequestParam Long id,
                                           @RequestParam(required = false) String openid) {
        return R.ok(mpShopQueryService.merchantDetail(id, openid));
    }

    /** 入驻商户列表（按关键词/市场/品类筛选，含在售产品数；传 lat/lng + sort=distance 按距离排序） */
    @GetMapping("/merchants")
    public R<List<Merchant>> merchants(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String market,
                                       @RequestParam(required = false) Long marketId,
                                       @RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) Double lat,
                                       @RequestParam(required = false) Double lng,
                                       @RequestParam(required = false) String sort,
                                       @RequestParam(required = false) String openid) {
        return R.ok(mpShopQueryService.merchants(keyword, market, marketId, categoryId,
                lat, lng, sort, openid));
    }

    /** 入驻商户分页（品牌导览页滚动加载，按热度排序，关键词匹配名称/市场） */
    @GetMapping("/merchants/page")
    public R<IPage<Merchant>> merchantPage(@RequestParam(defaultValue = "1") long pageNum,
                                           @RequestParam(defaultValue = "30") long pageSize,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) Long marketId,
                                           @RequestParam(required = false) String openid) {
        return R.ok(mpShopQueryService.merchantPage(pageNum, pageSize, keyword,
                categoryId, marketId, openid));
    }

    /** 邀请码校验：入驻页展示邀请人门店名称，不暴露联系方式 */
    @GetMapping("/invite")
    public R<Map<String, Object>> invite(@RequestParam String code) {
        Merchant inviter = merchantService.findByInviteCode(code);
        if (inviter == null) {
            throw new BusinessException("邀请码不正确或已失效");
        }
        Map<String, Object> data = new HashMap<>(2);
        data.put("name", inviter.getName());
        return R.ok(data);
    }
}
