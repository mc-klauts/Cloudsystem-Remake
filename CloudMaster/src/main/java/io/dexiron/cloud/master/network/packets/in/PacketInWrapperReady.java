package io.dexiron.cloud.master.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.master.CloudBootsrap;
import io.dexiron.cloud.master.wrapper.Wrapper;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public class PacketInWrapperReady implements Packet {

    private  String name;
    private  boolean ready;

    public PacketInWrapperReady() {

    }

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeBoolean(this.ready);
    }

    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.name = byteBuf.readUTF();
        this.ready = byteBuf.readBoolean();
    }

    @Override
    public Packet handle(Channel channel) throws IOException {

        Wrapper wrapper = CloudBootsrap.getInstance().getCloudManager().getWrappperMap().get(this.name);
        wrapper.setReady(this.ready);

        System.out.println("Templates were build and send!");

        return null;
    }
}
