package com.atoken.blockchain.http.request.account.dot.sidecar;

import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.RequestClient;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.*;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * https://paritytech.github.io/substrate-api-sidecar/dist/
 *
 * @author: erwin
 * @time: 2020/9/10 2:02 下午
 */
public class DotSidecarClient extends RequestClient {

    public DotSidecarClient(String baseUrl) {
        super(baseUrl);
    }

    private static final String ACCOUNTS_BALANCE_INFO = "/accounts/%s/balance-info";
    private static final String ACCOUNTS_VESTING_INFO = "/accounts/%s/vesting-info";
    private static final String ACCOUNTS_STAKING_INFO = "/accounts/%s/staking-info";
    private static final String ACCOUNTS_STAKING_PAYOUTS = "/accounts/%s/staking-payouts";


    private static final String BLOCKS_HEAD = "/blocks/head";
    private static final String BLOCKS = "/blocks/%s";
    // /blocks/{blockId}/extrinsics/{extrinsicIndex}
    private static final String BLOCKS_EXTRINSIC = "/blocks/{blockId}/extrinsics/{extrinsicIndex}";


    private static final String NODE_NETWORK = "/node/network";
    private static final String NODE_TRANSACTION_POOL = "/node/transaction-pool";
    private static final String NODE_VERSION = "/node/version";


    private static final String PALLETS_STAKING_PROGRESS = "/pallets/staking/progress";
    //    /pallets/{palletId}/storage
    private static final String PALLETS_STORAGE = "/pallets/%s/storage";
    //    /pallets/{palletId}/storage/{storageItemId}
    private static final String PALLETS_STORAGE_ITEM = "/pallets/%s/storage/%s";


    private static final String RUNTIME_SPEC = "/runtime/spec";
    private static final String RUNTIME_METADATA = "/runtime/metadata";
    private static final String RUNTIME_CODE = "/runtime/code";

    private static final String TRANSACTION = "/transaction";
    private static final String TRANSACTION_MATERIAL = "/transaction/material";
    private static final String TRANSACTION_DRY_RUN = "/transaction/dry-run";
    private static final String TRANSACTION_FEE_ESTIMATE = "/transaction/fee-estimate";


    public BlockResp blocksHead() {
        return buildRpcRequestGet(BLOCKS_HEAD).send(BlockResp.class);
    }

    public BlockResp blocks(Integer height) {
        return buildRpcRequestGet(BLOCKS, String.valueOf(height)).send(BlockResp.class);
    }

    public BlockResp blocks(String hash) {
        return buildRpcRequestGet(BLOCKS, hash).send(BlockResp.class);
    }

    public BalanceInfoResp balanceInfo(String address) {
        return buildRpcRequestGet(ACCOUNTS_BALANCE_INFO, address).send(BalanceInfoResp.class);
    }

    public AccountVestingInfo vestingInfo(String address) {
        return buildRpcRequestGet(ACCOUNTS_VESTING_INFO, address).send(AccountVestingInfo.class);
    }


    public TransactionPoolResp transactionPool() {
        return buildRpcRequestGet(NODE_TRANSACTION_POOL).send(TransactionPoolResp.class);
    }

    public Object transaction(TransactionReq transactionReq) {
        return buildRpcRequestPost(TRANSACTION).data(transactionReq).send(Object.class);
    }


    public InfoDTO transactionFeeEstimate(TransactionReq transactionReq) {
        return buildRpcRequestPost(TRANSACTION_FEE_ESTIMATE).data(transactionReq).send(InfoDTO.class);
    }

    public RuntimeSpecResp runtimeSpec() {
        return buildRpcRequestGet(RUNTIME_SPEC).send(RuntimeSpecResp.class);
    }


    public TransactionMaterialResp transactionMaterial() {
        return buildRpcRequestGet(TRANSACTION_MATERIAL).send(TransactionMaterialResp.class);
    }

    private Request buildRpcRequestPost(String path) {
        String url = StringUtils.join(baseUrl, path);
        return new Request().url(url).setContentType(ContentType.JSON_UTF8).method(Method.POST);
    }

    private Request buildRpcRequestGet(String path, Object... params) {
        String format = String.format(path, params);
        String url = StringUtils.join(baseUrl, format);
        return new Request().url(url).method(Method.GET);
    }


    public NodeNetwork nodeNetwork() {
        return buildRpcRequestGet(NODE_NETWORK).send(NodeNetwork.class);
    }

    public NodeVersion nodeVersion() {
        return buildRpcRequestGet(NODE_VERSION).send(NodeVersion.class);
    }

    public PalletStakingProgress palletsStakingProgress() {
        return buildRpcRequestGet(PALLETS_STAKING_PROGRESS).send(PalletStakingProgress.class);

    }

    public PalletStorage palletsStorage(String palletId) {
        return buildRpcRequestGet(PALLETS_STORAGE, palletId).send(PalletStorage.class);

    }

    public PalletStorageItem palletStorageItem(String palletId, String storageItem) {
        return palletStorageItem(palletId, storageItem, null, null);
    }

    public PalletStorageItem palletStorageItem(String palletId, String storageItem, String key1) {
        return palletStorageItem(palletId, storageItem, key1, null);
    }

    public PalletStorageItem palletStorageItem(String palletId, String storageItem, String key1, String key2) {
        StringBuilder sb = new StringBuilder(PALLETS_STORAGE_ITEM);
        if (key1 != null) {
            sb.append("?key1=").append(key1);
            if (key2 != null) {
                sb.append("&key2=").append(key2);
            }
        }
        return buildRpcRequestGet(sb.toString(), palletId, storageItem).send(PalletStorageItem.class);
    }

    public Object runtimeMetadata() {
        return buildRpcRequestGet(RUNTIME_METADATA).send(Object.class);
    }

    public RuntimeCode runtimeCode() {
        return buildRpcRequestGet(RUNTIME_CODE).send(RuntimeCode.class);

    }

    public Object transactionDryRun(TransactionReq transactionReq) {
        return buildRpcRequestPost(TRANSACTION_DRY_RUN).data(transactionReq).send(Object.class);
    }

    public AccountStakingInfo accountStakingInfo(String accountId) {
        return buildRpcRequestGet(ACCOUNTS_STAKING_INFO, accountId).send(AccountStakingInfo.class);
    }

    public AccountStakingPayouts accountStakingPayouts(String accountId) {
        return buildRpcRequestGet(ACCOUNTS_STAKING_PAYOUTS, accountId).send(AccountStakingPayouts.class);

    }


    public String stakingCurrentEra() {
        String palletId = "staking";
        String storageItem = "currentEra";
        return palletStorageItem(palletId, storageItem).getValue().toString();
    }

    /**
     * 验证人节点总质押金额，验证人抵押金额，质押详情
     *
     * @param accountId 验证人地址
     * @return Exposure
     */
    public Exposure stakingErasStakers(String accountId) {
        String palletId = "staking";
        String storageItem = "ErasStakers";
        String key1 = stakingCurrentEra();
        JSONObject jsonObject = (JSONObject) palletStorageItem(palletId, storageItem, key1, accountId).getValue();
        return jsonObject.toJavaObject(Exposure.class);
    }

    /**
     * 获取积分
     *
     * @return Exposure
     */
    public IndividualExposure stakingErasRewardPoints() {
        String palletId = "staking";
        String storageItem = "ErasRewardPoints";
        String key1 = stakingCurrentEra();
        JSONObject jsonObject = (JSONObject) palletStorageItem(palletId, storageItem, key1).getValue();
        return jsonObject.toJavaObject(IndividualExposure.class);
    }


    /**
     * 计算成百分比要除以10的10次方 * 100
     *
     * @param accountId
     * @return
     */
    public Commission stakingCommission(String accountId) {
        String palletId = "staking";
        String storageItem = "ErasValidatorPrefs";
        String key1 = stakingCurrentEra();
        JSONObject jsonObject = (JSONObject) palletStorageItem(palletId, storageItem, key1, accountId).getValue();
        return jsonObject.toJavaObject(Commission.class);
    }


    public BigDecimal totalIssuance() {
        String palletId = "balances";
        String storageItem = "totalIssuance";
        return new BigDecimal(palletStorageItem(palletId, storageItem).getValue().toString());
    }

    public Ledger stakingLedger(String accountId) {
        String palletId = "staking";
        String storageItem = "ledger";
        JSONObject jsonObject = (JSONObject) palletStorageItem(palletId, storageItem, accountId).getValue();
        return jsonObject.toJavaObject(Ledger.class);
    }

    /**
     * 查询提名人信息
     *
     * @param accountId
     * @return
     */
    public Nominators nominators(String accountId) {
        String palletId = "staking";
        String storageItem = "Nominators";
        JSONObject jsonObject = (JSONObject) palletStorageItem(palletId, storageItem, accountId).getValue();
        return jsonObject.toJavaObject(Nominators.class);
    }

    /**
     * 获取总节点数
     * @return 节点数
     */
    public long validatorCount() {
        String palletId = "staking";
        String storageItem = "ValidatorCount";
        return Long.valueOf(palletStorageItem(palletId, storageItem).getValue().toString());
    }


    /**
     * 获取总节点数
     * @return 节点数
     */
    public BigDecimal validatorReward() {
        String palletId = "staking";
        String storageItem = "ErasValidatorReward";
        String key1 = stakingCurrentEra();
        key1 = String.valueOf(Integer.valueOf(key1) -1);
        Object object = palletStorageItem(palletId, storageItem, key1, null).getValue();
        return new BigDecimal(object.toString());
    }

    /**
     * 获取节点竞拍信息
     * @return 竞拍信息
     */
    public BiddingInfoDTO getBiddingInfoDTO() {
        return buildRpcRequestGet("/experimental/paras/crowdloans").send(BiddingInfoDTO.class);
    }
}
