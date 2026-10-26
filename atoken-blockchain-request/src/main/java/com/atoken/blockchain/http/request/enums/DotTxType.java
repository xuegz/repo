package com.atoken.blockchain.http.request.enums;

import org.polkadot.types.type.Extrinsic;

import java.math.BigDecimal;
import java.util.Arrays;

public enum DotTxType {
    TRANSFER("0300", "交易", new TransferAndBondParseArgs())
    , CHILL("0506", "取消提名", new ParseArgs())
    , VALIDATE("0504", "质押", new ParseArgs())
    , BOND_EXTRA("0501", "追加绑定", new BondExtraParseArgs())
    , SET_PAYEE("0507", "质押", new ParseArgs())
    , BOND("0500", "绑定", new TransferAndBondParseArgs())
    , NOMINATE("0505", "提名", new NominateParseArgs())
    , UNBOND("0502", "取消绑定", new UnBondParseArgs())
    , OTHER("", "其他交易", new ParseArgs());
    private String typeCode;
    private String description;
    private ParseArgs parseArgs;

    DotTxType(String typeCode, String description, ParseArgs parseArgs) {
        this.typeCode = typeCode;
        this.description = description;
        this.parseArgs = parseArgs;
    }

    public String typeCode() {
        return this.typeCode;
    }

    public boolean isTxType(String txType) {
        return this.typeCode.equals(txType);
    }

    public static DotTxType valueOfByTypeCode(String typeCode) {
        try {
            return Arrays.stream(DotTxType.values()).filter(dotTxType -> dotTxType.typeCode.equals(typeCode)).findFirst().get();
        } catch (Exception e) {
            return OTHER;
        }
    }

    public ExtrinsicArgs parseArgs(Extrinsic extrinsic) {
        return parseArgs.parse(extrinsic);
    }

    public static class ExtrinsicArgs {
        private String toAddr = "";
        private BigDecimal amount = BigDecimal.ZERO;
        private String comment = "";

        public String getToAddr() {
            return toAddr;
        }

        public void setToAddr(String toAddr) {
            this.toAddr = toAddr;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

}

class ParseArgs {
    public DotTxType.ExtrinsicArgs parse(Extrinsic extrinsic) {
        return new DotTxType.ExtrinsicArgs();
    }
}

class TransferAndBondParseArgs extends ParseArgs {
    @Override
    public DotTxType.ExtrinsicArgs parse(Extrinsic extrinsic) {
        DotTxType.ExtrinsicArgs extrinsicArgs = new DotTxType.ExtrinsicArgs();
        extrinsicArgs.setToAddr(extrinsic.getMethod().getArgs().get(0).toString());
        extrinsicArgs.setAmount(new BigDecimal(extrinsic.getMethod().getArgs().get(1).toString()));
        return extrinsicArgs;
    }
}

class BondExtraParseArgs extends ParseArgs {
    @Override
    public DotTxType.ExtrinsicArgs parse(Extrinsic extrinsic) {
        DotTxType.ExtrinsicArgs extrinsicArgs = new DotTxType.ExtrinsicArgs();
        extrinsicArgs.setAmount(new BigDecimal(extrinsic.getMethod().getArgs().get(0).toString()));
        return extrinsicArgs;
    }
}

class UnBondParseArgs extends ParseArgs {
    @Override
    public DotTxType.ExtrinsicArgs parse(Extrinsic extrinsic) {
        DotTxType.ExtrinsicArgs extrinsicArgs = new DotTxType.ExtrinsicArgs();
        extrinsicArgs.setAmount(new BigDecimal(extrinsic.getMethod().getArgs().get(0).toString()));
        return extrinsicArgs;
    }
}

class NominateParseArgs extends ParseArgs {
    @Override
    public DotTxType.ExtrinsicArgs parse(Extrinsic extrinsic) {
        DotTxType.ExtrinsicArgs extrinsicArgs = new DotTxType.ExtrinsicArgs();
        extrinsicArgs.setComment(extrinsic.getMethod().getArgs().get(0).toString());
        return extrinsicArgs;
    }
}
