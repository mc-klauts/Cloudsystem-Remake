package io.dexiron.cloud.server.api.servergroup.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class PacketOutConnectGameserver implements Packet {

    private final String name;
    private final int port;

    public PacketOutConnectGameserver(String name, int port) {
        this.name = name;
        this.port = port;
    }

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeInt(this.port);
    }
}
