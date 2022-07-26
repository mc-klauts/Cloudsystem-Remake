package io.dexiron.cloud.master.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class PacketOutUpdateAPI implements Packet {
    private final String  name;
    private final String  extra;
    private final String  motd;
    private final String  state;
    private final Integer  onlinePlayers;

    public PacketOutUpdateAPI(String name, String extra, String motd, String state, Integer onlinePlayers) {
        this.name = name;
        this.extra = extra;
        this.motd = motd;
        this.state = state;
        this.onlinePlayers = onlinePlayers;
    }


    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeUTF(this.extra);
        byteBuf.writeUTF(this.motd);
        byteBuf.writeUTF(this.state);
        byteBuf.writeInt(onlinePlayers);
    }
}
