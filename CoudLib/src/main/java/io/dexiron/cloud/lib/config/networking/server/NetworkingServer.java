package io.dexiron.cloud.lib.config.networking.server;

import io.netty.channel.Channel;

import java.util.function.Consumer;

public interface NetworkingServer {

    void start(int port, Consumer<Channel> channel);

    boolean shutdown();

}
