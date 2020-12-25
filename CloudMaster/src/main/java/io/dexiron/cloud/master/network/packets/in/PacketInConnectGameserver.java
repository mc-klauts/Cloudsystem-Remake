package io.dexiron.cloud.master.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.master.CloudBootsrap;
import io.dexiron.cloud.master.game.GameServer;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public class PacketInConnectGameserver implements Packet {

    private String name;
    private int port;

    public PacketInConnectGameserver() {
    }

    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.name = byteBuf.readUTF();
        this.port = byteBuf.readInt();
    }

    @Override
    public Packet handle(Channel channel) throws IOException {
        GameServer server = CloudBootsrap.getInstance().getCloudManager().getServerByName(name);
        server.setPort(port);
        server.onConnect(channel);
        return null;
    }
}
