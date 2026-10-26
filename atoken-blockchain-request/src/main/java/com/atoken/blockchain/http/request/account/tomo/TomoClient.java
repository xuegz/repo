package com.atoken.blockchain.http.request.account.tomo;

import com.atoken.blockchain.http.request.RequestClient;
import com.atoken.blockchain.http.request.RpcParams;
import com.atoken.blockchain.http.request.account.atom.AtomNodeStatus;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TomoClient extends RequestClient {
    private Web3j web3j;
    private ParityExtend parityExtend;
    private String scanUrl;
    private String masterUrl;
    private String apiUrl;
    private String rpcUrl;
    private final static String EXCLUDE_ADDRESS = "0x0000000000000000000000000000000000000000";

    public TomoClient(String baseUrl, String rpcUrl, String scanUrl, String apiUrl, String masterUrl) {
        super(baseUrl);
        web3j = Web3j.build(new HttpService(rpcUrl));
        parityExtend = ParityExtend.build(new HttpService(rpcUrl));
        this.scanUrl = scanUrl;
        this.masterUrl = masterUrl;
        this.apiUrl = apiUrl;
        this.rpcUrl = rpcUrl;
    }

    public Integer getCurrentBlockHeight() {
        return this.send(web3j.ethBlockNumber()).getBlockNumber().intValue();
    }

    public EthBlock.Block getBlockByHeight(Integer blockHeight, boolean needTxs) {
        return this.send(web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockHeight)), needTxs)).getBlock();
    }

    public EthBlock.Block getBlockHeadByHeight(Integer blockHeight) {
        return this.send(web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockHeight)), false)).getBlock();
    }

    public EthBlock.Block getBlockByHeight(Integer blockHeight) {
        return this.send(web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockHeight)), true)).getBlock();
    }

    public TransactionReceipt getTransactionReceiptByTxHash(String txHash) {
        return this.send(web3j.ethGetTransactionReceipt(txHash)).getTransactionReceipt().orElse(null);
    }

    public Transaction getTxByTxHash(String txHash) {
        return this.send(web3j.ethGetTransactionByHash(txHash)).getTransaction().orElse(null);
    }

    public BigDecimal getBalanceByAddress(String address) {
        return new BigDecimal(this.send(web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)).getBalance());
    }

    public Integer getNonce(String address) {
        return this.send(web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)).getTransactionCount().intValue();
    }

    public List<Address> getValidators() {
        List<Type> typeList = new ContractRequest(ContractEnum.STAKING, ContractFunctionEnum.GET_CANDIDATES)
                .addOutputParameters(new TypeReference<DynamicArray<Address>>() {}).builderAndSend();
        DynamicArray<Address> resultType = (DynamicArray<Address>) typeList.get(0);
        return resultType.getValue().stream().filter(address -> !address.getValue().equals(EXCLUDE_ADDRESS)).collect(Collectors.toList());
    }

    public Address getValidatorOwner(String validatorAddress) {
        List<Type> typeList = new ContractRequest(ContractEnum.STAKING, ContractFunctionEnum.GET_CANDIDATE_OWNER)
                .fromAddress(validatorAddress)
                .addInputParameter(new Address(validatorAddress))
                .addOutputParameters(new TypeReference<Address>() {}).builderAndSend();
        return (Address) typeList.get(0);
    }

    public List<Address> getVoters(String validatorAddress) {
        List<Type> typeList = new ContractRequest(ContractEnum.STAKING, ContractFunctionEnum.GET_VOTERS)
                .addInputParameter(new Address(validatorAddress))
                .addOutputParameters(new TypeReference<DynamicArray<Address>>() {}).builderAndSend();
        DynamicArray<Address> resultType = (DynamicArray<Address>) typeList.get(0);
        return resultType.getValue();
    }

    public BigDecimal getVoterCap(String validatorAddress, String voterAddress) {
        List<Type> typeList = new ContractRequest(ContractEnum.STAKING, ContractFunctionEnum.GET_VOTER_CAP)
                .addInputParameter(new Address(validatorAddress), new Address(voterAddress))
                .addOutputParameters(new TypeReference<Uint256>() {}).builderAndSend();
        Uint256 resultType = (Uint256) typeList.get(0);
        return new BigDecimal(resultType.getValue());
    }

    public BigDecimal getCandidateCap(String validatorAddress) {
        List<Type> typeList = new ContractRequest(ContractEnum.STAKING, ContractFunctionEnum.GET_CANDIDATE_CAP)
                .fromAddress(validatorAddress)
                .addInputParameter(new Address(validatorAddress))
                .addOutputParameters(new TypeReference<Uint256>() {}).builderAndSend();
        Uint256 resultType = (Uint256) typeList.get(0);
        return new BigDecimal(resultType.getValue());
    }

    public List<Uint256> getWithdrawBlockNumbers(String voterAddress) {
        List<Type> typeList = new ContractRequest(ContractEnum.STAKING, ContractFunctionEnum.GET_WITHDRAW_BLOCKNUMBERS)
                .addOutputParameters(new TypeReference<DynamicArray<Uint256>>() {})
                .fromAddress(voterAddress).builderAndSend();
        DynamicArray<Uint256> resultType = (DynamicArray<Uint256>) typeList.get(0);
        return resultType.getValue();
    }

    public BigDecimal getWithdrawCap(String fromAddress, Long blockHeight) {
        List<Type> typeList = new ContractRequest(ContractEnum.STAKING, ContractFunctionEnum.GET_WITHDRAW_CAP)
                .addInputParameter(new Uint256(blockHeight))
                .addOutputParameters(new TypeReference<Uint256>() {})
                .fromAddress(fromAddress).builderAndSend();
        Uint256 resultType = (Uint256) typeList.get(0);
        return new BigDecimal(resultType.getValue());
    }

    public TomoVoterRewards getVoterRewards(String voterAddress, Integer page, Integer limit) {
        return new com.atoken.blockchain.http.request.Request()
                .url(scanUrl, "api/rewards", voterAddress)
                .method(Method.GET).data("page", Objects.isNull(page) ? "1": page.toString(), "limit", Objects.isNull(limit) ? "20": limit.toString())
                .send(TomoVoterRewards.class);
    }

    public TomoVoterRewards getVoterRewardsByEpoch(Integer epoch, String voterAddress, Integer page, Integer limit) {
        return new com.atoken.blockchain.http.request.Request()
                .url(scanUrl, "api/rewards/epoch", epoch.toString())
                .method(Method.GET)
                .data("hash", voterAddress, "page", Objects.isNull(page) ? "1": page.toString(), "limit", Objects.isNull(limit) ? "20": limit.toString())
                .send(TomoVoterRewards.class);
    }

    public TomoAccount getAccountInfo(String ownerAddress) {
        return new com.atoken.blockchain.http.request.Request()
                .url(scanUrl, "api/accounts", ownerAddress)
                .method(Method.GET)
                .send(TomoAccount.class);
    }

    public TomoMasterNodes getMasterNodes() {
        return new com.atoken.blockchain.http.request.Request()
                .url(scanUrl, "api/masternodes")
                .method(Method.GET)
                .send(TomoMasterNodes.class);
    }

    public TomoVotedCandidates getVotedCandidates(String voterAddress, Integer page, Integer limit) {
        return new com.atoken.blockchain.http.request.Request()
                .url(masterUrl, "api/voters", voterAddress, "candidates")
                .method(Method.GET)
                .data("page", Objects.isNull(page) ? "1": page.toString(), "limit", Objects.isNull(limit) ? "20": limit.toString())
                .send(TomoVotedCandidates.class);
    }

    public Double geYearYieldBy(String validatorAddress) {
        return new com.atoken.blockchain.http.request.Request()
                .url(masterUrl, "api/voters/calculatingReward1Day")
                .method(Method.GET)
                .data("candidate", validatorAddress, "amount", "100")
                .send(Double.class) * 360/100;
    }

    public List<TomoTransaction> getPeddingTxs() {
        com.atoken.blockchain.http.request.Request request = new com.atoken.blockchain.http.request.Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("eth_pendingTransactions");
        return request.url(rpcUrl)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(TomoTransactionsRespnonse.class)
                .getResult();
    }

    private <T extends Response> T send(Request<?, T> request) {
        try {
            return request.send();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private class ContractRequest {
        private ContractEnum contractEnum;
        private ContractFunctionEnum contractFunctionEnum;
        private List<Type> inputParameters = new ArrayList<>();
        private List<TypeReference<?>> outputParameters = new ArrayList<>();
        private String fromAddress;

        public ContractRequest(ContractEnum contractEnum, ContractFunctionEnum contractFunctionEnum) {
            if (!contractFunctionEnum.support(contractEnum)) {
                throw new RuntimeException(String.format("the contract: %s not support the function", contractEnum.contractAddress()));
            }
            this.contractEnum = contractEnum;
            this.contractFunctionEnum = contractFunctionEnum;
        }

        public ContractRequest contractEnum(ContractEnum contractEnum) {
            this.contractEnum = contractEnum;
            return this;
        }

        public ContractRequest contractFunctionEnum(ContractFunctionEnum contractFunctionEnum) {
            this.contractFunctionEnum = contractFunctionEnum;
            return this;
        }

        public ContractRequest addInputParameter(Type... inputParameters) {
            this.inputParameters.addAll(Arrays.asList(inputParameters));
            return this;
        }

        public ContractRequest addOutputParameters(TypeReference<?>... outputParameters) {
            this.outputParameters.addAll(Arrays.asList(outputParameters));
            return this;
        }

        public ContractRequest fromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
            return this;
        }

        public List<Type> builderAndSend() {
            Function function = new Function(contractFunctionEnum.functionName(), inputParameters, outputParameters);
            String encode = FunctionEncoder.encode(function);
            org.web3j.protocol.core.methods.request.Transaction ethCallTransaction = org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(fromAddress, contractEnum.contractAddress(), encode);
            String result = send(web3j.ethCall(ethCallTransaction, DefaultBlockParameterName.LATEST)).getValue();
            return FunctionReturnDecoder.decode(result, function.getOutputParameters());
        }
    }
}
