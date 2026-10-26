package com.atoken.update.service;

import com.atoken.update.entity.CoinFee;
import com.atoken.update.entity.DataDict;
import com.atoken.update.entity.ImportAddressBean;

import java.util.List;

public interface ExchangeService {
    Integer updateCionFee(CoinFee coinFee);

    Integer insertCoinFee(CoinFee coinFee);

    List<ImportAddressBean> selectImportAddressByCoinType(String coinType);

    List<ImportAddressBean> selectAllImportAddressByCoinType(String coinType);

    Integer updateImportAddressStatus(Integer status, Integer count, Integer id);

    DataDict selectDataDictByKey(String coinType);
}
