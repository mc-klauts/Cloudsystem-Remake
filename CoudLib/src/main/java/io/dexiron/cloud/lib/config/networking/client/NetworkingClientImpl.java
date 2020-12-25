package io.dexiron.cloud.lib.config.networking.client;

import io.dexiron.cloud.lib.config.CloudWrapperConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.Getter;
import lombok.Setter;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.SECONDS;

public class NetworkingClientImpl implements NetworkingClient {

    @Getter
    @Setter
    private EventLoopGroup workerGroup;

    @Getter
    private boolean EPOLL = Epoll.isAvailable();

    @Getter
    @Setter
    private ChannelFuture future;

    @Setter
    @Getter
    private boolean shutDown;

    //TODO: Use thread

    private SslContext sslContext;

    public NetworkingClientImpl() {
        try {
            sslContext = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Channel bindOnServer(CloudWrapperConfig cloudWrapperConfig, Consumer<Channel> channelConsumer) {
        setWorkerGroup(this.EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup());

        setFuture(new Bootstrap()
                .group(getWorkerGroup())
                .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        channel.pipeline().addLast(sslContext.newHandler(channel.alloc(), cloudWrapperConfig.getHost(), cloudWrapperConfig.getPort().intValue()));
                        channelConsumer.accept(channel);

                    }
                }).connect(new InetSocketAddress(cloudWrapperConfig.getHost(), cloudWrapperConfig.getPort().intValue())).syncUninterruptibly());
        System.out.println(String.format("Networking client is started on host with port: %s and channel: %s", cloudWrapperConfig.getHost(), cloudWrapperConfig.getPort(), getFuture().channel().toString()));
        Channel channel = getFuture().channel();
        return channel;
    }

    @Override
    public Channel bindOnServer(String host, int port, Consumer<Channel> channelConsumer) {
        setWorkerGroup(this.EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup());

        setFuture(new Bootstrap()
                .group(getWorkerGroup())
                .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        channel.pipeline().addLast(sslContext.newHandler(channel.alloc(), host, port));
                        channelConsumer.accept(channel);

                    }
                }).connect(new InetSocketAddress(host, port)).syncUninterruptibly());
        System.out.println(String.format("Networking client is started on host with port: %s and channel: %s", host, port, getFuture().channel().toString()));
        Channel channel = getFuture().channel();
        return channel;
    }


    @Override
    public boolean shutdown() {
        if (!isShutDown()) {
            workerGroup.shutdownGracefully();
            workerGroup.terminationFuture().syncUninterruptibly();

            try {
                SECONDS.sleep(2);
            } catch (InterruptedException e) {

            }
            System.out.println("Networking client successfully shutdown!");
            setShutDown(true);
        }
        return false;
    }
}
