package io.dexiron.cloud.server.api.servergroup.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.server.api.proxy.CloudProxyAPI;
import io.dexiron.cloud.server.api.servergroup.CloudAPI;
import io.dexiron.cloud.server.api.utils.ServerGroup;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;

import java.io.IOException;

public class PacketInTemplateData implements Packet {

    private String group;
    private int ram;
    private boolean isStatic;
    private int maxServer;
    private int minServer;


    public PacketInTemplateData() {
    }


    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.group = byteBuf.readUTF();
        this.ram = byteBuf.readInt();
        this.maxServer = byteBuf.readInt();
        this.minServer = byteBuf.readInt();
        this.isStatic = byteBuf.readBoolean();
    }

    @Override
    public Packet handle(Channel channel) throws IOException {


        ServerGroup group = new ServerGroup();

        group.setName(this.group);
        group.setRamPerServer(this.ram);
        group.setMaxServer(this.maxServer);
        group.setMinServer(this.minServer);
        group.setStatic(this.isStatic);

        CloudAPI.getInstance().getServerGroups().put(group.getName(), group);

        System.out.println("updated template data " + this.group + this.ram + this.maxServer + this.minServer + this.isStatic);
        System.out.println("template sizeeee " + CloudAPI.getInstance().getServerGroups().size());



        return null;
    }
}
