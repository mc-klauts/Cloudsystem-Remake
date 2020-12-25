package io.dexiron.cloud.lib.config.networking.client;

import io.dexiron.cloud.lib.config.CloudWrapperConfig;
import io.netty.channel.Channel;

import java.util.function.Consumer;

public interface NetworkingClient {

    Channel bindOnServer(CloudWrapperConfig cloudWrapperConfig, Consumer<Channel> channelConsumer);

    Channel bindOnServer(String host, int port, Consumer<Channel> channelConsumer);

    boolean shutdown();

}
