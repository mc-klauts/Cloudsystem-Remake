package io.dexiron.cloud.master.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.master.CloudBootsrap;
import io.dexiron.cloud.master.proxy.Proxy;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public class PacketInRegisterProxy implements Packet {

    private String name;
    private State state;

    public PacketInRegisterProxy() {
    }

    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.name = byteBuf.readUTF();
        this.state = State.class.getEnumConstants()[byteBuf.readInt()];
    }

    @Override
    public Packet handle(Channel channel) throws IOException {
        switch (state) {
            case ONLINE:
                Proxy proxy = CloudBootsrap.getInstance().getProxy();
                proxy.onConnect(channel);
                break;

            case OFFLINE:
                System.out.println("proxy " + name + this.state.toString());
                CloudBootsrap.getInstance().startProxy();
                break;
        }
        return null;
    }

    public enum State {
        ONLINE, OFFLINE;
    }

}
