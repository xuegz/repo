package com.atoken.update.service.coin.account.ht;

import java.util.List;
import com.atoken.update.entity.coin.account.ht.HtTokenBalance;

public interface HtTokenBalanceService {

    HtTokenBalance getOneByContractAddrAndAddr(String contractAddr, String addr);

    List<HtTokenBalance> getListByContractAddrAndAddrs(String contractAddr, List<String> addrs);

    int saveOrUpdate(HtTokenBalance tokenBalance);

    int updateById(HtTokenBalance tokenBalance);
}
