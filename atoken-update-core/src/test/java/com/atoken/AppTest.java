package com.atoken;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(1573179851795l).plusMillis(TimeUnit.HOURS.toMillis(8)).atZone(ZoneId.systemDefault());
        System.out.println("zonedDateTime = " + zonedDateTime);
    }
}


