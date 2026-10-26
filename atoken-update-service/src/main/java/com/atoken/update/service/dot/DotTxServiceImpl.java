package com.atoken.update.service.dot;

import com.atoken.update.dao.dot.DotTxDao;
import com.atoken.update.entity.Address;
import com.atoken.update.entity.TransferMessage;
import com.atoken.update.entity.Tx;
import com.atoken.update.entity.dot.DotTx;
import com.atoken.update.handler.TxAfterHandler;
import com.atoken.update.service.impl.TxServiceImpl;
import com.atoken.update.util.ConstantUtil;
import com.atoken.update.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service("dotTxService")
public class DotTxServiceImpl extends TxServiceImpl {

    @Autowired
    private DotTxDao dotTxDao;

    @Override
    @Transactional
    public Integer saveTx(String dataBase, List<Address> addressList, Tx tx, Map<String, BigDecimal> addressBalanceMap, TxAfterHandler txAfterHandler, TransferMessage transferMessage) {

        DotTx dotTx = (DotTx) tx;
        
        // 过滤出from地址
        Address fromAddress = this.filterAddress(addressList, dotTx.getFromAddr());
        // 过滤出to地址
        Address toAddress = this.filterAddress(addressList, dotTx.getToAddr());
        List<TransferMessage> transferMessages = new ArrayList<>();
        if (Objects.isNull(transferMessage)) {
            transferMessage = new TransferMessage();
        }
        if (Objects.nonNull(fromAddress)) {
            logger.info("地址余额信息：{}", addressBalanceMap.toString());
            // 判断交易是否存在
            Integer existTx = dotTxDao.existTx(dataBase, dotTx.getHash(), dotTx.getTxNum());
            VerifyUtil.objectNotNull(existTx, "existTx is null");
            if (dotTx.isSaveTx()) {
                if (existTx == 1) {
                    // if atoken send tx, update tx
                    Integer row = dotTxDao.updateTx(dataBase,dotTx);
                    VerifyUtil.numberNotZore(row, "tx update failed");
                    Long id = dotTxDao.selectIdByTxId(dataBase, dotTx.getTxId());
                    dotTx.setId(id);
                } else {
                    dotTx.setPlatType(ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND);
                    // if not atoken send tx, insert tx
                    Integer row = dotTxDao.insertTx(dataBase,dotTx);
                    VerifyUtil.numberNotZore(row, "tx insert failed");
                }
            }
            // save tx detail table
            this.saveToAddressAsset(dataBase, fromAddress, addressBalanceMap.get(fromAddress.getAddr()), dotTx.getState());
            if (dotTx.isSaveTx()) {
                transferMessages.add(transferMessage.toTransferMessage(tx, fromAddress, true));
            }

            // 如果to地址也在我们的库里，则更新
            if (Objects.nonNull(toAddress)) {
                this.saveToAddressAsset(dataBase, toAddress, addressBalanceMap.get(toAddress.getAddr()), dotTx.getState());
                if (dotTx.isSaveTx()) {
                    transferMessages.add(transferMessage.toTransferMessage(tx, toAddress, false));
                }
            }
            // 交易后置处理
            if (Objects.nonNull(txAfterHandler)) {
                txAfterHandler.handler(fromAddress, toAddress,dotTx);
            }
            // 是否需要发送消息
            if (dotTx.isSaveTx()) {
                // 发送通知消息到rabbitmq
                txInfoChannel.send(MessageBuilder.withPayload(transferMessages).build());
            }
            return 1;
        } else if (Objects.nonNull(toAddress)) {
            logger.info("地址余额信息：{}", addressBalanceMap.toString());
            // to地址在我们库里
            // 判断交易是否存在
            Integer existTx = dotTxDao.existTx(dataBase, dotTx.getHash(), dotTx.getTxNum());
            VerifyUtil.objectNotNull(existTx, "existTx is null");
            if (dotTx.isSaveTx()) {
                if (existTx == 1) {
                    // if atoken send tx, update tx
                    Integer row = dotTxDao.updateTx(dataBase,dotTx);
                    VerifyUtil.numberNotZore(row, "tx update failed");
                    Long id = dotTxDao.selectIdByTxId(dataBase, dotTx.getTxId());
                    dotTx.setId(id);
                } else {
                    dotTx.setPlatType(ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND);
                    // if not atoken send tx, insert tx
                    Integer row = dotTxDao.insertTx(dataBase,dotTx);
                    VerifyUtil.numberNotZore(row, "tx insert failed");
                }

            }
            // save tx detail table
            this.saveToAddressAsset(dataBase, toAddress, addressBalanceMap.get(toAddress.getAddr()), dotTx.getState());
            if (dotTx.isSaveTx()) {
                transferMessages.add(transferMessage.toTransferMessage(tx, toAddress, false));
            }
            // 交易后置处理
            if (Objects.nonNull(txAfterHandler)) {
                txAfterHandler.handler(fromAddress, toAddress,dotTx);
            }
            if (dotTx.isSaveTx()) {
                txInfoChannel.send(MessageBuilder.withPayload(transferMessages).build());
            }
            return 1;
        }
        return 0;
    }


}
