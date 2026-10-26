package com.atoken.update.service;

import com.atoken.update.entity.Address;
import com.atoken.update.entity.ImportAddressBean;
import com.atoken.update.handler.BalanceAfterHander;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dfg
 */
public interface AddressService {
    /**
     * 通过地址查询地址信息
     * @param dataBase
     * @param fromAddr
     * @param toAddr
     * @return
     */
    List<Address> selectAddressByAddress(String dataBase, String fromAddr, String toAddr);

    Integer updateAmountByAddress(String dataBase, String address, BigDecimal amount);

    /**
     * 地址余额更新处理
     * @param dataBase
     * @param importAddress
     * @param amount
     * @param balanceAfterHander
     */
    void addressAmountHander(String dataBase, ImportAddressBean importAddress, BigDecimal amount, BalanceAfterHander balanceAfterHander);
}
