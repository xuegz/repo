package com.atoken.update.dingding.annotaion;

import com.atoken.update.dingding.annotaion.handler.DefaultAlarmHandler;

import java.lang.annotation.*;

/**
 * @author dfg
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SendAlarm {
    /**
     * 需要发送的消息内容
     * 如果需要替换某些值：{a.b}：使用在参数列表的某些值替换 ${a.b}:使用配置文件的值替换
     * @return
     */
    String value();

    /**
     * 提心服务提供商
     * @return
     */
    AlarmProvider provider() default AlarmProvider.DINGDING;

    /**
     * 提醒条件，哪些需要发送，哪些不需要发送
     * 如：a.b == 1 || a.b == 2
     * @return
     */
    String condition() default "";

    /**
     * 是否需要异步发送
     * @return
     */
    String async() default "false";

    /**
     * 哪些是需要发送的
     * @return
     */
    Class handler() default DefaultAlarmHandler.class;
}
