package io.dexiron.cloud.server.api.proxy.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class PacketOutGameServerAction implements Packet {

    private final String name;
    private final int amount;
    private final Direction direction;

    public PacketOutGameServerAction(String name, int amount, Direction direction) {
        this.name = name;
        this.amount = amount;
        this.direction = direction;
    }

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeInt(this.amount);
        byteBuf.writeInt(this.direction.ordinal());
    }

    public enum Direction {
        START, STOP, STOPGROUP;
    }

}
