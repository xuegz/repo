package com.atoken.update.binding;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Created by qingyun.yu on 2018/11/22.
 */
public interface MessageChannelBinding {
    String CHANNEL_TX_INFO = "coin_tx_info";

    @Output(MessageChannelBinding.CHANNEL_TX_INFO)
    MessageChannel txInfoChannel();
}
