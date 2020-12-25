package io.dexiron.cloud.master.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.master.CloudBootsrap;
import io.dexiron.cloud.master.events.ServerInfoUpdateEvent;
import io.dexiron.cloud.master.game.GameServer;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public class PacketInServerInfo implements Packet {

    private String name;
    private String motd;
    private String extra;
    private String state;
    private Integer players;

    public PacketInServerInfo() {
    }


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
        System.out.println(this.name + this.extra + this.motd + this.state + this.players);

        GameServer server = CloudBootsrap.getInstance().getCloudManager().getServerByName(name);
        server.setExtra(this.extra);
        server.setMotd(this.motd);
        server.setState(this.state);
        server.setPlayers(this.players);

        CloudBootsrap.getInstance().getCloudManager().sendDataToServer();
        return null;
    }
}
