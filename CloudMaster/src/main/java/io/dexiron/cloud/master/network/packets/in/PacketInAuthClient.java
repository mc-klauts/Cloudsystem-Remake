package io.dexiron.cloud.master.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.master.CloudBootsrap;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class PacketInAuthClient implements Packet {

    private String name;
    private String ip;
    private String uuid;
    private Long time;


    public PacketInAuthClient() {
    }

    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.name = byteBuf.readUTF();
        this.ip = byteBuf.readUTF();
        this.uuid = byteBuf.readUTF();
        this.time = byteBuf.readLong();

    }

    @Override
    public Packet handle(Channel channel) throws IOException {

        CloudBootsrap.getInstance().getClientAuthAdapter().sessionCheck(this.name, this.ip, channel, this.time, this.uuid);
        return null;
    }
}
