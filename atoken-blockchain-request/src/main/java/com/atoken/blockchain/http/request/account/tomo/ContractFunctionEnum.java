package com.atoken.blockchain.http.request.account.tomo;

import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.ConstantUtil;
import com.atoken.blockchain.utils.DataUtil;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigDecimal;
import java.util.*;

public enum ContractFunctionEnum {
    // 获取验证人地址
    GET_CANDIDATES(Arrays.asList(ContractEnum.STAKING), "getCandidates", Collections.EMPTY_MAP, Collections.EMPTY_MAP)
    // 获取质押地址拥有者
    , GET_CANDIDATE_OWNER(Arrays.asList(ContractEnum.STAKING), "getCandidateOwner", Collections.EMPTY_MAP, Collections.EMPTY_MAP)
    // 获取质押人的质押数量
    , GET_VOTER_CAP(Arrays.asList(ContractEnum.STAKING), "getVoterCap", Collections.EMPTY_MAP, Collections.EMPTY_MAP)
    // 获取验证人的质押数量
    , GET_CANDIDATE_CAP(Arrays.asList(ContractEnum.STAKING), "getCandidateCap", Collections.EMPTY_MAP, Collections.EMPTY_MAP)
    // 获取质押人
    , GET_VOTERS(Arrays.asList(ContractEnum.STAKING), "getVoters", Collections.EMPTY_MAP, Collections.EMPTY_MAP)
    // 获取提现区块高度列表
    , GET_WITHDRAW_BLOCKNUMBERS(Arrays.asList(ContractEnum.STAKING), "getWithdrawBlockNumbers", Collections.EMPTY_MAP, Collections.EMPTY_MAP)
    // 获取提现金额
    , GET_WITHDRAW_CAP(Arrays.asList(ContractEnum.STAKING), "getWithdrawCap", Collections.EMPTY_MAP, Collections.EMPTY_MAP)
    // 质押
    , VOTE(Arrays.asList(ContractEnum.STAKING)
            , "vote"
            , toMap("0x6dd7d8ea", Arrays.asList(new TypeReference<Address>() {}))
            , toTxDataHandlerMap("0x6dd7d8ea", new VoteDataHandler()))
    // 解除质押
    , UNVOTE(Arrays.asList(ContractEnum.STAKING)
            , "unvote"
            , toMap("0x02aa9be2", Arrays.asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}))
            , toTxDataHandlerMap("0x02aa9be2", new UnVoteDataHandler()))
    // 提现
    , WITHDRAW(Arrays.asList(ContractEnum.STAKING)
            , "withdraw"
            , toMap("0x441a3e70", Arrays.asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}))
            , toTxDataHandlerMap("0x441a3e70", new WithdrawDataHandler()))
    // 普通交易
    , NORMAL(Arrays.asList(), "normal", Collections.EMPTY_MAP, Collections.EMPTY_MAP)
    ;
    private List<ContractEnum> supportContractList;
    private String functionName;
    private Map<String, List<TypeReference<?>>> methodIdMap;
    private Map<String, TxDataHandler> txDataHandlerMap;

    ContractFunctionEnum(List<ContractEnum> supportContractList, String functionName, Map<String, List<TypeReference<?>>> methodIdMap, Map<String, TxDataHandler> txDataHandlerMap) {
        this.supportContractList = supportContractList;
        this.functionName = functionName;
        this.methodIdMap = methodIdMap;
        this.txDataHandlerMap = txDataHandlerMap;
    }

    public static boolean isNormalTx(String input) {
        return "0x".equals(input);
    }

    public static ContractInfo parse(String input, String data) {
        String methodId = input.substring(0, 10);
        String inputData = ConstantUtil.HEX_PREFIX + input.substring(10);
        ContractFunctionEnum contractFunctionEnum = Arrays.stream(ContractFunctionEnum.values())
                .filter(item -> item.methodIdMap.keySet()
                        .contains(methodId))
                .findFirst().orElse(null);
        Function function = new Function(contractFunctionEnum.functionName, Collections.EMPTY_LIST, contractFunctionEnum.methodIdMap.get(methodId));
        List<Type> typeList = FunctionReturnDecoder.decode(inputData, function.getOutputParameters());
        ContractInfo contractInfo = contractFunctionEnum.txDataHandlerMap.get(methodId).handler(typeList, data);
        contractInfo.contractFunctionEnum(contractFunctionEnum)
                .typeList(typeList);
        return contractInfo;
    }

    public String functionName() {
        return functionName;
    }

    public ContractFunctionEnum valueOfByMethodId(String input) {
        String methodId = input.substring(0, 10);
        return Arrays.stream(ContractFunctionEnum.values())
                .filter(item -> item.methodIdMap.keySet()
                        .contains(methodId))
                .findFirst().orElse(null);
    }

    public boolean support(ContractEnum contractEnum) {
        return supportContractList.contains(contractEnum);
    }

    private static Map<String, List<TypeReference<?>>> toMap(Object... args) {
        Map<String, List<TypeReference<?>>> methodIdMap = new HashMap<>();
        for(int i = 0, len = args.length; i < len; i += 2) {
            methodIdMap.put(args[i].toString(), (List<TypeReference<?>>) args[i + 1]);
        }
        return methodIdMap;
    }

    private static Map<String, TxDataHandler> toTxDataHandlerMap(Object... args) {
        Map<String, TxDataHandler> txDataHandlerMap = new HashMap<>();
        for(int i = 0, len = args.length; i < len; i += 2) {
            txDataHandlerMap.put(args[i].toString(), (TxDataHandler) args[i + 1]);
        }
        return txDataHandlerMap;
    }

    private interface TxDataHandler {
        ContractInfo handler(List<Type> typeList, String data);
    }

    private static class VoteDataHandler implements TxDataHandler {

        @Override
        public ContractInfo handler(List<Type> typeList, String data) {
            Address addressType = (Address) typeList.get(0);
            String address = addressType.getValue();
            return new ContractInfo().address(address);
        }
    }

    private static class UnVoteDataHandler implements TxDataHandler {

        @Override
        public ContractInfo handler(List<Type> typeList, String data) {
            Address addressType = (Address) typeList.get(0);
            Uint256 uint256Type = (Uint256) typeList.get(1);
            BigDecimal amount = DataUtil.formatAmount(new BigDecimal(uint256Type.getValue()), CoinEnum.tomo.decimals());
            return new ContractInfo().address(addressType.getValue()).amount(amount);
        }
    }

    private static class WithdrawDataHandler implements TxDataHandler {

        @Override
        public ContractInfo handler(List<Type> typeList, String data) {
            Function function = new Function("", Collections.EMPTY_LIST, Arrays.asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
            List<Type> dataTypeList = FunctionReturnDecoder.decode(data, function.getOutputParameters());
            Uint256 uint256Type = (Uint256) dataTypeList.get(2);
            BigDecimal amount = DataUtil.formatAmount(new BigDecimal(uint256Type.getValue()), CoinEnum.tomo.decimals());
            return new ContractInfo().amount(amount);
        }
    }

    public static class ContractInfo {
        private ContractFunctionEnum contractFunctionEnum;
        private List<Type> typeList;
        private String address;
        private BigDecimal amount;

        public ContractFunctionEnum getContractFunctionEnum() {
            return contractFunctionEnum;
        }

        public ContractInfo contractFunctionEnum(ContractFunctionEnum contractFunctionEnum) {
            this.contractFunctionEnum = contractFunctionEnum;
            return this;
        }

        public List<Type> getTypeList() {
            return typeList;
        }

        public ContractInfo typeList(List<Type> typeList) {
            this.typeList = typeList;
            return this;
        }

        public String getAddress() {
            return address;
        }

        public ContractInfo address(String address) {
            this.address = address;
            return this;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public ContractInfo amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
    }
}
