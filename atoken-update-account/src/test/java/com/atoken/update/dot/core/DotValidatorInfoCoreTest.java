package com.atoken.update.dot.core;

import com.atoken.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class DotValidatorInfoCoreTest extends BaseTest {

    @Autowired
    private DotValidatorInfoCore dotValidatorInfoCore;

    @Test
    public void validatorHandler() {
        dotValidatorInfoCore.validatorHandler();
    }
}