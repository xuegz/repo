package com.atoken.request;

import com.atoken.blockchain.http.request.account.dot.sidecar.DotSidecarClient;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.*;
import org.junit.Test;

import java.math.BigDecimal;

public class DotSidecarClientTest {

    private final DotSidecarClient dotSidecarClient = new DotSidecarClient("http://39.96.194.39:28080");

    @Test
    public void testBalanceInfo() {
        String addr = "1rupXt37Uvb9AZjs3h7XtwErg7igm6zNJUu8YHeAKufzWDK";
        BalanceInfoResp balanceInfoResp = dotSidecarClient.balanceInfo(addr);
        System.out.println(balanceInfoResp);
    }

    @Test
    public void testVestingInfo() {
        String addr = "1rupXt37Uvb9AZjs3h7XtwErg7igm6zNJUu8YHeAKufzWDK";
        AccountVestingInfo accountVestingInfo = dotSidecarClient.vestingInfo(addr);
        System.out.println(accountVestingInfo);
    }

    @Test
    public void testBlocksHeight() {
        Integer blockHeight = 1604533;
        BlockResp blockResp = dotSidecarClient.blocks(blockHeight);
        System.out.println(blockResp);
    }

    @Test
    public void testBlocksHash() {
        String blockHash = "0x2c909311a743af60c1f1d5659f480bae72cef9a97256ba531f6888192b27ba7f";
        BlockResp blockResp = dotSidecarClient.blocks(blockHash);
        System.out.println(blockResp);
    }

    @Test
    public void testBlocksHead() {
        BlockResp blockResp = dotSidecarClient.blocksHead();
        System.out.println(blockResp);
    }

    @Test
    public void testNodeNetwork() {
        NodeNetwork nodeNetwork = dotSidecarClient.nodeNetwork();
        System.out.println(nodeNetwork);
    }


    @Test
    public void testNodeVersion() {
        NodeVersion nodeVersion = dotSidecarClient.nodeVersion();
        System.out.println(nodeVersion);
    }


    @Test
    public void testPalletsStakingProgress() {
        PalletStakingProgress palletStakingProgress = dotSidecarClient.palletsStakingProgress();
        System.out.println(palletStakingProgress);
    }

    @Test
    public void testPalletsStorage() {
        String palletId = "RandomnessCollectiveFlip";
        PalletStorage palletStorage = dotSidecarClient.palletsStorage(palletId);
        System.out.println(palletStorage);
    }

    @Test
    public void testPalletStorageItem() {
        String palletId = "system";
        String storageItem = "extrinsicCount";
        PalletStorageItem palletsStorageItem = dotSidecarClient.palletStorageItem(palletId, storageItem);
        System.out.println(palletsStorageItem);
    }

    @Test
    public void testStakingCurrentEra() {
        String stakingCurrentEra = dotSidecarClient.stakingCurrentEra();
        System.out.println(stakingCurrentEra);
    }

    @Test
    public void testStakingErasStakers() {
        String accountId = "12pgR5dyEkUP6iTR9u7b74Nz7uCsCYKpkFfA5LFbQPjGwNHL";
        Exposure exposure = dotSidecarClient.stakingErasStakers(accountId);
        System.out.println(exposure);
    }

    @Test
    public void testStakingErasRewardPoints() {
        IndividualExposure exposure = dotSidecarClient.stakingErasRewardPoints();
        System.out.println(exposure);
    }

    @Test
    public void testStakingCommission() {
        String accountId = "12pgR5dyEkUP6iTR9u7b74Nz7uCsCYKpkFfA5LFbQPjGwNHL";
        Commission exposure = dotSidecarClient.stakingCommission(accountId);
        System.out.println(exposure);
    }

    @Test
    public void testRuntimeMetadata() {
        Object runtimeMetadata = dotSidecarClient.runtimeMetadata();
        System.out.println(runtimeMetadata);
    }

    @Test
    public void testRuntimeCode() {
        RuntimeCode runtimeCode = dotSidecarClient.runtimeCode();
        System.out.println(runtimeCode);
    }


    @Test
    public void testRuntimeSpec() {
        RuntimeSpecResp runtimeSpecResp = dotSidecarClient.runtimeSpec();
        System.out.println(runtimeSpecResp);
    }


    @Test
    public void testTransactionPool() {
        TransactionPoolResp transactionPoolResp = dotSidecarClient.transactionPool();
        System.out.println(transactionPoolResp);
    }


    @Test
    public void testTransaction() {
        String tx = "0x350284c0ae9bf031bfbcafc1a249854119b66d58cd7e9ec2604e66a7af5c9439f488640178f7b0312ee4cce3c091c3176ae20f31973991329334d73b857466716591375e24acceda279b3a539e2b589bd81585a905b25d8cc07fbba872aaa5b4cc08cd850044000500c0ae9bf031bfbcafc1a249854119b66d58cd7e9ec2604e66a7af5c9439f488640700c817a804";
        Object transactionResp = dotSidecarClient.transaction(TransactionReq.builder().tx(tx).build());
        System.out.println(transactionResp);
    }

    @Test
    public void testTransactionDryRun() {
        String tx = "0x350284c0ae9bf031bfbcafc1a249854119b66d58cd7e9ec2604e66a7af5c9439f488640178f7b0312ee4cce3c091c3176ae20f31973991329334d73b857466716591375e24acceda279b3a539e2b589bd81585a905b25d8cc07fbba872aaa5b4cc08cd850044000500c0ae9bf031bfbcafc1a249854119b66d58cd7e9ec2604e66a7af5c9439f488640700c817a804";
        Object transactionResp = dotSidecarClient.transactionDryRun(TransactionReq.builder().tx(tx).build());
        System.out.println(transactionResp);
    }


    @Test
    public void testTransactionFeeEstimate() {
        String tx = "0x350284c0ae9bf031bfbcafc1a249854119b66d58cd7e9ec2604e66a7af5c9439f488640178f7b0312ee4cce3c091c3176ae20f31973991329334d73b857466716591375e24acceda279b3a539e2b589bd81585a905b25d8cc07fbba872aaa5b4cc08cd850044000500c0ae9bf031bfbcafc1a249854119b66d58cd7e9ec2604e66a7af5c9439f488640700c817a804";
        InfoDTO transactionResp = dotSidecarClient.transactionFeeEstimate(TransactionReq.builder().tx(tx).build());
        System.out.println(transactionResp);
    }

    @Test
    public void testTransactionMaterial() {
        TransactionMaterialResp transactionMaterialResp = dotSidecarClient.transactionMaterial();
        System.out.println(transactionMaterialResp);
    }


    @Test
    public void testAccountStakingInfo() {
        String accountId = "12pgR5dyEkUP6iTR9u7b74Nz7uCsCYKpkFfA5LFbQPjGwNHL";
        AccountStakingInfo accountStakingInfo = dotSidecarClient.accountStakingInfo(accountId);
        System.out.println(accountStakingInfo);
    }


    @Test
    public void testAccountStakingPayouts() {
        String accountId = "12pgR5dyEkUP6iTR9u7b74Nz7uCsCYKpkFfA5LFbQPjGwNHL";
        AccountStakingPayouts accountStakingPayouts = dotSidecarClient.accountStakingPayouts(accountId);
        System.out.println(accountStakingPayouts);
    }


    @Test
    public void testNominators() {
        String accountId = "12x4pXd3bKpkbbd5FjcmdZXpXcUhBEitbWeAw4WS3eAV6ecD";
        Nominators nominators = dotSidecarClient.nominators(accountId);
        System.out.println(nominators);

    }


    @Test
    public void validatorCount() {
        long count = dotSidecarClient.validatorCount();
        System.out.println(count/365/3600/1000);
    }

    @Test
    public void validatorReward() {
        BigDecimal count = dotSidecarClient.validatorReward();
//        System.out.println(count/365/24/3600/1000/1000000);
    }


    @Test
    public void totalIssuance() {
        BigDecimal count = dotSidecarClient.totalIssuance();
        System.out.println(count);

    }
}