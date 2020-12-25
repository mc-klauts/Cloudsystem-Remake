package io.dexiron.cloud.server.api.servergroup.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.server.api.servergroup.CloudAPI;
import io.dexiron.cloud.server.api.servergroup.events.ServerInfoUpdateEvent;
import io.dexiron.cloud.server.api.utils.ServerInfo;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;

import java.io.IOException;

public class PacketInUpdateAPI implements Packet {

    private String name;
    private String motd;
    private String extra;
    private String state;
    private Integer players;

    public PacketInUpdateAPI() {}



    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.name = byteBuf.readUTF();
        this.extra = byteBuf.readUTF();
        this.motd = byteBuf.readUTF();
        this.state = byteBuf.readUTF();
        this.players = byteBuf.readInt();
    }

    @Override
    public Packet handle(Channel channel) throws IOException {

        ServerInfo serverInfo = new ServerInfo(this.name, this.motd, this.extra, this.state, this.players);
        serverInfo.setMaxPlayers(20);
        serverInfo.setGroup(this.name.split("-")[0]);
        CloudAPI.getInstance().getServerInfos().put(this.name, serverInfo);

        Bukkit.getPluginManager().callEvent(new ServerInfoUpdateEvent(serverInfo));

        return null;
    }

}
