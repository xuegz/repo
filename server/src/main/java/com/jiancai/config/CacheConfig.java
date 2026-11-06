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

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(CATEGORY, BANNER, AD, NOTICE);
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(Duration.ofMinutes(30)));
        return manager;
    }
}
