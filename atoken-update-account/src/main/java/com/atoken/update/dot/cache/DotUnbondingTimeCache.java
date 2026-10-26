package com.atoken.update.dot.cache;

import com.atoken.blockchain.http.request.account.dot.sidecar.DotSidecarClient;
import com.atoken.update.bnb.constant.DotConstant;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class DotUnbondingTimeCache {
    public static final String CACHE_KEY = "unbonding_time";
    @Value("${blockchain.dot.guava.capacity}")
    private int capacity;
    @Value("${blockchain.dot.guava.maximum-size}")
    private int maximumSize;
    @Value("${blockchain.dot.guava.over-time: 86400}")
    private Long overTime;//缓存过期时间
    @Value("${blockchain.dot.guava.concurrency-level}")
    private int concurrencyLevel;
    @Autowired
    private DotSidecarClient dotSidecarClient;


    private static LoadingCache<String, Integer> loadingCache;

    @PostConstruct
    public void init() {
        loadingCache = CacheBuilder.newBuilder()
                .initialCapacity(capacity)
                .maximumSize(maximumSize)
                .concurrencyLevel(concurrencyLevel)
                .expireAfterAccess(overTime, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Integer>() {
                    /**
                     * 没有找到相映apiid的信息，则执行下面的方法
                     * @param key
                     * @return
                     * @throws Exception
                     */
                    @Override
                    public Integer load(String key) {
                        // epochLength = 2400
                        // eraLength = (epochLength * 6)
                        // https://polkadot.api.subscan.io/api/scan/metadata
                        return DotConstant.ERA_LENGTH * DotConstant.UN_LOCK_TIME;

                    }
                });
    }

    public Integer get(String key) {
        return loadingCache.getUnchecked(key);
    }

}
