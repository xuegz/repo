package com.atoken.update.service.impl;

import com.atoken.update.binding.MessageChannelBinding;
import com.atoken.update.dao.AddressDao;
import com.atoken.update.dao.TxDao;
import com.atoken.update.entity.Address;
import com.atoken.update.entity.Block;
import com.atoken.update.entity.TransferMessage;
import com.atoken.update.entity.Tx;
import com.atoken.update.handler.RollBackTxAfterHander;
import com.atoken.update.handler.TxAfterHandler;
import com.atoken.update.service.TxService;
import com.atoken.update.util.AtokenLogger;
import com.atoken.update.util.ConstantUtil;
import com.atoken.update.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service("txService")
public class TxServiceImpl implements TxService, AtokenLogger {

    @Autowired
    @Qualifier(MessageChannelBinding.CHANNEL_TX_INFO)
    protected MessageChannel txInfoChannel;
    @Autowired
    private TxDao txDao;
    @Autowired
    private AddressDao addressDao;
    private TransferMessage transferMessage = new TransferMessage();

    @Override
    public Integer insertTx(String dataBase, Tx tx) {
        return txDao.insertTx(dataBase, tx);
    }

    @Override
    public Integer updateTx(String dataBase, Tx tx) {
        return txDao.updateTx(dataBase, tx);
    }

    @Override
    public Integer updateTxConfirm(String dataBase, String blockHash) {
        return txDao.updateTxConfirm(dataBase, blockHash);
    }

    @Override
    public Integer updateTxUnblock(String dataBase, String blockHash) {
        return txDao.updateTxUnblock(dataBase, blockHash);
    }

    @Override
    public Integer deleteTxByBlockHash(String dataBase, String blockHash) {
        return txDao.deleteTxByBlockHash(dataBase, blockHash);
    }

    protected Address filterAddress(List<Address> addressList, String address) {
        Optional<Address> optionalAddress = addressList.stream().filter(addr -> addr.getAddr().equals(address)).findAny();
        return Optional.empty().equals(optionalAddress) ? null : optionalAddress.get();
    }

    @Override
    @Transactional
    public Integer saveTx(String dataBase, List<Address> addressList, Tx tx, Map<String, BigDecimal> addressBalanceMap, TxAfterHandler txAfterHandler, TransferMessage transferMessage) {


        // 过滤出from地址
        Address fromAddress = this.filterAddress(addressList, tx.getFromAddr());
        // 过滤出to地址
        Address toAddress = this.filterAddress(addressList, tx.getToAddr());
        List<TransferMessage> transferMessages = new ArrayList<>();
        if (Objects.isNull(transferMessage)) {
            transferMessage = new TransferMessage();
        }
        if (Objects.nonNull(fromAddress)) {
            logger.info("地址余额信息：{}", addressBalanceMap.toString());
            // 判断交易是否存在
            Integer existTx = txDao.existTx(dataBase, tx.getHash(), tx.getTxNum());
            VerifyUtil.objectNotNull(existTx, "existTx is null");
            if (tx.isSaveTx()) {
                if (existTx == 1) {
                    // if atoken send tx, update tx
                    Integer row = txDao.updateTx(dataBase, tx);
                    VerifyUtil.numberNotZore(row, "tx update failed");
                    Long id = txDao.selectIdByTxId(dataBase, tx.getTxId());
                    tx.setId(id);
                } else {
                    tx.setPlatType(ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND);
                    // if not atoken send tx, insert tx
                    Integer row = txDao.insertTx(dataBase, tx);
                    VerifyUtil.numberNotZore(row, "tx insert failed");
                }
            }
            // save tx detail table
            this.saveToAddressAsset(dataBase, fromAddress, addressBalanceMap.get(fromAddress.getAddr()), tx.getState());
            if (tx.isSaveTx()) {
                transferMessages.add(transferMessage.toTransferMessage(tx, fromAddress, true));
            }

            // 如果to地址也在我们的库里，则更新
            if (Objects.nonNull(toAddress)) {
                this.saveToAddressAsset(dataBase, toAddress, addressBalanceMap.get(toAddress.getAddr()), tx.getState());
                if (tx.isSaveTx()) {
                    transferMessages.add(transferMessage.toTransferMessage(tx, toAddress, false));
                }
            }
            // 交易后置处理
            if (Objects.nonNull(txAfterHandler)) {
                txAfterHandler.handler(fromAddress, toAddress, tx);
            }
            // 是否需要发送消息
            if (tx.isSaveTx()) {
                // 发送通知消息到rabbitmq
                txInfoChannel.send(MessageBuilder.withPayload(transferMessages).build());
            }
            return 1;
        } else if (Objects.nonNull(toAddress)) {
            logger.info("地址余额信息：{}", addressBalanceMap.toString());
            // to地址在我们库里
            // 判断交易是否存在
            Integer existTx = txDao.existTx(dataBase, tx.getHash(), tx.getTxNum());
            VerifyUtil.objectNotNull(existTx, "existTx is null");
            if (tx.isSaveTx()) {
                if (existTx == 1) {
                    // if atoken send tx, update tx
                    Integer row = txDao.updateTx(dataBase, tx);
                    VerifyUtil.numberNotZore(row, "tx update failed");
                    Long id = txDao.selectIdByTxId(dataBase, tx.getTxId());
                    tx.setId(id);
                } else {
                    tx.setPlatType(ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND);
                    // if not atoken send tx, insert tx
                    Integer row = txDao.insertTx(dataBase, tx);
                    VerifyUtil.numberNotZore(row, "tx insert failed");
                }

            }
            // save tx detail table
            this.saveToAddressAsset(dataBase, toAddress, addressBalanceMap.get(toAddress.getAddr()), tx.getState());
            if (tx.isSaveTx()) {
                transferMessages.add(transferMessage.toTransferMessage(tx, toAddress, false));
            }
            // 交易后置处理
            if (Objects.nonNull(txAfterHandler)) {
                txAfterHandler.handler(fromAddress, toAddress, tx);
            }
            if (tx.isSaveTx()) {
                txInfoChannel.send(MessageBuilder.withPayload(transferMessages).build());
            }
            return 1;
        }
        return 0;
    }

    protected void saveToAddressAsset(String dataBase, Address address, BigDecimal balance, Integer state) {
        Integer row = addressDao.updateAmountByAddress(dataBase, address.getAddr(), balance);
        VerifyUtil.numberNotZore(row, "update address asset failed");
    }

    @Override
    @Transactional
    public void rollBackTx(String dataBase, Block parentBlock, RollBackTxAfterHander rollBackTxAfterHander) {
        List<Tx> txList = txDao.selectTxByBlockHash(dataBase, parentBlock.getHash());
        if (txList.size() > 0) {
            txDao.updateTxUnblock(dataBase, parentBlock.getHash());
            txDao.deleteTxByBlockHash(dataBase, parentBlock.getHash());
            txList.forEach(tx -> {
                List<Address> addressList = addressDao.selectAddressByAddress(dataBase, tx.getFromAddr(), tx.getToAddr());
                Address fromAddress = this.filterAddress(addressList, tx.getFromAddr());
                Address toAddress = this.filterAddress(addressList, tx.getToAddr());

                if (Objects.nonNull(rollBackTxAfterHander)) {
                    rollBackTxAfterHander.handler(fromAddress, toAddress);
                }
//                if (Objects.nonNull(fromAddress)) {
//                    AddressAsset addressAsset = AddressAsset.txToAddressAssetForRollBack(dataBase, tx, true);
//                    Integer row = addressAssetDao.updateAddressAsset(dataBase, addressAsset);
//                    VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
//                }

//                if (Objects.nonNull(toAddress)) {
//                    AddressAsset addressAsset = AddressAsset.txToAddressAssetForRollBack(dataBase, tx, false);
//                    Integer row = addressAssetDao.updateAddressAsset(dataBase, addressAsset);
//                    VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
//                }
            });
        }
    }

    @Override
    public List<Tx> selectTxBtBlockHash(String dataBase, String blockHash) {
        return txDao.selectTxByBlockHash(dataBase, blockHash);
    }

    @Override
    public Integer existTx(String dataBase, String txHash, Integer txNum) {
        return txDao.existTx(dataBase, txHash, txNum);
    }
}
