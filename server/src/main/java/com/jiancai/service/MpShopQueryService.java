package com.jiancai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 小程序商户浏览：商户主页 / 商户列表（热度、距离排序）/ 品牌导览分页
 */
@Service
@RequiredArgsConstructor
public class MpShopQueryService {

    private final MerchantService merchantService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final MemberAccessService memberAccessService;

    /** 商户详情（含在售产品，非会员隐藏联系方式） */
    public Map<String, Object> merchantDetail(Long id, String openid) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null || !Integer.valueOf(1).equals(merchant.getStatus())) {
            throw new BusinessException("商户不存在或未上线");
        }
        List<Product> products = productService.list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, id)
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime)
                .last("limit 50"));
        productService.fillInfo(products);

        boolean member = memberAccessService.isMember(openid);
        memberAccessService.maskMerchant(merchant, member);
        memberAccessService.maskProducts(products, member);

        Map<String, Object> data = new HashMap<>(4);
        data.put("merchant", merchant);
        data.put("products", products);
        return data;
    }

    /** 入驻商户列表（按关键词/市场/品类筛选，含在售产品数；传 lat/lng + sort=distance 按距离排序） */
    public List<Merchant> merchants(String keyword, String market, Long marketId, Long categoryId,
                                    Double lat, Double lng, String sort, String openid) {
        boolean member = memberAccessService.isMember(openid);
        var wrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getStatus, 1)
                .like(keyword != null && !keyword.isBlank(), Merchant::getName, keyword)
                .eq(market != null && !market.isBlank(), Merchant::getMarket, market)
                .eq(marketId != null, Merchant::getMarketId, marketId)
                .in(categoryId != null, Merchant::getCategoryId,
                        categoryId == null ? List.of() : categoryService.subtreeIds(categoryId));
        List<Merchant> list = merchantService.list(wrapper);
        Map<Long, Integer> counts = productService.countOnSaleByMerchants(
                list.stream().map(Merchant::getId).collect(Collectors.toList()));
        for (Merchant m : list) {
            m.setProductCount(counts.getOrDefault(m.getId(), 0));
            // 位置为会员权益，非会员不计算、不返回距离
            if (member && lat != null && lng != null && m.getLatitude() != null && m.getLongitude() != null) {
                m.setDistance(distanceKm(lat, lng,
                        m.getLatitude().doubleValue(), m.getLongitude().doubleValue()));
            }
        }
        Comparator<Merchant> byHot = Comparator.comparingLong(MpShopQueryService::hotScore).reversed();
        if (member && "distance".equals(sort) && lat != null && lng != null) {
            Comparator<Merchant> byDistance = Comparator.comparing(
                    m -> m.getDistance() == null ? Double.MAX_VALUE : m.getDistance());
            list.sort(byDistance.thenComparing(byHot));
        } else {
            // 热度加权排序：浏览 1 分 + 转发 2 分 + 电话 5 分
            list.sort(byHot);
        }
        memberAccessService.maskMerchants(list, member);
        return list;
    }

    /** 入驻商户分页（品牌导览页滚动加载，按热度排序，关键词匹配名称/市场） */
    public IPage<Merchant> merchantPage(long pageNum, long pageSize, String keyword,
                                        Long categoryId, Long marketId, String openid) {
        long size = Math.min(Math.max(pageSize, 1), 100);
        var wrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getStatus, 1)
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(Merchant::getName, keyword)
                        .or()
                        .like(Merchant::getMarket, keyword))
                .eq(marketId != null, Merchant::getMarketId, marketId)
                .in(categoryId != null, Merchant::getCategoryId,
                        categoryId == null ? List.of() : categoryService.subtreeIds(categoryId))
                .orderByDesc(Merchant::getViews)
                .orderByDesc(Merchant::getForwards)
                .orderByDesc(Merchant::getCalls)
                .orderByAsc(Merchant::getId);
        IPage<Merchant> page = merchantService.page(new Page<>(Math.max(pageNum, 1), size), wrapper);
        Map<Long, Integer> counts = productService.countOnSaleByMerchants(
                page.getRecords().stream().map(Merchant::getId).collect(Collectors.toList()));
        page.getRecords().forEach(m -> m.setProductCount(counts.getOrDefault(m.getId(), 0)));
        memberAccessService.maskMerchants(page.getRecords(), memberAccessService.isMember(openid));
        return page;
    }

    private static long hotScore(Merchant m) {
        return (m.getViews() == null ? 0 : m.getViews())
                + (m.getForwards() == null ? 0 : m.getForwards()) * 2L
                + (m.getCalls() == null ? 0 : m.getCalls()) * 5L;
    }

    /** 两点球面距离（公里，保留一位小数） */
    private static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double dLat = radLat2 - radLat1;
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double km = 6371.0 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(km * 10) / 10.0;
    }
}
