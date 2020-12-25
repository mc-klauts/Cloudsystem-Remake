package io.dexiron.cloud.lib.config.networking.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.Getter;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class NetworkingServerImpl implements NetworkingServer {
    private final boolean EPOLL = Epoll.isAvailable();

    @Getter
    private EventLoopGroup bossGroup;

    @Getter
    private EventLoopGroup workerGroup;

    @Getter
    public ExecutorService executorService = Executors.newSingleThreadExecutor();

    private SslContext sslCtx;

    public NetworkingServerImpl() {
        try{
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContext.newServerContext( ssc.certificate(), ssc.privateKey() );
            ssc.delete();
        } catch (CertificateException | SSLException e){
            e.printStackTrace();
        }
    }

    public void start(int port, Consumer<Channel> channelConsumer) {
        executorService.execute(() -> {
            this.bossGroup = this.EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
            this.workerGroup = this.EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
            final ChannelFuture future = new ServerBootstrap().group(this.bossGroup, this.workerGroup).channel(this.EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    if ( sslCtx != null ){
                        channel.pipeline().addLast( sslCtx.newHandler( channel.alloc() ) );
                    }
                    channelConsumer.accept(channel);
                }

            }).bind(port).syncUninterruptibly();
            System.out.println(String.format("Networking server is started on port: %s", port));
        });
    }

    @Override
    public boolean shutdown() {
        if (!(bossGroup.isShutdown() && workerGroup.isShutdown())) {
            bossGroup.shutdownGracefully(3, 3, TimeUnit.MILLISECONDS);
            workerGroup.shutdownGracefully(3, 3, TimeUnit.MILLISECONDS);
            executorService.shutdownNow();
            System.out.println("Networking server successfully shutdown!");
        }
        return false;
    }
}
