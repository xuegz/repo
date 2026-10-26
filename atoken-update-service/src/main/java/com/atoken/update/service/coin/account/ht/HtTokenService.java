package com.atoken.update.service.coin.account.ht;

import com.atoken.update.entity.coin.account.ht.HtToken;

public interface HtTokenService {

    HtToken getOneByContractAddr(String contractAddr);

    int saveOrUpdate(HtToken token);
}
