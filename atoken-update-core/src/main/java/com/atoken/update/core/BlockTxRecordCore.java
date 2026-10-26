package com.atoken.update.core;

import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.ImportAddressBean;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.ExchangeService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;

public abstract class BlockTxRecordCore implements AtokenLogger {

    protected BlockchainConfig blockchainConfig;

    private BlockHandlerService blockHandlerService;

    private ExchangeService exchangeService;

    protected String coinType;

    private CountDownLatch countDownLatch;

    protected BlockTxRecordCore(BlockHandlerService blockHandlerService,BlockchainConfig blockchainConfig,ExchangeService exchangeService){
        this.blockHandlerService = blockHandlerService;
        this.blockchainConfig = blockchainConfig;
        this.exchangeService = exchangeService;
    }

    public abstract List<TxRecord> pullTxRecordData(String addr);

    public void batchHandlerTxRecord(){
        //没有找到对应的币种则直接跳过
        List<ImportAddressBean> importAddressBeanList = this.exchangeService.selectImportAddressByCoinType(coinType.toUpperCase());
        if (CollectionUtils.isEmpty(importAddressBeanList)){
            return;
        }

        ThreadPoolExecutor txRecordExecutor = new ThreadPoolExecutor(this.blockchainConfig.getThreadPoolSize()*10,this.blockchainConfig.getHandlerTxRecordMaxThread(),30000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(10000), Executors.defaultThreadFactory());
        txRecordExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        //逻辑分页处理
        Integer total = importAddressBeanList.size();

        Integer pageSize = this.blockchainConfig.getHandlerNum();

        //获取页数，根据页数分配任务数量，每个任务处理pageSize条数据落库
        Integer pages = total%pageSize==0?total/pageSize:total/pageSize+1;

        this.countDownLatch = new CountDownLatch(pages);
        //遍历导入的钱包地址并获取交易记录
        for (int i=1;i<=pages;i++){
            List<ImportAddressBean> waitHandlerImport = startPage(importAddressBeanList,i,pageSize,total,pages);
            txRecordExecutor.submit(new HandlerTxRecordTask(waitHandlerImport,blockHandlerService));
        }

        try {
            countDownLatch.await();
        }catch (InterruptedException e){
            logger.error("====coin {},error:{}",this.blockchainConfig.getBlockchainAsset(),e);
            Thread.currentThread().interrupt();
        }finally {
            txRecordExecutor.shutdown();
        }
    }

    class HandlerTxRecordTask implements Callable<Boolean> {

        List<ImportAddressBean> importAddressBeans;

        BlockHandlerService blockHandlerService;

        public HandlerTxRecordTask(List<ImportAddressBean> importAddressBeans,BlockHandlerService blockHandlerService){
            this.importAddressBeans = importAddressBeans;
            this.blockHandlerService = blockHandlerService;
        }

        @Override
        public Boolean call() throws Exception {
            try {
                importAddressBeans.stream().forEach(importAddressBean -> {
                    List<TxRecord> txRecordList = pullTxRecordData(importAddressBean.getAddr());
                    if (CollectionUtils.isEmpty(txRecordList)){
                        return;
                    }
                    this.blockHandlerService.batchHandlerTxRecord(txRecordList);
                });
            }catch (Exception e){
                logger.error("batch handler tx record error:{}",e);
                throw new RuntimeException("tx record error",e);
            }finally {
                countDownLatch.countDown();
            }
            return true;
        }
    }

    private List<ImportAddressBean> startPage(List<ImportAddressBean> txRecords,Integer pageNum,Integer pageSize,Integer total,Integer pages){
        // 开始索引
        int fromIndex = 0;
        // 结束索引
        int toIndex = 0;

        if (!pageNum.equals(pages)) {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = fromIndex + pageSize;
        } else {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = total;
        }

       return txRecords.subList(fromIndex, toIndex);
    }
}
