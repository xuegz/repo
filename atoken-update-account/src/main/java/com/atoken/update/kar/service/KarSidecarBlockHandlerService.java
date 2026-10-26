package com.atoken.update.kar.service;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.*;
import com.atoken.blockchain.http.request.account.dot.sidecar.enums.MethodEnum;
import com.atoken.blockchain.http.request.account.kar.sidecar.KarSidecarClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.kar.KarTx;
import com.atoken.update.enums.FeeType;
import com.atoken.update.kar.config.KarBlockchainConfig;
import com.atoken.update.notification.DingdingNotification;
import com.atoken.update.service.TxRecordService;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.util.LoggerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j
@Service("karSidecarBlockHandlerService")
public class KarSidecarBlockHandlerService implements BlockHandlerService {

    @Autowired
    private KarSidecarClient karSidecarClient;

    @Autowired
    private KarBlockchainConfig blockchainConfig;

    @Autowired
    private DingdingNotification dingdingNotification;

    @Value("${atoken.notification.duration: 3600}")
    private Long notificationSendDuration;

    @Resource(name = "txRecordService")
    private TxRecordService txRecordService;

    private static Long notificationSendTime = 0L;

    @Override
    public Integer getBlockNumber() {
        return this.karSidecarClient.blocksHead().getNumber();
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
                BlockResp blockResp = this.karSidecarClient.blocks(height);

                Long timestamp = Long.parseLong(blockResp.getExtrinsics().get(0).getArgs().get("now")) / 1000;

                long count = 0;
                List<ExtrinsicsDTO> list = blockResp.getExtrinsics();
                for (ExtrinsicsDTO extrinsicsDTO : list) {
                    long tmpCount = extrinsicsDTO.getEvents().stream().filter(eventDTO->  MethodEnum.isSupport(eventDTO.getMethod())).count();
                    if (tmpCount > 0) {
                        count++;
                    }
                }

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
                log.error("karSidecarClient.blocks error height:{}", height);
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
    public List<KarTx> getBlockTx(Block block) {

        BlockResp blockResp = this.karSidecarClient.blocks(block.getHash());

        long count = blockResp.getExtrinsics().stream()
                .map(extrinsicsDTO -> {
                    List<EventDTO> eventDTOList = extrinsicsDTO.getEvents().stream()
                            .filter(eventDTO -> MethodEnum.isSupport(eventDTO.getMethod()))
                            .collect(Collectors.toList());
                    return eventDTOList.size();
                }).count();

        if (count <= 0) {
            return null;
        }

        return blockResp.getExtrinsics().parallelStream()
                .filter(extrinsicsDTO -> Objects.nonNull(extrinsicsDTO.getSignature()))
                .map(extrinsicsDTO -> {

                    List<KarTx> txList = new ArrayList<>();

                    List<EventDTO> eventDTOList = extrinsicsDTO.getEvents().stream()
                            .filter(event -> MethodEnum.isSupport(event.getMethod()))
                            .collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(eventDTOList)) {
                        KarTx tx = new KarTx();

                        String fromAddress = extrinsicsDTO.getSignature().getSigner();
                        tx.setFromAddr(fromAddress);
                        tx.setToAddr(fromAddress);
                        // 只更新地址余额，不更新交易入库
                        tx.setSaveTx(false);
                        txList.add(tx);
                        return txList;
                    }

                    for (int i = 0; i < eventDTOList.size(); i++) {

                        EventDTO eventDTO = eventDTOList.get(i);

                        MethodDTO methodDTO = eventDTO.getMethod();

                        MethodEnum methodEnum = MethodEnum.valueOf(methodDTO);

                        BigDecimal fee = BigDecimal.ZERO;
                        if (Objects.nonNull(extrinsicsDTO.getInfo()) && null != extrinsicsDTO.getInfo().getPartialFee()) {
                            fee = DataUtil.formatAmountByDecimal(extrinsicsDTO.getInfo().getPartialFee(), CoinEnum.kar.decimals());
                        }

                        MethodEnum.EventArgsDTO eventArgsDTO = methodEnum.parseArgs(eventDTO.getData());

                        KarTx tx = new KarTx();
                        tx.setHash(extrinsicsDTO.getHash());
                        tx.setBlockHash(block.getHash());
                        tx.setBlockHeight(block.getHeight());
                        tx.setFromAddr(eventArgsDTO.getFromAddr());
                        tx.setToAddr(eventArgsDTO.getToAddr());
                        tx.setTimestamp(block.getTimestamp());
                        tx.setAmount(DataUtil.formatAmountByDecimal(eventArgsDTO.getAmount(), CoinEnum.kar.decimals()));
                        tx.setAssetShortName(CoinEnum.kar.name());
                        tx.setMainCoin(blockchainConfig.getMainAsset());
                        tx.setFee(fee);
                        tx.setGasPrice(BigDecimal.ZERO);
                        tx.setGasLimit(BigDecimal.ZERO);
                        tx.setTxNum(i);
                        // 自定义交易id，一般没有子交易就tx hash就可以，如果有子交易，则带上一个子交易的id，txHash_id
                        tx.setTxId(tx.getHash() + "_" + i);
                        tx.setState(extrinsicsDTO.getSuccess() ? 1 : 0);
                        tx.setType(methodEnum.getMethod());
                        tx.setStatus(1);
                        tx.setBlockState((short) 1);
                        tx.setComment("");
                        tx.setFeeType(FeeType.NO_RECORD_FEE);
                        tx.setNonce(extrinsicsDTO.getNonce());
                        tx.setTip(DataUtil.formatAmountByDecimal(extrinsicsDTO.getTip(), CoinEnum.kar.decimals()));
                        txList.add(tx);
                    }

                    return txList;
                }).flatMap(Collection::stream).collect(Collectors.toList());

    }

    /**
     * 根据区块高度获取hash
     *
     * @param blockHeight
     * @return
     */
    @Override
    public String getBlockHash(Integer blockHeight) {
        return this.karSidecarClient.blocks(blockHeight).getHash();
    }

    /**
     * 获取交易池交易数量
     *
     * @return
     */
    @Override
    public Integer getMemPoolTxCount() {
        return this.karSidecarClient.transactionPool().getPool().size();
    }

    /**
     * 获取地址余额
     *
     * @param address
     * @return
     */
    @Override
    public BigDecimal getAddressBalance(String address) {
        return DataUtil.formatAmountByDecimal(this.karSidecarClient.balanceInfo(address).getFree(), CoinEnum.kar.decimals());
    }

    @Override
    public void batchHandlerTxRecord(List<TxRecord> txRecordList) {
        this.txRecordService.batchTxRecord(CoinEnum.kar.name(), txRecordList);
    }

    @Override
    public BigDecimal getAddressFreeBalance(String address) {
        BalanceInfoResp resp = this.karSidecarClient.balanceInfo(address);
        return DataUtil.formatAmountByDecimal(resp.getFree().subtract(resp.getMiscFrozen()), CoinEnum.kar.decimals());
    }
}
