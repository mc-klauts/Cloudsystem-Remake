package io.dexiron.cloud.server.api.proxy.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.server.api.proxy.CloudProxyAPI;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PacketInRegisterGameserver implements Packet {

    private String name;
    private String adress;
    private int port;
    private Direction direction;

    public PacketInRegisterGameserver(String name, String adress, int port, Direction direction) {
        this.name = name;
        this.adress = adress;
        this.port = port;
        this.direction = direction;
    }

    public PacketInRegisterGameserver() {
    }

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeUTF(this.adress);
        byteBuf.writeInt(this.port);
        byteBuf.writeInt(this.direction.ordinal());
    }

    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.name = byteBuf.readUTF();
        this.adress = byteBuf.readUTF();
        this.port = byteBuf.readInt();
        this.direction = Direction.class.getEnumConstants()[byteBuf.readInt()];
    }

    @Override
    public Packet handle(Channel channel) throws IOException {

        switch (direction) {
            case ADD:
                CloudProxyAPI.getInstance().getProxy().getServers().put(this.name, CloudProxyAPI.getInstance().getProxy().constructServerInfo(name, new InetSocketAddress(this.adress, this.port), "", false));

                System.out.println(this.adress + this.name);

                ProxyServer.getInstance().getPlayers().forEach(players -> {
                    if (players.hasPermission("centola.extra.servermessage")) {
                        players.sendMessage("§7Der Server §8(§e" + name + "§8) §7wurde §agestartet");
                    }
                });
                break;

            case REMOVE:
                CloudProxyAPI.getInstance().getProxy().getServers().remove(name);
                ProxyServer.getInstance().getPlayers().forEach(players -> {
                    if (players.hasPermission("centola.extra.servermessage")) {
                        players.sendMessage("§7Der Server §8(§e" + name + "§8) §7wurde §cgestoppt");
                    }
                });
                break;
        }

        return null;
    }

    public enum Direction {
        ADD, REMOVE;
    }

}
