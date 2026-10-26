package com.atoken.update.kusama.cache;

import com.atoken.blockchain.http.request.account.ksm.KsmClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class KsmUnbondingTimeCache {

    public static final String CACHE_KEY = "unbonding_time";
    @Value("${blockchain.ksm.guava.capacity}")
    private int capacity;
    @Value("${blockchain.ksm.guava.maximum-size}")
    private int maximumSize;
    @Value("${blockchain.ksm.guava.over-time: 86400}")
    private Long overTime;//缓存过期时间
    @Value("${blockchain.ksm.guava.concurrency-level}")
    private int concurrencyLevel;
    @Autowired
    private KsmClient ksmClient;


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
                        // 获取解绑时间
                        Integer era = ksmClient.getCurrentEra().intValue();
                        Integer sessionLength = ksmClient.getSessionLenghth().intValue();
                        Integer sessionPerEra = ksmClient.getSessionsPerEra().intValue();
                        return era * sessionLength * sessionPerEra + 100;
                    }
                });
    }

    public Integer get(String key) {
        return loadingCache.getUnchecked(key);
    }
}
