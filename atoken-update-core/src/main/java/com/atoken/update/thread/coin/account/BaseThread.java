package com.atoken.update.thread.coin.account;

import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.util.AtokenLogger;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public abstract class BaseThread<T, TX extends Tx> implements AtokenLogger {
    protected List<TX> txList = Collections.synchronizedList(new ArrayList<>());
    protected CountDownLatch countDownLatch;
    protected BlockchainConfig blockchainConfig;
    protected ThreadPoolExecutor threadPoolExecutor;
    protected LinkedBlockingQueue<T> txQueue = new LinkedBlockingQueue<>();
    protected Block block;

    public BaseThread(BlockchainConfig blockchainConfig) {
        this.blockchainConfig = blockchainConfig;
    }

    public abstract void execute();

    public abstract Runnable runnable();

    public void init(Block block) {
        this.block = block;
        this.txList = Collections.synchronizedList(new ArrayList<>());
    }

    public List<TX> getTxList() {
        return txList;
    }

    public void await() {
        try {
            this.countDownLatch.await();
        } catch (Exception e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void initThreadPool() {
        threadPoolExecutor = new ThreadPoolExecutor(blockchainConfig.getThreadPoolSize(), blockchainConfig.getThreadPoolSize(),
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(blockchainConfig.getThreadPoolSize()), Executors.defaultThreadFactory());

        for (int i = 0; i < blockchainConfig.getThreadPoolSize(); i++) {
            threadPoolExecutor.execute(runnable());
        }
    }
}
