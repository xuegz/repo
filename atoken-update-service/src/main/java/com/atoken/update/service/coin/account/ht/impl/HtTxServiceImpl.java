package com.atoken.update.service.coin.account.ht.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.atoken.update.dao.coin.account.AccountAddressDao;
import com.atoken.update.dao.coin.account.ht.HtTxDao;
import com.atoken.update.entity.coin.account.Address;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.ht.HtTx;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;
import com.atoken.update.service.coin.account.ht.HtTxService;

@Service
public class HtTxServiceImpl implements HtTxService {

    @Autowired
    private HtTxDao htTxDao;

    @Autowired
    private AccountAddressDao addressDao;

    @Override
    public int saveOrUpdate(HtTx tx) {
        return htTxDao.saveOrUpdate(tx);
    }

    private Address filterAddress(List<Address> addressList, String address) {
        Optional<Address> optionalAddress =
                addressList.stream().filter(addr -> addr.getAddr().equals(address)).findAny();
        return Optional.empty().equals(optionalAddress) ? null : optionalAddress.get();
    }

    @Override
    @Transactional
    public void rollBackTx(String coin, Block parentBlock,
            RollBackTxAfterHander rollBackTxAfterHander) {
        List<HtTx> txList = htTxDao.selectListByBlockHash(parentBlock.getHash());
        if (txList.size() > 0) {
            List<Long> ids = new ArrayList<Long>(txList.size());
            txList.forEach(tx -> {
                ids.add(tx.getId());
                List<Address> addressList =
                        addressDao.selectAddressByAddress(coin, tx.getFromAddr(), tx.getToAddr());
                Address fromAddress = this.filterAddress(addressList, tx.getFromAddr());
                Address toAddress = this.filterAddress(addressList, tx.getToAddr());

                if (Objects.nonNull(rollBackTxAfterHander)) {
                    rollBackTxAfterHander.handler(fromAddress, toAddress);
                }
            });
            if (!ids.isEmpty()) {
                htTxDao.deleteByIds(ids);
            }
        }
    }
}
