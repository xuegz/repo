package com.atoken.blockchain.http.request.account.dot.sidecar.enums;

import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.MethodDTO;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author: erwin
 * @time: 2020/9/9 4:58 下午
 */
@Getter
public enum MethodEnum {

    // 参考 DotTxType 枚举
    // balances-transfer.json
    BALANCES_TRANSFER("balances", "transfer", "交易", new TransferParseArgs()),
    BALANCES_TRANSFER_KEEP_ALIVE("balances", "transferKeepAlive", "交易", new TransferParseArgs()),

    // 	staking-bond.json
    STAKING_BOND("staking", "Bonded", "绑定", new BondParseArgs()),
    // staking-unbond.json
    STAKING_UNBOND("staking", "Unbonded", "取消绑定", new UnBondParseArgs()),
    // staking-bond_extra.json
    STAKING_BOND_EXTRA("staking", "bondExtra", "追加绑定", new BondExtraParseArgs()),
    // staking-set_payee.json
    STAKING_SET_PAYEE("staking", "setPayee", "质押", new ParseArgs()),
    // staking-validate.json
    STAKING_VALIDATE("staking", "validate", "质押", new ParseArgs()),
    // staking-nominate.json
    STAKING_NOMINATE("staking", "nominate", "提名", new NominateParseArgs()),
    // staking-chill.json
    STAKING_CHILL("staking", "chill", "取消提名", new ParseArgs()),

    STAKING_SET_CONTROLLER("staking", "setController", "更改控制账户", new SetControllerParseArgs()),

    STAKING_WITHDRAWN("staking", "withdrawn", "撤回", new WithdrawnParseArgs()),

    OTHER("", "", "其他交易", new ParseArgs());


    MethodEnum(String pallet, String method, String desc, ParseArgs parseArgs) {
        this.pallet = pallet;
        this.method = method;
        this.desc = desc;
        this.parseArgs = parseArgs;
    }

    private final String pallet;

    private final String method;

    private final String desc;

    private final ParseArgs parseArgs;


    private static final Set<MethodEnum> SUPPORT_METHOD = new HashSet<>();

    static {

        SUPPORT_METHOD.add(BALANCES_TRANSFER);
        SUPPORT_METHOD.add(BALANCES_TRANSFER_KEEP_ALIVE);
        SUPPORT_METHOD.add(STAKING_BOND);
        SUPPORT_METHOD.add(STAKING_UNBOND);
        SUPPORT_METHOD.add(STAKING_BOND_EXTRA);
        SUPPORT_METHOD.add(STAKING_SET_PAYEE);
        SUPPORT_METHOD.add(STAKING_VALIDATE);
        SUPPORT_METHOD.add(STAKING_NOMINATE);
        SUPPORT_METHOD.add(STAKING_CHILL);
        SUPPORT_METHOD.add(STAKING_SET_CONTROLLER);
        SUPPORT_METHOD.add(STAKING_WITHDRAWN);
        SUPPORT_METHOD.add(OTHER);

    }

    public static boolean isSupport(MethodDTO methodDTO) {

        if (Objects.isNull(methodDTO)) {
            return false;
        }
        for (MethodEnum methodEnum : SUPPORT_METHOD) {
            if (methodEnum.getMethod().equalsIgnoreCase(methodDTO.getMethod())
                    && methodEnum.getPallet().equalsIgnoreCase(methodDTO.getPallet())) {
                return true;
            }
        }
        return false;
    }

    public static MethodEnum valueOf(MethodDTO methodDTO) {

        if (Objects.isNull(methodDTO)) {
            return OTHER;
        }
        for (MethodEnum methodEnum : SUPPORT_METHOD) {
            if (methodEnum.getMethod().equalsIgnoreCase(methodDTO.getMethod())
                    && methodEnum.getPallet().equalsIgnoreCase(methodDTO.getPallet())) {
                return methodEnum;
            }
        }
        return OTHER;
    }

    public MethodEnum.ExtrinsicArgs parseArgs(Map<String, String> args) {
        return parseArgs.parse(args);
    }

    public MethodEnum.EventArgsDTO parseArgs(List<String> args) {
        return parseArgs.parse(args);
    }

    @Data
    public static class ExtrinsicArgs {
        private String toAddr = "";
        private BigDecimal amount = BigDecimal.ZERO;
        private String comment = "";
    }


    @Data
    public static class EventArgsDTO {
        private String fromAddr = "";
        private String toAddr = "";
        private BigDecimal amount = BigDecimal.ZERO;
    }

}


class ParseArgs {

    public MethodEnum.ExtrinsicArgs parse(Map<String, String> args) {
        return new MethodEnum.ExtrinsicArgs();
    }

    public MethodEnum.EventArgsDTO parse(List<String> args) {
        return new MethodEnum.EventArgsDTO();
    }

}

class TransferParseArgs extends ParseArgs {
    @Override
    public MethodEnum.ExtrinsicArgs parse(Map<String, String> args) {
        MethodEnum.ExtrinsicArgs extrinsicArgs = new MethodEnum.ExtrinsicArgs();
        extrinsicArgs.setToAddr(args.get("dest"));
        extrinsicArgs.setAmount(new BigDecimal(args.get("value")));
        return extrinsicArgs;
    }

    @Override
    public MethodEnum.EventArgsDTO parse(List<String> args) {
        MethodEnum.EventArgsDTO eventArgsDTO = new MethodEnum.EventArgsDTO();
        eventArgsDTO.setFromAddr(args.get(0));
        eventArgsDTO.setToAddr(args.get(1));
        eventArgsDTO.setAmount(new BigDecimal(args.get(2)));
        return eventArgsDTO;
    }


}

class BondParseArgs extends ParseArgs {
    @Override
    public MethodEnum.ExtrinsicArgs parse(Map<String, String> args) {
        MethodEnum.ExtrinsicArgs extrinsicArgs = new MethodEnum.ExtrinsicArgs();
        extrinsicArgs.setToAddr(args.get("controller"));
        extrinsicArgs.setAmount(new BigDecimal(args.get("value")));
        return extrinsicArgs;
    }
}


class BondExtraParseArgs extends ParseArgs {
    @Override
    public MethodEnum.ExtrinsicArgs parse(Map<String, String> args) {
        MethodEnum.ExtrinsicArgs extrinsicArgs = new MethodEnum.ExtrinsicArgs();
        extrinsicArgs.setAmount(new BigDecimal(args.get("max_additional")));
        return extrinsicArgs;
    }

    @Override
    public MethodEnum.EventArgsDTO parse(List<String> args) {
        MethodEnum.EventArgsDTO eventArgsDTO = new MethodEnum.EventArgsDTO();
        eventArgsDTO.setAmount(new BigDecimal(args.get(1)));
        return eventArgsDTO;
    }
}

class UnBondParseArgs extends ParseArgs {
    @Override
    public MethodEnum.ExtrinsicArgs parse(Map<String, String> args) {
        MethodEnum.ExtrinsicArgs extrinsicArgs = new MethodEnum.ExtrinsicArgs();
        extrinsicArgs.setAmount(new BigDecimal(args.get("value")));
        return extrinsicArgs;
    }

    @Override
    public MethodEnum.EventArgsDTO parse(List<String> args) {
        MethodEnum.EventArgsDTO eventArgsDTO = new MethodEnum.EventArgsDTO();
        eventArgsDTO.setToAddr(args.get(0));
        eventArgsDTO.setAmount(new BigDecimal(args.get(1)));
        return eventArgsDTO;
    }
}

class NominateParseArgs extends ParseArgs {

}

class SetControllerParseArgs extends ParseArgs {
    
}

class WithdrawnParseArgs extends ParseArgs {
    @Override
    public MethodEnum.EventArgsDTO parse(List<String> args) {
        MethodEnum.EventArgsDTO eventArgsDTO = new MethodEnum.EventArgsDTO();
        eventArgsDTO.setToAddr(args.get(0));
        eventArgsDTO.setAmount(new BigDecimal(args.get(1)));
        return eventArgsDTO;
    }
}
