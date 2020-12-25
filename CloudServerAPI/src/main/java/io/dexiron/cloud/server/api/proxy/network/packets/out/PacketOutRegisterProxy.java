package io.dexiron.cloud.server.api.proxy.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class PacketOutRegisterProxy implements Packet {

    private final String name;
    private final State state;

    public PacketOutRegisterProxy(String name, State state) {
        this.name = name;
        this.state = state;
    }

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeInt(this.state.ordinal());
    }

    public enum State {
        ONLINE, OFFLINE;
    }

}
