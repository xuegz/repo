package com.atoken.update.service.coin.account.bnb;

import java.util.List;
import com.atoken.update.entity.coin.account.bnb.BnbTokenBalance;

public interface BnbTokenBalanceService {

    BnbTokenBalance getOneByContractAddrAndAddr(String contractAddr, String addr);

    List<BnbTokenBalance> getListByContractAddrAndAddrs(String contractAddr, List<String> addrs);

    int saveOrUpdate(BnbTokenBalance tokenBalance);

    int updateById(BnbTokenBalance tokenBalance);
}
