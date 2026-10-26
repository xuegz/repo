package com.atoken.update.thread.ont;

import com.atoken.update.entity.ont.Block;
import com.atoken.update.entity.ont.Tx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author dfg
 */
public abstract class BaseThread<T> {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected List<Tx> txList = Collections.synchronizedList(new ArrayList<>());
    protected CountDownLatch countDownLatch;
    protected final static Integer poolSize = 1;
    protected ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(poolSize), Executors.defaultThreadFactory());
    protected LinkedBlockingQueue<T> txQueue = new LinkedBlockingQueue<>();
    protected Block block;
    public BaseThread() {
        for (int i = 0; i < poolSize; i++) {
            threadPoolExecutor.execute(runnable());
        }
    }

    public abstract void execute();

    public abstract Runnable runnable();

    public void init(Block block) {
        this.block = block;
        this.txList = Collections.synchronizedList(new ArrayList<>());
    }

    public List<Tx> getTxList() {
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
}
