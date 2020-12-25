package io.dexiron.cloud.master.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.master.CloudBootsrap;
import io.dexiron.cloud.master.wrapper.Wrapper;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public class PacketInWrapperRegister implements Packet {

    private String name;
    private State state;
    private Long ram;

    public PacketInWrapperRegister() {
    }

    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.name = byteBuf.readUTF();
        this.ram = byteBuf.readLong();
        this.state = State.class.getEnumConstants()[byteBuf.readInt()];
    }

    @Override
    public Packet handle(Channel channel) throws IOException {
        switch (state) {
            case ONLINE:
                Wrapper wrapper = new Wrapper(this.name, this.ram.intValue());
                wrapper.onConnect(channel);
                break;

            case OFFLINE:
                CloudBootsrap.getInstance().getCloudManager().getWrappperMap().get(this.name).onDisconnect();
                break;
        }
        return null;
    }

    public enum State {
        ONLINE, OFFLINE;
    }

}
