package com.atoken.update.dot.service;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.account.dot.sidecar.DotSidecarClient;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.*;
import com.atoken.blockchain.http.request.account.dot.sidecar.enums.MethodEnum;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.Block;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.entity.dot.DotTx;
import com.atoken.update.enums.FeeType;
import com.atoken.update.notification.DingdingNotification;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.dot.DotTxRecordServiceImpl;
import com.atoken.update.util.LoggerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: erwin
 * @time: 2020/9/10 11:09 上午
 */
@Slf4j
@Service("dotStakingBlockHandlerService")
public class DotStakingBlockHandlerServiceImpl implements BlockHandlerService {


    @Autowired
    private DotSidecarClient dotSidecarClient;

    @Autowired
    @Qualifier("dotBlockchainConfig")
    private BlockchainConfig blockchainConfig;

    @Autowired
    private DingdingNotification dingdingNotification;

    @Value("${atoken.notification.duration: 3600}")
    private Long notificationSendDuration;

    private static Long notificationSendTime = 0L;

    @Resource(name = "dotTxRecordService")
    private DotTxRecordServiceImpl txRecordService;

    @Override
    public Integer getBlockNumber() {
        return dotSidecarClient.blocksHead().getNumber();
    }

    /**
     * 根据高度列表获取区块信息列表
     *
     * @param blockHeights 区块高度集合
     * @return 区块集合
     */
    @Override
    public List<Block> getBlocks(List<Integer> blockHeights) {

        return blockHeights.stream().map(height -> {

            try {
                BlockResp blockResp = dotSidecarClient.blocks(height);

                Long timestamp = Long.parseLong(blockResp.getExtrinsics().get(0).getArgs().get("now")) / 1000;

                long count = blockResp.getExtrinsics().stream()
                        .map(extrinsicsDTO -> {
                            List<EventDTO> eventDTOList = extrinsicsDTO.getEvents().stream()
                                    .filter(eventDTO -> MethodEnum.isSupport(eventDTO.getMethod()))
                                    .collect(Collectors.toList());
                            int size = eventDTOList.size();
                            if (MethodEnum.STAKING_SET_PAYEE.getMethod().equals(extrinsicsDTO.getMethod().getMethod())) {
                                size++;
                            } else if (MethodEnum.STAKING_NOMINATE.getMethod().equals(extrinsicsDTO.getMethod().getMethod())) {
                                size++;
                            } else if (MethodEnum.STAKING_SET_PAYEE.getMethod().equals(extrinsicsDTO.getMethod().getMethod())) {
                                size++;
                            }
                            return size;
                        }).count();
                Block block = new Block();
                block.setHash(blockResp.getHash());
                block.setHeight(blockResp.getNumber());
                block.setPrevious(blockResp.getParentHash());
                block.setSize(0);
                block.setTimestamp(timestamp);
                block.setTxCount((int) count);
                block.setState((short) 1);
                return block;
            } catch (Exception e) {
                log.error("dotSidecarClient.blocks error height:{}", height);
                // 邮件发送间隔时间， 默认一个小时
                if (Instant.now().getEpochSecond() - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.nodeExceptionAlarm(blockchainConfig.getMainAsset(), height, LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 根据区块信息获取交易信息
     *
     * @param block
     * @return
     */
    @Override
    public List<DotTx> getBlockTx(Block block) {

        BlockResp blockResp = dotSidecarClient.blocks(block.getHash());


        long count = blockResp.getExtrinsics().stream()
                .map(extrinsicsDTO -> {
                    List<EventDTO> eventDTOList = extrinsicsDTO.getEvents().stream()
                            .filter(eventDTO -> MethodEnum.isSupport(eventDTO.getMethod()))
                            .collect(Collectors.toList());
                    int size = eventDTOList.size();
                    return size;
                }).count();

        if (count <= 0) {
            return null;
        }

        return blockResp.getExtrinsics().parallelStream()
                .filter(extrinsicsDTO -> Objects.nonNull(extrinsicsDTO.getSignature()))
                .map(extrinsicsDTO -> {

                    List<DotTx> txList = new ArrayList<>();

                    String method = extrinsicsDTO.getMethod().getMethod();
                    if (MethodEnum.STAKING_SET_CONTROLLER.getMethod().equals(method)
                        || MethodEnum.STAKING_NOMINATE.getMethod().equals(method)
                        || MethodEnum.STAKING_SET_PAYEE.getMethod().equals(method)) {
                        stakingHandler(extrinsicsDTO, block, txList);
                    }

                    List<EventDTO> eventDTOList = extrinsicsDTO.getEvents().stream()
                            .filter(event -> MethodEnum.isSupport(event.getMethod()))
                            .collect(Collectors.toList());

                    String signer = extrinsicsDTO.getSignature().getSigner();
                    String fromAddress = StringUtils.EMPTY;
                    if (JSON.isValidObject(signer)){
                        fromAddress = JSON.parseObject(signer).getString("id");
                    }else {
                        fromAddress = signer;
                    }


                    for (int i = 0; i < eventDTOList.size(); i++) {
                        EventDTO eventDTO = eventDTOList.get(i);
                        MethodDTO methodDTO = eventDTO.getMethod();

                        MethodEnum methodEnum = MethodEnum.valueOf(methodDTO);

                        BigDecimal fee = BigDecimal.ZERO;
                        if (Objects.nonNull(extrinsicsDTO.getInfo()) && null != extrinsicsDTO.getInfo().getPartialFee()) {
                            fee = DataUtil.formatAmount(extrinsicsDTO.getInfo().getPartialFee(), CoinEnum.dot.decimals());
                        }

                        MethodEnum.EventArgsDTO eventArgsDTO = methodEnum.parseArgs(eventDTO.getData());

                        DotTx tx = new DotTx();
                        tx.setHash(extrinsicsDTO.getHash());
                        tx.setBlockHash(block.getHash());
                        tx.setBlockHeight(block.getHeight());
                        tx.setFromAddr(fromAddress);
                        tx.setToAddr(eventArgsDTO.getToAddr());
                        tx.setTimestamp(block.getTimestamp());
                        tx.setAmount(DataUtil.formatAmount(eventArgsDTO.getAmount(), CoinEnum.dot.decimals()));
                        tx.setAssetShortName(CoinEnum.dot.name());
                        tx.setMainCoin(blockchainConfig.getMainAsset());
                        tx.setFee(fee);
                        tx.setGasPrice(BigDecimal.ZERO);
                        tx.setGasLimit(BigDecimal.ZERO);
                        tx.setTxNum(i);
                        // 自定义交易id，一般没有子交易就tx hash就可以，如果有子交易，则带上一个子交易的id，txHash_id
                        tx.setTxId(tx.getHash() + "_" + i);
                        tx.setState(extrinsicsDTO.getSuccess() ? 1 : 0);
                        tx.setType(method);
                        tx.setStatus(1);
                        tx.setBlockState((short) 1);
                        tx.setComment("");
                        tx.setFeeType(FeeType.NO_RECORD_FEE);
                        tx.setNonce(extrinsicsDTO.getNonce());
                        tx.setTip(DataUtil.formatAmount(extrinsicsDTO.getTip(), CoinEnum.dot.decimals()));
                        txList.add(tx);
                    }

                    return txList;
                }).flatMap(Collection::stream).collect(Collectors.toList());

    }

    private void stakingHandler(ExtrinsicsDTO extrinsicsDTO, Block block, List<DotTx> txList) {
        MethodDTO methodDTO = extrinsicsDTO.getMethod();
        MethodEnum methodEnum = MethodEnum.valueOf(methodDTO);


        String signer = extrinsicsDTO.getSignature().getSigner();
        String fromAddress = StringUtils.EMPTY;
        if (JSON.isValidObject(signer)){
            fromAddress = JSON.parseObject(signer).getString("id");
        }else {
            fromAddress = signer;
        }

        BigDecimal fee = BigDecimal.ZERO;
        if (Objects.nonNull(extrinsicsDTO.getInfo()) && null != extrinsicsDTO.getInfo().getPartialFee()) {
            fee = DataUtil.formatAmount(extrinsicsDTO.getInfo().getPartialFee(), CoinEnum.dot.decimals());
        }


        DotTx tx = new DotTx();
        tx.setHash(extrinsicsDTO.getHash());
        tx.setBlockHash(block.getHash());
        tx.setBlockHeight(block.getHeight());
        tx.setFromAddr(fromAddress);
        tx.setTimestamp(block.getTimestamp());
        tx.setAssetShortName(CoinEnum.dot.name());
        tx.setMainCoin(blockchainConfig.getMainAsset());
        tx.setFee(fee);
        tx.setGasPrice(BigDecimal.ZERO);
        tx.setGasLimit(BigDecimal.ZERO);
        tx.setTxNum(0);
        // 自定义交易id，一般没有子交易就tx hash就可以，如果有子交易，则带上一个子交易的id，txHash_id
        tx.setTxId(tx.getHash());
        tx.setState(extrinsicsDTO.getSuccess() ? 1 : 0);
        tx.setType(methodEnum.getMethod());
        tx.setStatus(1);
        tx.setBlockState((short) 1);
        tx.setComment("");
        tx.setFeeType(FeeType.NO_RECORD_FEE);
        tx.setNonce(extrinsicsDTO.getNonce());
        tx.setTip(DataUtil.formatAmount(extrinsicsDTO.getTip(), CoinEnum.dot.decimals()));
        txList.add(tx);
    }

    /**
     * 根据区块高度获取hash
     *
     * @param blockHeight
     * @return
     */
    @Override
    public String getBlockHash(Integer blockHeight) {
        return dotSidecarClient.blocks(blockHeight).getHash();
    }

    /**
     * 获取交易池交易数量
     *
     * @return
     */
    @Override
    public Integer getMemPoolTxCount() {
        return dotSidecarClient.transactionPool().getPool().size();
    }

    /**
     * 获取地址余额
     *
     * @param address
     * @return
     */
    @Override
    public BigDecimal getAddressBalance(String address) {
        BalanceInfoResp resp = this.dotSidecarClient.balanceInfo(address);
        return DataUtil.formatAmountByDecimal(resp.getFree().subtract(resp.getMiscFrozen()), CoinEnum.dot.decimals());
    }

    /**
     * 批量处理交易记录
     * @param txRecordList
     */
    @Override
    public void batchHandlerTxRecord(List<TxRecord> txRecordList) {
        this.txRecordService.batchTxRecord(this.blockchainConfig.getDataBase(),txRecordList);
    }

}
