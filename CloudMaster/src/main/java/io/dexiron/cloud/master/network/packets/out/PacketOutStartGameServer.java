package io.dexiron.cloud.master.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class PacketOutStartGameServer implements Packet {

    private final String name;
    private final String group;
    private final int ram;
    private final boolean isStatic;
    private final int maxPlayers;


    public PacketOutStartGameServer(String name, String group, int ram, boolean isStatic, int maxPlayers) {
        this.name = name;
        this.group = group;
        this.ram = ram;
        this.isStatic = isStatic;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeUTF(this.group);
        byteBuf.writeInt(this.ram);
        byteBuf.writeBoolean(this.isStatic);
        byteBuf.writeInt(this.maxPlayers);
    }
}
