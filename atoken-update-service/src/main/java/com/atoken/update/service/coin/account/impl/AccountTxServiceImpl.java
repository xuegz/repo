package com.atoken.update.service.coin.account.impl;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.account.kar.subscan.ExtrinsicData;
import com.atoken.blockchain.http.request.account.kar.subscan.ExtrinsicResponse;
import com.atoken.blockchain.http.request.account.kar.subscan.Extrinsics;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import com.atoken.update.binding.MessageChannelBinding;
import com.atoken.update.dao.coin.account.AccountAddressDao;
import com.atoken.update.dao.coin.account.AccountTxDao;
import com.atoken.update.entity.coin.account.Address;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.TransferMessage;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;
import com.atoken.update.handler.coin.account.TxAfterHandler;
import com.atoken.update.service.coin.account.AccountTxService;
import com.atoken.update.util.AtokenLogger;
import com.atoken.update.util.ConstantUtil;
import com.atoken.update.util.VerifyUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AccountTxServiceImpl implements AccountTxService, AtokenLogger {
    @Autowired
    @Qualifier(MessageChannelBinding.CHANNEL_TX_INFO)
    private MessageChannel txInfoChannel;
    @Autowired
    private AccountTxDao txDao;
    @Autowired
    private AccountAddressDao addressDao;
    private TransferMessage transferMessage = new TransferMessage();

    @Value("${blockchain.kar.subscan-url}")
    private String karSubScanUrl;

    private static final String KAR_GET_FEE_API_PREFIX = "/api/scan/block";

    @Override
    public Integer insertTx(String coin, Tx tx) {
        return txDao.insertTx(coin, tx);
    }

    @Override
    public Integer updateTx(String coin, Tx tx) {
        return txDao.updateTx(coin, tx);
    }

    @Override
    public Integer updateTxConfirm(String coin, String blockHash) {
        return txDao.updateTxConfirm(coin, blockHash);
    }

    @Override
    public Integer updateTxUnblock(String coin, String blockHash) {
        return txDao.updateTxUnblock(coin, blockHash);
    }

    @Override
    public Integer deleteTxByBlockHash(String coin, String blockHash) {
        return txDao.deleteTxByBlockHash(coin, blockHash);
    }

    private Address filterAddress(List<Address> addressList, String address) {
        Optional<Address> optionalAddress = addressList.stream().filter(addr -> addr.getAddr().equals(address)).findAny();
        return Optional.empty().equals(optionalAddress) ? null : optionalAddress.get();
    }

    @Override
    @Transactional
    public Integer saveTx(String coin, List<Address> addressList, Tx tx, Map<String, BigDecimal> addressBalanceMap, TxAfterHandler txAfterHandler, TransferMessage transferMessage) {
        // 过滤出from地址
        Address fromAddress = this.filterAddress(addressList, tx.getFromAddr());
        // 过滤出to地址
        Address toAddress = this.filterAddress(addressList, tx.getToAddr());
        List<TransferMessage> transferMessages = new ArrayList<>();
        if (Objects.isNull(transferMessage)) {
            transferMessage = new TransferMessage();
        }
        if (Objects.nonNull(fromAddress)) {
            //kar 单独处理fee
            if (tx.getMainCoin().equalsIgnoreCase(CoinEnum.kar.name())) {
                tx.setFee(calFee(tx.getBlockHash(), fromAddress.getAddr(), tx.getTip()));
            }
            logger.info("地址余额信息：{}", addressBalanceMap.toString());
            // 判断交易是否存在
            Integer existTx = txDao.existTx(coin, tx.getHash(), tx.getTxNum());
            VerifyUtil.objectNotNull(existTx, "existTx is null");
            if (tx.isSaveTx()) {
                if (existTx == 1) {
                    // if atoken send tx, update tx
                    Integer row = txDao.updateTx(coin, tx);
                    VerifyUtil.numberNotZore(row, "tx update failed");
                    Long id = txDao.selectIdByTxId(coin, tx.getTxId());
                    tx.setId(id);
                } else {
                    tx.setPlatType(ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND);
                    // if not atoken send tx, insert tx
                    Integer row = txDao.insertTx(coin, tx);
                    logger.info("{} save tx result:{},tx:{}", coin, row, JSON.toJSONString(tx));
                    VerifyUtil.numberNotZore(row, "tx insert failed");
                }
            }
            // save tx detail table
            this.saveToAddressAsset(coin, fromAddress, addressBalanceMap.get(fromAddress.getAddr()), tx.getState());
            if (tx.isSendMessage()) {
                transferMessages.add(transferMessage.toTransferMessage(tx, fromAddress, true));
            }

            // 如果to地址也在我们的库里，则更新
            if (Objects.nonNull(toAddress)) {
                this.saveToAddressAsset(coin, toAddress, addressBalanceMap.get(toAddress.getAddr()), tx.getState());
                if (tx.isSendMessage()) {
                    transferMessages.add(transferMessage.toTransferMessage(tx, toAddress, false));
                }
            }
            // 交易后置处理
            if (Objects.nonNull(txAfterHandler)) {
                txAfterHandler.handler(fromAddress, toAddress, tx);
            }
            // 是否需要发送消息
            if (tx.isSendMessage()) {
                // 发送通知消息到rabbitmq
                txInfoChannel.send(MessageBuilder.withPayload(transferMessages).build());
            }
            return 1;
        } else if (Objects.nonNull(toAddress)) {
            //kar 单独处理fee
            if (tx.getMainCoin().equalsIgnoreCase(CoinEnum.kar.name())) {
                tx.setFee(calFee(tx.getBlockHash(), toAddress.getAddr(), tx.getTip()));
            }
            logger.info("地址余额信息：{}", addressBalanceMap.toString());
            // to地址在我们库里
            // 判断交易是否存在
            Integer existTx = txDao.existTx(coin, tx.getHash(), tx.getTxNum());
            VerifyUtil.objectNotNull(existTx, "existTx is null");
            if (tx.isSaveTx()) {
                if (existTx == 1) {
                    // if atoken send tx, update tx
                    Integer row = txDao.updateTx(coin, tx);
                    VerifyUtil.numberNotZore(row, "tx update failed");
                    Long id = txDao.selectIdByTxId(coin, tx.getTxId());
                    tx.setId(id);
                } else {
                    tx.setPlatType(ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND);
                    // if not atoken send tx, insert tx
                    Integer row = txDao.insertTx(coin, tx);
                    VerifyUtil.numberNotZore(row, "tx insert failed");
                }

            }
            // save tx detail table
            this.saveToAddressAsset(coin, toAddress, addressBalanceMap.get(toAddress.getAddr()), tx.getState());
            if (tx.isSendMessage()) {
                transferMessages.add(transferMessage.toTransferMessage(tx, toAddress, false));
            }
            // 交易后置处理
            if (Objects.nonNull(txAfterHandler)) {
                txAfterHandler.handler(fromAddress, toAddress, tx);
            }
            if (tx.isSendMessage()) {
                // 发送通知消息到rabbitmq
                txInfoChannel.send(MessageBuilder.withPayload(transferMessages).build());
            }
            return 1;
        }
        return 0;
    }

    private void saveToAddressAsset(String coin, Address address, BigDecimal balance, Integer state) {
        logger.info("{} save addr to asset {},balance {}", coin, address.getAddr(), balance);
        Integer row = addressDao.updateAmountByAddress(coin, address.getAddr(), balance);
        VerifyUtil.numberNotZore(row, "update address asset failed");
    }

    @Override
    @Transactional
    public void rollBackTx(String coin, Block parentBlock, RollBackTxAfterHander rollBackTxAfterHander) {
        List<Tx> txList = txDao.selectTxByBlockHash(coin, parentBlock.getHash());
        if (txList.size() > 0) {
            txDao.updateTxUnblock(coin, parentBlock.getHash());
            txDao.deleteTxByBlockHash(coin, parentBlock.getHash());
            txList.forEach(tx -> {
                List<Address> addressList = addressDao.selectAddressByAddress(coin, tx.getFromAddr(), tx.getToAddr());
                Address fromAddress = this.filterAddress(addressList, tx.getFromAddr());
                Address toAddress = this.filterAddress(addressList, tx.getToAddr());

                if (Objects.nonNull(rollBackTxAfterHander)) {
                    rollBackTxAfterHander.handler(fromAddress, toAddress);
                }
//                if (Objects.nonNull(fromAddress)) {
//                    AddressAsset addressAsset = AddressAsset.txToAddressAssetForRollBack(coin, tx, true);
//                    Integer row = addressAssetDao.updateAddressAsset(coin, addressAsset);
//                    VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
//                }

//                if (Objects.nonNull(toAddress)) {
//                    AddressAsset addressAsset = AddressAsset.txToAddressAssetForRollBack(coin, tx, false);
//                    Integer row = addressAssetDao.updateAddressAsset(coin, addressAsset);
//                    VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
//                }
            });
        }
    }

    @Override
    public List<Tx> selectTxBtBlockHash(String coin, String blockHash) {
        return txDao.selectTxByBlockHash(coin, blockHash);
    }

    @Override
    public Integer existTx(String coin, String txHash, Integer txNum) {
        return txDao.existTx(coin, txHash, txNum);
    }

    private BigDecimal calFee(String blockHash, String addr, BigDecimal tip) {
        BigDecimal fee = BigDecimal.ZERO;

        try {
            Request request = new Request();
            Map<String, Object> params = Maps.newHashMap();
            params.put("block_hash", blockHash);
            String header[] = {"User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56"};
            ExtrinsicResponse extrinsicResponse = request.url(karSubScanUrl.concat(KAR_GET_FEE_API_PREFIX))
                    .setContentType(ContentType.JSON)
                    .method(Method.POST)
                    .data(params)
                    .headers(header)
                    .send(ExtrinsicResponse.class);
            if (Objects.nonNull(extrinsicResponse) && Objects.nonNull(extrinsicResponse.getData())) {
                ExtrinsicData extrinsicData = extrinsicResponse.getData();
                List<Extrinsics> extrinsicsList = extrinsicData.getExtrinsics();
                if (!CollectionUtils.isEmpty(extrinsicsList)) {
                    Optional<Extrinsics> optionalExtrinsics = extrinsicsList.stream().filter(extrinsics -> extrinsics.getAccount_id().equals(addr)).findFirst();
                    fee = new BigDecimal(optionalExtrinsics.get().getFee()).divide(new BigDecimal(Math.pow(10, 12))).setScale(10, BigDecimal.ROUND_HALF_UP);
                }
            }
            fee = fee.add(tip);
        } catch (Exception e) {
            logger.error("calFee, addr:{}", addr, e);
        }
        return fee;
    }
}
