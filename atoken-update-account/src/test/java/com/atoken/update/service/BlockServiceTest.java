package com.atoken.update.service;

import com.alibaba.fastjson.JSON;
import com.atoken.BaseTest;
import com.atoken.update.entity.Block;
import com.atoken.update.entity.coin.account.staking.ValidatorEO;
import com.atoken.update.service.coin.account.AccountStakingService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BlockServiceTest extends BaseTest {
    @Autowired
    private BlockService blockService;
    @Autowired
    private AccountStakingService stakingService;
    
    @Test
    public void testGetBlock() {
        List<Block> blockList = blockService.selectBlocks("atom_query");
        System.out.println("JSON.toJSONString(blockList) = " + JSON.toJSONString(blockList));
    }

    @Test
    public void testSaveBlock() {
        Block block = new Block();
        block.setPrevious("sss");
        block.setTimestamp(10000l);
        block.setTxCount(1);
        block.setHeight(22);
        block.setHash("sdadac");
        blockService.insertBlock("atom_query", block);
    }

    @Test
    public void testMysqlTime() {
        List<ValidatorEO> dbBondedValidators = stakingService.selectBondedValidators("tomo");
    }
}
