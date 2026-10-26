package com.atoken.update.service.ont.impl;

import com.atoken.update.binding.MessageChannelBinding;
import com.atoken.update.dao.ont.*;
import com.atoken.update.entity.ont.*;
import com.atoken.update.service.ont.OntTxService;
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

@Service
public class OntTxServiceImpl implements OntTxService {
    @Autowired
    @Qualifier(MessageChannelBinding.CHANNEL_TX_INFO)
    private MessageChannel txInfoChannel;
    @Autowired
    private OntTxDao ontTxDao;
    @Autowired
    private OntAddressAssetDao ontAddressAssetDao;
    @Autowired
    private OntAddressDao ontAddressDao;
    @Autowired
    private OntTokenDao ontTokenDao;

    @Override
    public Integer insertTx(Tx tx) {
        return ontTxDao.insertTx(tx);
    }

    @Override
    public Integer updateTx(Tx tx) {
        return ontTxDao.updateTx(tx);
    }

    @Override
    public Integer updateTxConfirm(String blockHash) {
        return ontTxDao.updateTxConfirm(blockHash);
    }

    @Override
    public Integer updateTxUnblock(String blockHash) {
        return ontTxDao.updateTxUnblock(blockHash);
    }

    @Override
    public Integer deleteTxByBlockHash(String blockHash) {
        return ontTxDao.deleteTxByBlockHash(blockHash);
    }

    private Address filterAddress(List<Address> addressList, String address){
        Optional<Address> optionalAddress = addressList.stream().filter(addr -> addr.getAddr().equals(address)).findAny();
        return Optional.empty().equals(optionalAddress)? null: optionalAddress.get();
    }

    @Override
    @Transactional
    public Integer saveTx(List<Address> addressList, Tx tx) {
        Address fromAddress = this.filterAddress(addressList, tx.getFromAddr());
        Address toAddress = this.filterAddress(addressList, tx.getToAddr());
        // 获取主网币种信息
        Token mainCoin = ontTokenDao.selectTokenByAssetType(ConstantUtil.ASSERT_TYPE_MAIN);

        List<TransferMessage> transferMessages = new ArrayList<>();
        if (Objects.nonNull(fromAddress)) {
            Integer existTx = ontTxDao.existTx(tx.getHash(), tx.getTxNum());
            VerifyUtil.objectNotNull(existTx, "existTx is null");
            if (existTx == 1) {
                // if atoken send tx, update tx
                Integer row = ontTxDao.updateTx(tx);
                VerifyUtil.numberNotZore(row, "tx update failed");
                Long id = ontTxDao.selectIdByTxId(tx.getTxId());
                tx.setId(id);
            } else {
                tx.setPlatType(ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND);
                // if not atoken send tx, insert tx
                Integer row = ontTxDao.insertTx(tx);
                VerifyUtil.numberNotZore(row, "tx insert failed");
            }
            // save tx detail table
            this.saveToAddressAsset(tx, fromAddress, true);
            transferMessages.add(TransferMessage.toTransferMessage(tx, fromAddress, true, mainCoin));

            if (Objects.nonNull(toAddress)) {
                this.saveToAddressAsset(tx, toAddress, false);
                transferMessages.add(TransferMessage.toTransferMessage(tx, toAddress, false, mainCoin));
            }
            txInfoChannel.send(MessageBuilder.withPayload(transferMessages).build());
            return 1;
        } else if (Objects.nonNull(toAddress)) {
            Integer existTx = ontTxDao.existTx(tx.getHash(), tx.getTxNum());
            VerifyUtil.objectNotNull(existTx, "existTx is null");
            if(existTx == 1) {
                // if atoken send tx, update tx
                Integer row = ontTxDao.updateTx(tx);
                VerifyUtil.numberNotZore(row, "tx update failed");
                Long id = ontTxDao.selectIdByTxId(tx.getTxId());
                tx.setId(id);
            }else {
                tx.setPlatType(ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND);
                // if not atoken send tx, insert tx
                Integer row = ontTxDao.insertTx(tx);
                VerifyUtil.numberNotZore(row, "tx insert failed");
            }

            // save tx detail table
            this.saveToAddressAsset(tx, toAddress, false);
            transferMessages.add(TransferMessage.toTransferMessage(tx, toAddress, false, mainCoin));
            txInfoChannel.send(MessageBuilder.withPayload(transferMessages).build());
            return 1;
        }
        return 0;
    }

    @Override
    @Transactional
    public Integer saveTx(List<Address> addressList, Tx tx, Map<String, Map<String, BigDecimal>> addressBalanceMap) {
        // 过滤出from地址
        Address fromAddress = this.filterAddress(addressList, tx.getFromAddr());
        // 过滤出to地址
        Address toAddress = this.filterAddress(addressList, tx.getToAddr());
        // 获取主网币种信息
        Token mainCoin = ontTokenDao.selectTokenByAssetType(ConstantUtil.ASSERT_TYPE_MAIN);

        List<TransferMessage> transferMessages = new ArrayList<>();
        // 如果from地址在我们库里
        if (Objects.nonNull(fromAddress)) {
            // 判断交易是否存在
            Integer existTx = ontTxDao.existTx(tx.getHash(), tx.getTxNum());
            VerifyUtil.objectNotNull(existTx, "existTx is null");
            if (existTx == 1) {
                // if atoken send tx, update tx
                Integer row = ontTxDao.updateTx(tx);
                VerifyUtil.numberNotZore(row, "tx update failed");
                Long id = ontTxDao.selectIdByTxId(tx.getTxId());
                tx.setId(id);
            } else {
                tx.setPlatType(ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND);
                // if not atoken send tx, insert tx
                Integer row = ontTxDao.insertTx(tx);
                VerifyUtil.numberNotZore(row, "tx insert failed");
            }
            // save tx detail table
//            this.saveToAddressAsset(tx, fromAddress, true);
            this.saveToAddressAsset(fromAddress, addressBalanceMap.get(fromAddress.getAddr()).get(tx.getContractAddr()), tx.getContractAddr(), tx.getState());
            transferMessages.add(TransferMessage.toTransferMessage(tx, fromAddress, true, mainCoin));

            // 如果to地址也在我们的库里，则更新
            if (Objects.nonNull(toAddress)) {
                this.saveToAddressAsset(toAddress, addressBalanceMap.get(toAddress.getAddr()).get(tx.getContractAddr()), tx.getContractAddr(), tx.getState());
                transferMessages.add(TransferMessage.toTransferMessage(tx, toAddress, false, mainCoin));
            }
            // 发送通知消息到rabbitmq
            txInfoChannel.send(MessageBuilder.withPayload(transferMessages).build());
            return 1;

        } else if (Objects.nonNull(toAddress)) {
            // to地址在我们库里
            // 判断交易是否存在
            Integer existTx = ontTxDao.existTx(tx.getHash(), tx.getTxNum());
            VerifyUtil.objectNotNull(existTx, "existTx is null");
            if(existTx == 1) {
                // if atoken send tx, update tx
                Integer row = ontTxDao.updateTx(tx);
                VerifyUtil.numberNotZore(row, "tx update failed");
                Long id = ontTxDao.selectIdByTxId(tx.getTxId());
                tx.setId(id);
            }else {
                tx.setPlatType(ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND);
                // if not atoken send tx, insert tx
                Integer row = ontTxDao.insertTx(tx);
                VerifyUtil.numberNotZore(row, "tx insert failed");
            }

            // save tx detail table
//            this.saveToAddressAsset(tx, toAddress, false);
            this.saveToAddressAsset(toAddress, addressBalanceMap.get(toAddress.getAddr()).get(tx.getContractAddr()), tx.getContractAddr(), tx.getState());

            // 发送通知消息到rabbitmq
            transferMessages.add(TransferMessage.toTransferMessage(tx, toAddress, false, mainCoin));
            txInfoChannel.send(MessageBuilder.withPayload(transferMessages).build());
            return 1;
        }
        return 0;
    }

    private void saveToAddressAsset(Address address, BigDecimal balance, String contractAddr, Integer state) {
        // 查询资产是否存在
        Integer existAddressAsset = ontAddressAssetDao.existAddressAsset(address.getAddr(), contractAddr);
        VerifyUtil.objectNotNull(existAddressAsset, "existAddressAsset is null");

        // 如果资产不存在则插入
        if (existAddressAsset == 0) {
            AddressAsset addressAsset = new AddressAsset();
            addressAsset.setContractAddr(contractAddr);
            addressAsset.setAddr(address.getAddr());
            addressAsset.setAssetCode(null);
            addressAsset.setWalletId(address.getWalletId());
            Integer row = ontAddressAssetDao.insertAddressAsset(addressAsset);
            VerifyUtil.numberNotZore(row, "insert address asset failed");
        }

        // 更新资产信息
        if(state == 1) {
            AddressAsset addressAsset = new AddressAsset();
            addressAsset.setAddr(address.getAddr());
            addressAsset.setContractAddr(contractAddr);
            addressAsset.setAmount(balance);
            Integer row = ontAddressAssetDao.updateAsset(addressAsset);
            VerifyUtil.numberNotZore(row, "update address asset failed");
        }
    }

    private void saveToAddressAsset(Tx tx, Address address, boolean isFrom) {
        // save tx detail table
//        TxDetail txDetail = TxDetail.txToTxDetail(tx, isFrom, address);
//        Integer row = txDetailDao.insertTxDetail(txDetail);
//        VerifyUtil.numberNotZore(row, "insert address tx detail failed");
        // 查询资产是否存在
        Integer existAddressAsset = ontAddressAssetDao.existAddressAsset(address.getAddr(), tx.getContractAddr());
        VerifyUtil.objectNotNull(existAddressAsset, "existAddressAsset is null");

        // 如果资产不存在则插入
        if (existAddressAsset == 0) {
            AddressAsset addressAsset = new AddressAsset();
            addressAsset.setContractAddr(tx.getContractAddr());
            addressAsset.setAddr(address.getAddr());
            addressAsset.setAssetCode(null);
            addressAsset.setWalletId(address.getWalletId());
            Integer row = ontAddressAssetDao.insertAddressAsset(addressAsset);
            VerifyUtil.numberNotZore(row, "insert address asset failed");
        }

        // 更新资产信息
        if(tx.getState() == 1) {
            AddressAsset addressAsset = AddressAsset.txToAddressAsset(tx, isFrom);
            Integer row = ontAddressAssetDao.updateAddressAsset(addressAsset);
            VerifyUtil.numberNotZore(row, "update address asset failed");
        }
    }

    @Override
    @Transactional
    public void rollBackTx(Block parentBlock) {
        List<Tx> txList = ontTxDao.selectTxBtBlockHash(parentBlock.getHash());
        if (txList.size() > 0) {
            ontTxDao.updateTxUnblock(parentBlock.getHash());
            ontTxDao.deleteTxByBlockHash(parentBlock.getHash());
//            txDetailDao.deleteTxDetailByBlockHash(parentBlock.getHash());
            txList.forEach(tx -> {
                List<Address> addressList = ontAddressDao.selectAddressByAddress(tx.getFromAddr(), tx.getToAddr());
                Address fromAddress = this.filterAddress(addressList, tx.getFromAddr());
                Address toAddress = this.filterAddress(addressList, tx.getToAddr());

                if (Objects.nonNull(fromAddress)) {
                    AddressAsset addressAsset = AddressAsset.txToAddressAssetForRollBack(tx, true);
                    Integer row = ontAddressAssetDao.updateAddressAsset(addressAsset);
                    VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
                }

                if (Objects.nonNull(toAddress)) {
                    AddressAsset addressAsset = AddressAsset.txToAddressAssetForRollBack(tx, false);
                    Integer row = ontAddressAssetDao.updateAddressAsset(addressAsset);
                    VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
                }
            });
        }
    }

    @Override
    public List<Tx> selectTxBtBlockHash(String blockHash) {
        return ontTxDao.selectTxBtBlockHash(blockHash);
    }

    @Override
    public Integer existTx(String txHash, Integer txNum) {
        return ontTxDao.existTx(txHash, txNum);
    }
}
