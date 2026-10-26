package com.atoken.update.atom.cache;

import com.atoken.blockchain.http.request.account.atom.AtomClient;
import com.atoken.blockchain.http.request.account.atom.AtomStakingInfo;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class AtomUnbondingTimeCache {
    public static final String CACHE_KEY = "unbonding_time";
    @Value("${blockchain.atom.guava.capacity}")
    private int capacity;
    @Value("${blockchain.atom.guava.maximum-size}")
    private int maximumSize;
    @Value("${blockchain.atom.guava.over-time: 86400}")
    private Long overTime;//缓存过期时间
    @Value("${blockchain.atom.guava.concurrency-level}")
    private int concurrencyLevel;
    @Autowired
    private AtomClient atomClient;

    private static LoadingCache<String, Long> loadingCache;

    @PostConstruct
    public void init() {
        loadingCache = CacheBuilder.newBuilder()
                .initialCapacity(capacity)
                .maximumSize(maximumSize)
                .concurrencyLevel(concurrencyLevel)
                .expireAfterAccess(overTime, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Long>() {
                    /**
                     * 没有找到相映apiid的信息，则执行下面的方法
                     * @param key
                     * @return
                     * @throws Exception
                     */
                    @Override
                    public Long load(String key) {
                        // 获取质押信息
                        AtomStakingInfo atomStakingInfo = atomClient.getStakingInfo();
                        return atomStakingInfo.getUnbondingTime() / 1000000000 + 15;
                    }
                });
    }

    public Long get(String key) {
        return loadingCache.getUnchecked(key);
    }
}
