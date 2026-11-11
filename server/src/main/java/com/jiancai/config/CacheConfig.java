package com.jiancai.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 进程内缓存：品类、轮播、广告、公告等配置类数据读多写少。
 * 后台修改时显式清除，30 分钟过期只作为兜底。
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CATEGORY = "mpCategory";
    public static final String BANNER = "mpBanner";
    public static final String AD = "mpAd";
    public static final String NOTICE = "mpNotice";
    public static final String MARKET = "mpMarket";
    /** 商户看板统计，60 秒短缓存（允许延迟，换取高频刷新下的查询合并） */
    public static final String MERCHANT_STAT = "merchantStat";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(Duration.ofMinutes(30)));
        // 市场列表带商户数量，变化相对频繁，单独用 2 分钟短缓存
        manager.registerCustomCache(MARKET, Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(Duration.ofMinutes(2))
                .build());
        manager.registerCustomCache(MERCHANT_STAT, Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(Duration.ofSeconds(60))
                .build());
        return manager;
    }
}
