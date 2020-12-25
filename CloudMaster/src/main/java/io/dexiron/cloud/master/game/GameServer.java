package io.dexiron.cloud.master.game;

import io.dexiron.cloud.master.CloudBootsrap;
import io.dexiron.cloud.master.group.ServerGroup;
import io.dexiron.cloud.master.network.packets.out.PacketOutStartGameServer;
import io.dexiron.cloud.master.network.packets.out.PacketOutUpdateAPI;
import io.dexiron.cloud.master.wrapper.Wrapper;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

public class GameServer {

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private  String extra = "extraaa", motd = "normal motd";

    @Setter
    @Getter
    private ServerGroup group;

    @Setter
    @Getter
    private Channel connection;

    @Setter
    @Getter
    private Wrapper wrapper;

    @Setter
    @Getter
    private String state = "STARTING";

    @Setter
    @Getter
    private int maxPlayers = 0, players = 0, port;
    @Setter
    @Getter

    private boolean starting, lobby;

    public GameServer(String name, Wrapper wrapper, ServerGroup group) {
        this.name = name;
        this.wrapper = wrapper;
        this.group = group;
        this.lobby = (name.contains("Lobby") ? true : false);
        this.maxPlayers = group.getMaxPlayers();
    }

    public void start() {
        setStarting(true);
        wrapper.getChannel().writeAndFlush(new PacketOutStartGameServer(this.name, this.group.getName(), this.group.getRamPerServer(), this.group.isStatic(), this.maxPlayers)).channel().voidPromise();
        wrapper.setAvailableRam(wrapper.getAvailableRam() - group.getRamPerServer());

        System.out.println(isStarting());

        System.out.println("Server [name=" + name + "/ram=" + group.getRamPerServer() + "] was started!");

        CloudBootsrap.getInstance().getCloudManager().addServer(this);

    }

    public void stop() {
        System.out.println("Server [name=" + name + "] is stopped");
        if (getConnection() != null) {
            getConnection().disconnect();
        }
        unregister();
        this.getWrapper().setAvailableRam(this.getWrapper().getAvailableRam() + this.getGroup().getRamPerServer());
    }

    public void onDisconnect() {
        setConnection(null);
        System.out.println("Server [name=" + name + "] successfully stopped!");
        unregister();
    }

    public void onConnect(Channel channel) {
        setConnection(channel);
        setState("ONLINE");
        CloudBootsrap.getInstance().getProxy().registerServer(this);
        setStarting(false);

        System.out.println("Server [name=" + name + "/group=" + group.getName() + "] is online");

        CloudBootsrap.getInstance().getGameserverChannels().add(channel);

    }

    public void sendUpdate(Channel channel) {
        if (getConnection() != null) {

        }
    }

    private void unregister() {
        if (CloudBootsrap.getInstance().getCloudManager().getServers().containsKey(this.getName())) {
            CloudBootsrap.getInstance().getCloudManager().removeServer(this);
            setState("OFFLINE");
            CloudBootsrap.getInstance().getProxy().unregisterServer(this);
        }
    }


}
