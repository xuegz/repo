package com.atoken.update.service.coin.account;

import com.atoken.update.entity.coin.account.CoinFee;
import com.atoken.update.entity.coin.account.ImportAddressBean;

import java.util.List;

public interface AccountExchangeService {
    Integer updateCionFee(CoinFee coinFee);

    Integer insertCoinFee(CoinFee coinFee);

    List<ImportAddressBean> selectImportAddressByCoinType(String coinType);

    Integer updateImportAddressStatus(Integer status, Integer count, Integer id);
}
