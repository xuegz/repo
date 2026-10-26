package com.atoken.update.atom;

import com.alibaba.fastjson.JSONObject;
import com.atoken.BaseTest;
import com.atoken.blockchain.http.request.account.atom.AtomClient;
import com.atoken.update.atom.core.AtomUnbondedExpireCore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AtomTest extends BaseTest {

//    @Autowired
//    private AtomClient atomClient;

    @Autowired
    private AtomUnbondedExpireCore atomUnbondedExpireCore;

    @Test
    public void unbonedExpireTest(){
        this.atomUnbondedExpireCore.unbondedExpireHandler();
    }

    /**
     * 测试用户质押信息
     */
    @Test
    public void getDelegationTest(){
        String addr = this.getAddr();
        AtomClient atomClient = new AtomClient("http://39.96.27.184","26657","1317",null);
        System.out.println(JSONObject.toJSONString(atomClient.getDelegations(addr)));
    }

    /**
     * 获取用户解绑信息
     */
    @Test
    public void getUnbondTest(){
        String addr = this.getAddr();
        AtomClient atomClient = new AtomClient("http://39.96.27.184","26657","1317",null);
        System.out.println(JSONObject.toJSONString(atomClient.getUnbondingDelegations(addr)));
    }

    public String getAddr(){
        return "cosmos15n27h4j0maydzw3l4dymnkk3w9s3hcg5nzp9er";
    }
}
