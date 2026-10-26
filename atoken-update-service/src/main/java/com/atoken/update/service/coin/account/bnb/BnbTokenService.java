package com.atoken.update.service.coin.account.bnb;

import com.atoken.update.entity.coin.account.bnb.BnbToken;

public interface BnbTokenService {

    BnbToken getOneByContractAddr(String contractAddr);

    int saveOrUpdate(BnbToken token);
}
