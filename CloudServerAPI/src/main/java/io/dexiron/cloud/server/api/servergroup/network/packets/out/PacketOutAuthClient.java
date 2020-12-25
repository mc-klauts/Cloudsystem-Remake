package io.dexiron.cloud.server.api.servergroup.network.packets.out;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class PacketOutAuthClient implements Packet {

    private final String name;
    private final String ip;
    private final String uuid;
    private final Long time;


    public PacketOutAuthClient(String name, String ip, String uuid, Long time) {
        this.name = name;
        this.ip = ip;
        this.uuid = uuid;
        this.time = time;
    }

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {

        byteBuf.writeUTF(this.name);
        byteBuf.writeUTF(this.ip);
        byteBuf.writeUTF(this.uuid);
        byteBuf.writeLong(this.time);
        /**  byteBuf.writeUTF(this.name);
         byteBuf.writeUTF(this.ip);
         byteBuf.writeUTF(this.uuid);
         byteBuf.writeLong(this.time);**/
    }
}
