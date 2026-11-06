package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiancai.common.BusinessException;
import com.jiancai.config.CacheConfig;
import com.jiancai.entity.Market;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.mapper.MarketMapper;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.ProductMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 建材市场业务
 */
@Service
public class MarketService extends ServiceImpl<MarketMapper, Market> {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ProductMapper productMapper;

    /** 自身代理：走 @Cacheable，避免同类内调用绕过缓存 */
    @Autowired
    @Lazy
    private MarketService self;

    /** 小程序端可用市场：按排序值倒序，可附加距离与数量 */
    public List<Market> listEnabled(Double lat, Double lng, String sort, Integer limit) {
        List<Market> base = self.listEnabledBase();
        List<Market> list = new ArrayList<>(base.size());
        for (Market source : base) {
            Market copy = new Market();
            BeanUtils.copyProperties(source, copy);
            if (lat != null && lng != null && copy.getLatitude() != null && copy.getLongitude() != null) {
                copy.setDistance(distanceKm(lat, lng,
                        copy.getLatitude().doubleValue(), copy.getLongitude().doubleValue()));
            }
            list.add(copy);
        }
        if ("distance".equals(sort) && lat != null && lng != null) {
            list.sort(Comparator.comparing(m -> m.getDistance() == null ? Double.MAX_VALUE : m.getDistance()));
        }
        if (limit != null && limit > 0 && list.size() > limit) {
            return new ArrayList<>(list.subList(0, limit));
        }
        return list;
    }

    /** 缓存基础列表（含商户数），距离在返回前按请求坐标单独计算 */
    @Cacheable(cacheNames = CacheConfig.MARKET, key = "'enabled'")
    public List<Market> listEnabledBase() {
        List<Market> list = list(Wrappers.<Market>lambdaQuery()
                .eq(Market::getStatus, 1)
                .orderByDesc(Market::getSort)
                .orderByAsc(Market::getId));
        for (Market market : list) {
            fillInfo(market, null, null);
        }
        return list;
    }

    /** 平台端市场列表（含全部商户数） */
    public List<Market> adminList(String keyword, Integer status) {
        List<Market> list = list(Wrappers.<Market>lambdaQuery()
                .like(keyword != null && !keyword.isBlank(), Market::getName, keyword)
                .eq(status != null, Market::getStatus, status)
                .orderByDesc(Market::getSort)
                .orderByAsc(Market::getId));
        for (Market market : list) {
            market.setMerchantCount(merchantMapper.selectCount(
                    Wrappers.<Merchant>lambdaQuery().eq(Merchant::getMarketId, market.getId())).intValue());
        }
        return list;
    }

    /** 市场详情：市场信息 + 入驻商户 + 在售产品总数 */
    public Map<String, Object> detail(Long id, Double lat, Double lng) {
        Market market = getById(id);
        if (market == null || !Integer.valueOf(1).equals(market.getStatus())) {
            throw new BusinessException("建材市场不存在或已下线");
        }
        fillInfo(market, lat, lng);

        List<Merchant> merchants = merchantMapper.selectList(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getMarketId, id)
                .eq(Merchant::getStatus, 1)
                .orderByDesc(Merchant::getCreateTime));
        long productTotal = 0;
        for (Merchant merchant : merchants) {
            long products = productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                    .eq(Product::getMerchantId, merchant.getId())
                    .eq(Product::getStatus, 1));
            merchant.setProductCount((int) products);
            productTotal += products;
            if (lat != null && lng != null && merchant.getLatitude() != null && merchant.getLongitude() != null) {
                merchant.setDistance(distanceKm(lat, lng,
                        merchant.getLatitude().doubleValue(), merchant.getLongitude().doubleValue()));
            }
        }
        if (lat != null && lng != null) {
            merchants.sort(Comparator.comparing(
                    m -> m.getDistance() == null ? Double.MAX_VALUE : m.getDistance()));
        }

        Map<String, Object> data = new HashMap<>(4);
        data.put("market", market);
        data.put("merchants", merchants);
        data.put("productCount", productTotal);
        return data;
    }

    @CacheEvict(cacheNames = CacheConfig.MARKET, allEntries = true)
    public void saveMarket(Market market) {
        if (market.getName() == null || market.getName().isBlank()) {
            throw new BusinessException("请填写市场名称");
        }
        market.setName(market.getName().trim());
        long duplicate = count(Wrappers.<Market>lambdaQuery()
                .eq(Market::getName, market.getName())
                .ne(market.getId() != null, Market::getId, market.getId()));
        if (duplicate > 0) {
            throw new BusinessException("市场名称已存在");
        }
        if (market.getStatus() == null) {
            market.setStatus(1);
        }
        if (market.getSort() == null) {
            market.setSort(0);
        }
        if (market.getProvince() == null || market.getProvince().isBlank()) {
            market.setProvince("陕西省");
        }
        if (market.getCity() == null || market.getCity().isBlank()) {
            market.setCity("西安市");
        }
        saveOrUpdate(market);
        // 名称变更后同步商户上的冗余市场名
        merchantMapper.update(null, Wrappers.<Merchant>lambdaUpdate()
                .eq(Merchant::getMarketId, market.getId())
                .set(Merchant::getMarket, market.getName()));
    }

    @CacheEvict(cacheNames = CacheConfig.MARKET, allEntries = true)
    public void removeMarket(Long id) {
        Market market = getById(id);
        if (market == null) {
            throw new BusinessException("市场不存在");
        }
        long merchants = merchantMapper.selectCount(
                Wrappers.<Merchant>lambdaQuery().eq(Merchant::getMarketId, id));
        if (merchants > 0) {
            throw new BusinessException("该市场下还有 " + merchants + " 家商户，请先调整商户所属市场");
        }
        removeById(id);
    }

    /** 保存商户前同步市场 ID 与名称，二者只需传其一 */
    public void syncMerchantMarket(Merchant merchant) {
        if (merchant.getMarketId() != null) {
            Market market = getById(merchant.getMarketId());
            if (market == null) {
                throw new BusinessException("所选建材市场不存在");
            }
            merchant.setMarket(market.getName());
            return;
        }
        String name = merchant.getMarket();
        if (name == null || name.isBlank()) {
            merchant.setMarket(null);
            return;
        }
        Market market = getOne(Wrappers.<Market>lambdaQuery().eq(Market::getName, name.trim()), false);
        if (market == null) {
            merchant.setMarket(name.trim());
        } else {
            merchant.setMarketId(market.getId());
            merchant.setMarket(market.getName());
        }
    }

    private void fillInfo(Market market, Double lat, Double lng) {
        market.setMerchantCount(merchantMapper.selectCount(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getMarketId, market.getId())
                .eq(Merchant::getStatus, 1)).intValue());
        if (lat != null && lng != null && market.getLatitude() != null && market.getLongitude() != null) {
            market.setDistance(distanceKm(lat, lng,
                    market.getLatitude().doubleValue(), market.getLongitude().doubleValue()));
        }
    }

    /** 两点球面距离（公里，保留一位小数） */
    public static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
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
