package io.dexiron.cloud.wrapper.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class PacketOutWrapperReady implements Packet {

    private final String name;
    private final boolean ready;

    public PacketOutWrapperReady(String name, boolean ready) {
        this.name = name;
        this.ready = ready;
    }

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeBoolean(this.ready);
    }
}
