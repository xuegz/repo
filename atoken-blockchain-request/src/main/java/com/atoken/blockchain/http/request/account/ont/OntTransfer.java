package com.atoken.blockchain.http.request.account.ont;

import com.atoken.blockchain.utils.DataUtil;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;

import java.math.BigDecimal;

public class OntTransfer {
    private static final String ontContract = "0000000000000000000000000000000000000001";
    private static final String ongContract = "0000000000000000000000000000000000000002";
    private OntMethod method;
    private String from;
    private String to;
    private BigDecimal amount;
    private String assetCode;
    private String asset;

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public OntMethod getMethod() {
        return method;
    }

    public void setMethod(OntMethod method) {
        this.method = method;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public static OntTransfer parseTransfer(byte[] code) {
        OntTransfer transfer = new OntTransfer();
        String codeHexStr = Helper.toHexString(code);
        if (codeHexStr.length() > 44 && codeHexStr.substring(codeHexStr.length() - 44).equals(Helper.toHexString("Ontology.Native.Invoke".getBytes()))) {
            if (codeHexStr.substring(codeHexStr.length() - 92 - 16, codeHexStr.length() - 92).equals(Helper.toHexString("transfer".getBytes()))) {
                transfer.setMethod(OntMethod.transfer);
            } else if (codeHexStr.substring(codeHexStr.length() - 92 - 24, codeHexStr.length() - 92).equals(Helper.toHexString("transferFrom".getBytes()))) {
                transfer.setMethod(OntMethod.transferFrom);
            }
            transfer.setFrom(Address.parse(codeHexStr.substring(8, 48)).toBase58());
            transfer.setTo(Address.parse(codeHexStr.substring(56, 96)).toBase58());
            transfer.setAmount(BigDecimal.ZERO);

            if (codeHexStr.substring(102, 103).equals("5")) {
                transfer.setAmount(new BigDecimal(code[51] - 0x50));
            } else {
                transfer.setAmount(new BigDecimal(Helper.BigIntFromNeoBytes(Helper.hexToBytes(codeHexStr.substring(104, 104 + code[51] * 2)))));
            }
            String assertCode = codeHexStr.substring(codeHexStr.length() - 50 - 40, codeHexStr.length() - 50);
            OntAssetCode ontAssetCode = OntAssetCode.ontAssetCode(assertCode);
            transfer.setAsset(ontAssetCode.name());
            transfer.setAssetCode(ontAssetCode.assetCode());
        }
        return transfer;
    }
}
