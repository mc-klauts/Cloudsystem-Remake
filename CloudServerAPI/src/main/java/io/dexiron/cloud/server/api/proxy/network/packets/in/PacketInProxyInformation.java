package io.dexiron.cloud.server.api.proxy.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.server.api.proxy.CloudProxyAPI;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public class PacketInProxyInformation implements Packet {

    private String motd;
    private Integer maxPlayers;
    private boolean maintenance;

    public PacketInProxyInformation() {

    }

    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.motd = byteBuf.readUTF();
        this.maxPlayers = byteBuf.readInt();
        this.maintenance = byteBuf.readBoolean();
    }

    @Override
    public Packet handle(Channel channel) throws IOException {
        CloudProxyAPI.getInstance().setMotd(this.motd);
        CloudProxyAPI.getInstance().setMaxPlayers(this.maxPlayers);
        CloudProxyAPI.getInstance().setMaintenance(this.maintenance);
        System.out.println("aaaaaaaaaaaaa");
        return null;
    }
}
