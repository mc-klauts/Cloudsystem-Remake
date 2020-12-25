package io.dexiron.cloud.lib.config.networking.decoder;


import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.lib.config.networking.registry.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    protected void decode(final ChannelHandlerContext ctx, final ByteBuf byteBuf, final List<Object> output ) throws Exception{

        if ( byteBuf instanceof EmptyByteBuf) return;

        final int id = byteBuf.readInt();
        final Packet packet = PacketRegistry.getPacketRegistry().getPacketById( id, PacketRegistry.PacketDirection.IN );
        if ( packet == null )
            throw new NullPointerException( "Could not find packet by id " + id + "!" );
        packet.read( new ByteBufInputStream( byteBuf ) );
        output.add( packet );
    }
}