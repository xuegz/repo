package com.atoken.update.dot.core;

import com.atoken.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: erwin
 * @time: 2021/4/8 5:39 下午
 */
public class DotUnbondedExpireCoreTest extends BaseTest {

    @Autowired
    private DotUnbondedExpireCore dotUnbondedExpireCore;

    @Test
    public void unbondedExpireHandler() {
        dotUnbondedExpireCore.unbondedExpireHandler();
    }
}