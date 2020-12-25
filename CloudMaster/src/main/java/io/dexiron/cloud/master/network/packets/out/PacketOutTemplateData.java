package io.dexiron.cloud.master.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public class PacketOutTemplateData implements Packet {

    private final String group;
    private final int ram;
    private final boolean isStatic;
    private final int maxServer;
    private final int minServer;


    public PacketOutTemplateData(String group, int ram, boolean isStatic, int maxServer, int minServer) {
        this.group = group;
        this.ram = ram;
        this.isStatic = isStatic;
        this.maxServer = maxServer;
        this.minServer = minServer;
    }



    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.group);
        byteBuf.writeInt(this.ram);
        byteBuf.writeInt(this.maxServer);
        byteBuf.writeInt(this.minServer);
        byteBuf.writeBoolean(this.isStatic);
    }

    @Override
    public Packet handle(Channel channel) throws IOException {
        
        return null;
    }
}
