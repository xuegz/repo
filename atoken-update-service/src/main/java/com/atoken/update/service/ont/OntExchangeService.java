package com.atoken.update.service.ont;

import com.atoken.update.entity.ont.CoinFee;

public interface OntExchangeService {
    Integer updateCionFee(CoinFee coinFee);

    Integer insertCoinFee(CoinFee coinFee);
}
