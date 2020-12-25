package io.dexiron.cloud.wrapper.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class PacketOutWrapperRegister implements Packet {

    private final String name;
    private final State state;
    private final Long ram;

    public PacketOutWrapperRegister(String name, State state, Long ram) {
        this.name = name;
        this.state = state;
        this.ram = ram;
    }


    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeLong(this.ram);
        byteBuf.writeInt(this.state.ordinal());
    }

    public enum State {
        ONLINE, OFFLINE;
    }

}
