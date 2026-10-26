package com.atoken.update.bnb.constant;

import java.util.Collections;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;

/**
 * @author atoken-team
 * @date 2021/02/25
 */
public class BnbConstant {

    private BnbConstant() {}

    public static final String TRANSFER_TOPIC =
            "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_NAME_HASH = FunctionEncoder
            .encode(new Function(FUNC_NAME, Collections.emptyList(), Collections.emptyList()));

    public static final String FUNC_SYMBOL_HASH = FunctionEncoder
            .encode(new Function(FUNC_SYMBOL, Collections.emptyList(), Collections.emptyList()));

    public static final String FUNC_DECIMALS_HASH = FunctionEncoder
            .encode(new Function(FUNC_DECIMALS, Collections.emptyList(), Collections.emptyList()));

    public static final String FUNC_BALANCE_OF_HASH = "0x70a08231000000000000000000000000";
}
