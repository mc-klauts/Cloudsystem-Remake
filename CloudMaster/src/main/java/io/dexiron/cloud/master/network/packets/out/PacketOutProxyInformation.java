package io.dexiron.cloud.master.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class PacketOutProxyInformation implements Packet {

    private final String motd;
    private final Integer maxPlayers;
    private final boolean maintenance;

    public PacketOutProxyInformation(String motd, Integer maxPlayers, boolean maintenance) {
        this.motd = motd;
        this.maxPlayers = maxPlayers;
        this.maintenance = maintenance;
    }

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.motd);
        byteBuf.writeInt(this.maxPlayers);
        byteBuf.writeBoolean(this.maintenance);
    }
}
