package com.atoken.request;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.account.atom.*;
import com.atoken.blockchain.utils.Final;
import com.atoken.blockchain.utils.SecurityUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class AtomRequestTest {
    private AtomClient atomClient = new AtomClient("http://39.96.194.39", "26657", "1317", null);

    @Test
    public void testBase() throws DecoderException, InvalidProtocolBufferException {
        byte[] hash = Base64.decode("yQHwYl3uCkKoo2GaChRnd+THLQ2RM87nEZrE19910Z28ABIUWW/t8AtIMwcyU0sT32RcMDI9GF0aEAoFdWF0b20SBzEwMDAwMDASEwoNCgV1YXRvbRIEMzEwMRCd8gEaagom61rphyEDoJPxlcjRoNDtZ9xMdvs+lRzFaHe2dl2P5R2yVCWrsHISQKkqX5H1zXAIJuC57yw0Yb03Fwy75VRip0ZBtLiYsUqkOsPUoQZAhDNP+6LY+RUwz/nVzedkF0S29NZ32QXdGv0=");
        String hashStr = SecurityUtil.encoderBySha256(hash);
        System.out.println("hash = " + hashStr);
        byte[] h = Hex.decodeHex("7E814DB5D4915735AAACD7B38BAE9514D42DBB93C7FF98CBE7DCA18788FF3C82");
        String hh = Base64.toBase64String(h);
        System.out.println("hh = " + hh);
        String hHex = Hex.encodeHexString(hash);
        System.out.println("hHex = " + hHex);
        String adHex = Hex.encodeHexString("cosmos167w96tdvmazakdwkw2u57227eduula2cy572lf".getBytes());
        System.out.println("adHex = " + adHex);
        byte[] puhash = Base64.decode("ArpZkCzdlZbia15dulbX2t/O87jhpK2dUhT5htQHOh9M");
        String puhashs = Hex.encodeHexString(puhash);
        System.out.println("puhashs = " + puhashs);

        String hhhh = Hex.encodeHexString("tendermint/PubKeySecp256k1".getBytes());
        System.out.println("hhhh = " + hhhh);

        String baseStr = Base64.toBase64String(Hex.decodeHex("7b2274797065223a22636f736d6f732d73646b2f4d736744656c6567617465222c2276616c7565223a7b2264656c656761746f725f61646472657373223a22636f736d6f73316e73766a7a747565393836616e676c76716a70757163786a377273736e616636366d36656736222c2276616c696461746f725f61646472657373223a22636f736d6f7376616c6f70657231717338746e773274386c36616d747a7664656d6e6e737139647a6b306167307a3532757a6179222c22616d6f756e74223a7b2264656e6f6d223a227561746f6d222c22616d6f756e74223a223139333030227d7d7d"));
        System.out.println("baseStr = " + baseStr);
    }

    @Test
    public void testGetCurrentBlockHeight() {
        Integer currentCurrent = atomClient.getCurrentBlockHeight();
        System.out.println("currentCurrent = " + currentCurrent);
    }
    
    @Test
    public void testGetAtomNodeStatus() {
        AtomNodeStatus atomNodeStatus = atomClient.getAtomNodeStatus();
        System.out.println("JSON.toJSONString(atomNodeStatus) = " + JSON.toJSONString(atomNodeStatus));
    }

    @Test
    public void testGetBlockByHeight() {
        AtomBlockInfo atomBlockInfo = atomClient.getBlockByHeight(154918);
        System.out.println("JSON.toJSONString(atomBlockInfo) = " + JSON.toJSONString(atomBlockInfo));
    }

    @Test
    public void testGetTxByTxHash() {
        AtomTxInfo atomTxInfo = atomClient.getTxByTxHash("B99CDDED532AABEDCCFF87841438E59FC83DDA52C93BA36C3091EF718ECDF7AE");
        System.out.println("JSON.toJSONString(atomTxInfo) = " + JSON.toJSONString(atomTxInfo));
    }

    @Test
    public void testGetBalanceByAddress() {
        List<AtomAmount> atomAmounts = atomClient.getBalanceByAddress("cosmos1vam7f3edpkgn8nh8zxdvf47lwhgem0qqu4n36u");
        System.out.println("JSON.toJSONString(atomAmounts) = " + JSON.toJSONString(atomAmounts));
    }

    @Test
    public void testGetAtomAccountByAddress() {
        AtomAccount atomAccount = atomClient.getAtomAccountByAddress("cosmos15n27h4j0maydzw3l4dymnkk3w9s3hcg5nzp9er");
        System.out.println("JSON.toJSONString(atomAccount) = " + JSON.toJSONString(atomAccount));
    }
    
    @Test
    public void testGetAtomValidators() {
        List<AtomValidator> atomValidators = atomClient.getAtomValidators();
        System.out.println("JSON.toJSONString(atomValidators) = " + JSON.toJSONString(atomValidators));
    }

    @Test
    public void testGetDelegatorRewardsByValidator() {
        List<AtomAmount> atomAmounts = atomClient.getDelegatorRewardsByValidator("cosmos16m93gjfqvnjajzrfyszml8qm92a0w67ntjhd3d", "cosmosvaloper14k4pzckkre6uxxyd2lnhnpp8sngys9m6hl6ml7");
        System.out.println("JSON.toJSONString(atomAmounts) = " + JSON.toJSONString(atomAmounts));
    }

    @Test
    public void testGetDelegatorRewards() {
        List<AtomAmount> atomAmounts = atomClient.getDelegatorRewards("cosmos1wu89g797tccthatp52m5zar45z80zljnr626dl");
        System.out.println("JSON.toJSONString(atomAmounts) = " + JSON.toJSONString(atomAmounts));
    }
    
    @Test
    public void testGetStakingPool() {
        List<AtomValidator> atomValidators = atomClient.getAtomValidators();
        Final<BigDecimal> allAmount = Final.newFinal(BigDecimal.ZERO);
        atomValidators.forEach(atomValidator -> allAmount.setType(allAmount.getType().add(atomValidator.getTokens())));
        System.out.println("allAmount.getType() = " + allAmount.getType());
        AtomStakingPool atomStakingPool = atomClient.getAtomStakingPool();
        System.out.println("JSON.toJSONString(atomStakingPool) = " + JSON.toJSONString(atomStakingPool));
    }
    
    @Test
    public void testGetAtomDelegatorsByValidator() {
        List<AtomDelegator> atomDelegators = atomClient.getAtomDelegatorsByValidator("cosmosvaloper14k4pzckkre6uxxyd2lnhnpp8sngys9m6hl6ml7");
        System.out.println("JSON.toJSONString(atomDelegators) = " + JSON.toJSONString(atomDelegators));
    }

    @Test
    public void testGetUnbondingDelegationsByValidator() {
        AtomUnbondingDelegation atomUnbondingDelegation = atomClient.getUnbondingDelegationsByValidator("cosmos15n27h4j0maydzw3l4dymnkk3w9s3hcg5nzp9er", "cosmosvaloper1qwl879nx9t6kef4supyazayf7vjhennyh568ys");
        System.out.println("atomUnbondingDelegation = " + JSON.toJSONString(atomUnbondingDelegation));
    }
    
    @Test
    public void testGetInflation() {
        Double inflation = atomClient.getInflation();
        System.out.println("inflation = " + inflation);
    }

    @Test
    public void testEstimateGas() {
        AtomEstimateInfo<AtomEstimateAmount> atomEstimateInfo = new AtomEstimateInfo<>();
        atomEstimateInfo.setAmount(new AtomEstimateAmount());
        atomEstimateInfo.setDelegatorAddress("cosmos1g9ahr6xhht5rmqven628nklxluzyv8z9jqjcmc");
        atomEstimateInfo.setValidatorAddress("cosmosvaloper1depk54cuajgkzea6zpgkq36tnjwdzv4avv9cxd");
        AtomBaseReq atomBaseReq = new AtomBaseReq();
        atomBaseReq.setFrom("cosmos1g9ahr6xhht5rmqven628nklxluzyv8z9jqjcmc");
        atomBaseReq.setGas("auto");
        atomEstimateInfo.setBaseReq(atomBaseReq);
        System.out.println("atomEstimateInfo = " + JSON.toJSONString(atomEstimateInfo));
        Integer gas = atomClient.estimateMsgDelegateGas(atomEstimateInfo);
        System.out.println("gas = " + gas);
        
        gas = atomClient.estimateMsgUndelegateGas(atomEstimateInfo);
        System.out.println("gas = " + gas);
        atomEstimateInfo.setAmount(null);
        atomEstimateInfo.setDelegatorAddress("cosmos1g9ahr6xhht5rmqven628nklxluzyv8z9jqjcmc");
        atomEstimateInfo.setValidatorAddress("cosmosvaloper1depk54cuajgkzea6zpgkq36tnjwdzv4avv9cxd");

        gas = atomClient.estimateMsgWithdrawDelegationRewardGas(atomEstimateInfo);
        System.out.println("gas = " + gas);
    }

    @Test
    public void testGetDelegationsByValidator() {
        AtomDelegation atomDelegation = atomClient.getDelegationByValidator("cosmos16m93gjfqvnjajzrfyszml8qm92a0w67ntjhd3d", "cosmosvaloper14k4pzckkre6uxxyd2lnhnpp8sngys9m6hl6ml7");
        System.out.println("JSON.toJSONString(atomDelegation) = " + JSON.toJSONString(atomDelegation));
    }
    
    @Test
    public void testGetStakingInfo() {
        AtomStakingInfo atomStakingInfo = atomClient.getStakingInfo();
        System.out.println("JSON.toJSONString(atomStakingInfo) = " + JSON.toJSONString(atomStakingInfo));
    }

    @Test
    public void testGetMintingInfo() {
        AtomMintingInfo atomMintingInfo = atomClient.getMintingInfo();
        System.out.println("JSON.toJSONString(atomMintingInfo) = " + JSON.toJSONString(atomMintingInfo));
    }
    
    @Test
    public void testGetValidatorByAddress() {
        AtomValidator atomValidator = atomClient.getValidatorByAddress("cosmosvaloper1qwl879nx9t6kef4supyazayf7vjhennyh568ys");
        System.out.println("JSON.toJSONString(atomValidator) = " + JSON.toJSONString(atomValidator));
    }

    @Test
    public void testSendTx() {
        System.out.println(atomClient.sendTx("{\"tx\":{\"msg\":[{\"type\":\"cosmos-sdk/MsgSend\",\"value\":{\"amount\":[{\"amount\":\"30\",\"denom\":\"uatom\"}],\"from_address\":\"cosmos1fujw8ngwjvatxh4fcgd0ehkkaq59jxdlt4zzq3\",\"to_address\":\"cosmos14dsy2k6mc8qhml7xpqxnvdsp92yppqm2cvsztk\"}}],\"fee\": {\"gas\": \"40000\",\"amount\":[{\"denom\": \"uatom\",\"amount\":\"900\"}]},\"memo\": \"\",\"signatures\": [{\"signature\": \"f4sHL8G67a5rk+JiG5Lpt+nmgUAJQ3YhzpNaupvQDwxQmbn2n0Wukm42xE8QEDHv9bSe0mve9CqeamLZJXiKyg==\",\"pub_key\": {\"type\": \"tendermint/PubKeySecp256k1\",\"value\": \"Am0DyPpZtO/Zm2cBuTeh6JXA0G1BSMLDTD06vZQIis/j\"}}],\"account_number\": \"7639\",\"sequence\": \"25\"},\"mode\": \"sync\"}"));
    }
}
