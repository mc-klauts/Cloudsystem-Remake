package io.dexiron.cloud.lib.config.auth;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NettySession {

    String ip;
    String name;
    String sessionId;
    long time;
    Channel channel;

}
