package com.atoken.update.core.ont;

import com.atoken.update.entity.ImportAddressBean;
import com.atoken.update.entity.ont.AddressAsset;
import com.atoken.update.enums.ImportAddressStatus;
import com.atoken.update.service.ExchangeService;
import com.atoken.update.service.ont.OntAddressAssetService;
import com.atoken.update.service.ont.OntBlockHandlerService;
import com.atoken.update.service.ont.OntTokenService;
import com.atoken.update.util.AtokenLogger;
import com.atoken.update.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class OntAddressBalanceCore implements AtokenLogger {
    @Autowired
    private OntBlockHandlerService ontBlockHandlerService;
    @Autowired
    private OntTokenService ontTokenService;
    @Autowired
    private OntAddressAssetService ontAddressAssetService;
    @Autowired
    private ExchangeService exchangeService;

    public void addressBalanceHandler() {
        List<ImportAddressBean> importAddressBeans = exchangeService.selectImportAddressByCoinType("ONT");
        importAddressBeans.forEach(importAddressBean -> {
            try {
                Map<String, BigDecimal> addressBalanceMap = ontBlockHandlerService.getAddressBalance(importAddressBean.getAddr(), ontTokenService);
                // 入库地址余额
                addressBalanceMap.entrySet().stream()
                        .filter(balance -> balance.getValue().compareTo(BigDecimal.ZERO) == 1)
                        .forEach(addressBalance -> {
                            AddressAsset addressAsset = new AddressAsset();
                            addressAsset.setAddr(importAddressBean.getAddr());
                            addressAsset.setAmount(addressBalance.getValue());
                            addressAsset.setContractAddr(addressBalance.getKey());

                            // 区分主网币还是token
                            if (ConstantUtil.ASSERT_TYPE_MAIN.equals(addressBalance.getKey())) {
                                ontAddressAssetService.updateMainAsset(addressAsset);
                            } else {
                                ontAddressAssetService.updateAsset(addressAsset);
                            }
                        });
                exchangeService.updateImportAddressStatus(ImportAddressStatus.TREATED.status(), importAddressBean.getCount(), importAddressBean.getId());
            } catch (Exception e) {
                logger.error("更新地址： {}余额失败, {}", importAddressBean.getAddr(), e.getLocalizedMessage());
                Integer count = importAddressBean.getCount() + 1;
                if (count < 3) {
                    exchangeService.updateImportAddressStatus(ImportAddressStatus.TREATED.status(), count, importAddressBean.getId());
                } else {
                    exchangeService.updateImportAddressStatus(ImportAddressStatus.TREATED_FAIL.status(), count, importAddressBean.getId());
                }
            }
        });
    }
}
