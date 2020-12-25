package io.dexiron.cloud.master.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public class PacketsOutRegisterGameServer implements Packet {

    private String name;
    private String adress;
    private int port;
    private Direction direction;

    public PacketsOutRegisterGameServer(String name, String adress, int port, Direction direction) {
        this.name = name;
        this.adress = adress;
        this.port = port;
        this.direction = direction;
    }

    public PacketsOutRegisterGameServer(){}

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeUTF(this.adress);
        byteBuf.writeInt(this.port);
        byteBuf.writeInt(this.direction.ordinal());
    }

    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.name = byteBuf.readUTF();
        this.adress = byteBuf.readUTF();
        this.port = byteBuf.readInt();
        this.direction = Direction.class.getEnumConstants()[byteBuf.readInt()];
    }

    @Override
    public Packet handle(Channel channel) throws IOException {
        return null;
    }

    public enum Direction {
        ADD, REMOVE;
    }

}
