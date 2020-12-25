package io.dexiron.cloud.wrapper.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.wrapper.CloudBootsrap;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public class PacketInStartGameServer implements Packet {

    private String name;
    private String group;
    private int ram;
    private boolean isStatic;
    private int maxPlayers;


    public PacketInStartGameServer() {

    }


    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.name = byteBuf.readUTF();
        this.group = byteBuf.readUTF();
        this.ram = byteBuf.readInt();
        this.isStatic = byteBuf.readBoolean();
        this.maxPlayers = byteBuf.readInt();
    }

    @Override
    public Packet handle(Channel channel) throws IOException {
        CloudBootsrap.getInstance().getServerManager().startServer(this.name, this.group, this.ram, this.isStatic);
        return null;
    }
}
