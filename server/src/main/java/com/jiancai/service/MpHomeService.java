package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.entity.Market;
import com.jiancai.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序首页聚合：轮播 + 广告位 + 品类 + 热门市场 + 推荐产品
 */
@Service
@RequiredArgsConstructor
public class MpHomeService {

    private final SiteService siteService;
    private final CategoryService categoryService;
    private final MarketService marketService;
    private final ProductService productService;
    private final MemberAccessService memberAccessService;

    /** 首页聚合数据（非会员不返回商户联系方式） */
    public Map<String, Object> home(String openid) {
        Map<String, Object> data = new HashMap<>(8);
        data.put("banners", siteService.listEnabledBanner());
        data.put("ads", siteService.listAdByPosition("home"));
        data.put("categories", categoryService.listEnabledTop());
        List<Market> markets = marketService.listEnabled(null, null, null, null);
        markets.sort(Comparator.comparingInt(
                (Market m) -> m.getMerchantCount() == null ? 0 : m.getMerchantCount()).reversed());
        data.put("markets", markets.size() > 6 ? new ArrayList<>(markets.subList(0, 6)) : markets);

        List<Product> recommends = productService.list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime)
                .last("limit 10"));
        productService.fillInfo(recommends);
        memberAccessService.maskProducts(recommends, memberAccessService.isMember(openid));
        data.put("recommends", recommends);
        return data;
    }
}
