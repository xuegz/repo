package com.atoken.update;

import com.atoken.update.dingding.annotaion.AlarmScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.atoken.update")
@MapperScan(basePackages = "com.atoken.update.dao")
@EnableEurekaClient
@AlarmScan(basePackages = "com.atoken.update.notification")
@EnableAsync
public abstract class BaseApplication {
}
