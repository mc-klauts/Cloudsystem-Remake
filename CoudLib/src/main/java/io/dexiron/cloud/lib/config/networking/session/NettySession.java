package io.dexiron.cloud.lib.config.networking.session;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NettySession {
    String ip;
    String sessionId;
    long time;
    boolean sessionOpen;
    Channel channel;
}
