package com.atoken.update.service.coin.account;

import com.atoken.update.entity.coin.account.Address;
import com.atoken.update.entity.coin.account.ImportAddressBean;
import com.atoken.update.handler.coin.account.BalanceAfterHander;

import java.math.BigDecimal;
import java.util.List;

public interface AccountAddressService {
    List<Address> selectAddressByAddress(String coin, String fromAddr, String toAddr);

    List<Address> selectaddressbyaddr(String coin, String addr);

    Integer updateAmountByAddress(String coin, String address, BigDecimal amount);

    void addressAmountHander(String coin, ImportAddressBean importAddress, BigDecimal amount, BalanceAfterHander balanceAfterHander);
}
