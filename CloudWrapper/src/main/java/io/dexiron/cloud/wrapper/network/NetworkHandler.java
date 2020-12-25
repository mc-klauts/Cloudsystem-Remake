package io.dexiron.cloud.wrapper.network;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;

public class NetworkHandler extends SimpleChannelInboundHandler<Packet> {

    /**
     * Handle the incoming protocoll
     *
     * @param ctx    ChannelHandler context for handling channel
     * @param packet Packet which you want to handle
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        try {
            packet.handle(ctx.channel());
            System.out.println(packet.toString());
        } catch (IOException e) {

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.exit(0);

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

        System.exit(0);

    }
}
