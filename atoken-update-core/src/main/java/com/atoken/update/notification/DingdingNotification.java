package com.atoken.update.notification;

import com.atoken.update.dingding.annotaion.Alarm;
import com.atoken.update.dingding.annotaion.SendAlarm;

/**
 * @author dfg
 */
@Alarm
public interface DingdingNotification {
    String TEMPLATE_BLOCK_HANDLER = "孤块处理异常预警，币种：{coinName}, 高度：{height}, 异常：{exception}";

    /**
     * 区块更新异常报警
     * @param coinName
     * @param exception
     * @return
     */
    @SendAlarm("区块更新异常预警，币种：{coinName}, 异常：{exception}")
    boolean blocksExceptionAlarm(String coinName, String exception);

    /**
     * 孤块处理异常报警
     * @param coinName
     * @param height
     * @param exception
     * @return
     */
    @SendAlarm(TEMPLATE_BLOCK_HANDLER)
    boolean isolatedBlockExceptionAlarm(String coinName, Integer height, String exception);

    /**
     * 节点异常报警
     * @param coinName
     * @param height
     * @param exception
     * @return
     */
    @SendAlarm(TEMPLATE_BLOCK_HANDLER)
    boolean nodeExceptionAlarm(String coinName, Integer height, String exception);

    /**
     * 区块处理异常预警报警
     * @param coinName
     * @param height
     * @param exception
     * @return
     */
    @SendAlarm(TEMPLATE_BLOCK_HANDLER)
    boolean blockHandlerExceptionAlarm(String coinName, Integer height, String exception);

    /**
     * 交易处理异常预警报警
     * @param coinName
     * @param height
     * @param txHash
     * @param exception
     * @return
     */
    @SendAlarm("交易处理异常预警，币种：{coinName}, 高度：{height}, 交易hash：{txHash}, 异常：{exception}")
    boolean txHandlerExceptionAlarm(String coinName, Integer height, String txHash, String exception);
}
