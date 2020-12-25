package io.dexiron.cloud.master.proxy;

import io.dexiron.cloud.master.CloudBootsrap;
import io.dexiron.cloud.master.game.GameServer;
import io.dexiron.cloud.master.group.ServerGroup;
import io.dexiron.cloud.master.network.packets.out.PacketOutProxyInformation;
import io.dexiron.cloud.master.network.packets.out.PacketOutTemplateData;
import io.dexiron.cloud.master.network.packets.out.PacketsOutRegisterGameServer;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Proxy {

    @Getter
    @Setter
    private int ram, maxPlayers;

    @Setter
    @Getter
    private String motd;

    @Setter
    @Getter
    private Channel channel;

    @Setter
    @Getter
    private boolean started, maintenance = false;

    private File proxyConfigFile;
    private File proxyDirectory;

    public Proxy() {
        this.proxyConfigFile = new File("configs/proxy.json");
        if (!this.proxyConfigFile.exists()) {
            try {
                this.proxyConfigFile.getParentFile().mkdir();
                this.proxyConfigFile.createNewFile();
            } catch (IOException ex) {

            }
        }
        this.maintenance = CloudBootsrap.getInstance().getProxyConfig().getBoolean("maintenance");
        this.proxyDirectory = new File("proxy/");
        this.proxyDirectory.mkdirs();
    }

    public void start() {
        System.out.println("Proxy [ram=" + ram + "] was started");

        File file = new File(proxyDirectory, "BungeeCord.jar");
        if (!file.exists()) {
            System.out.println("Proxy [ram=" + ram + "] BungeeCord.jar error, Cloud is stopping");
            System.exit(0);
            return;
        }

        ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "screen -mdS Proxy java -server -Xmx" + getRam() + "M -jar BungeeCord.jar").directory(proxyDirectory);
        try {
            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onConnect(Channel channel) {
        setChannel(channel);
        System.out.println("Proxy started succesfully");
        setStarted(true);

        this.sendProxyInformation();

        if (CloudBootsrap.getInstance().getCloudManager().getServerGroups().size() != 0) {
            for (ServerGroup value : CloudBootsrap.getInstance().getCloudManager().getServerGroups().values()) {
                if (CloudBootsrap.getInstance().getProxy().getChannel() != null) {
                    CloudBootsrap.getInstance().getProxy().getChannel().writeAndFlush(new PacketOutTemplateData(value.getName(), value.getRamPerServer(), value.isStatic(), value.getMaxServer(), value.getMinServer())).channel().voidPromise();
                }
            }
        }
    }

    public void stop() {
        if (channel != null) {
            channel.close();
        }

        CloudBootsrap.getInstance().setProxy(null);
    }

    public void registerServer(GameServer server) {
        if (isStarted()) {
            System.out.println("innet adress " + ((InetSocketAddress) server.getConnection().remoteAddress()).getAddress().getHostAddress());
            getChannel().writeAndFlush(new PacketsOutRegisterGameServer(server.getName(),  ((InetSocketAddress) server.getConnection().remoteAddress()).getAddress().getHostAddress(), server.getPort(), PacketsOutRegisterGameServer.Direction.ADD));

        }
    }

    public void unregisterServer(GameServer server) {
        if (isStarted()) {
            getChannel().writeAndFlush(new PacketsOutRegisterGameServer(server.getName(),  ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress(), server.getPort(), PacketsOutRegisterGameServer.Direction.REMOVE));
        }
    }


    public void sendProxyInformation() {
        if (getChannel() != null) {
            getChannel().writeAndFlush(new PacketOutProxyInformation(getMotd(), getMaxPlayers(), isMaintenance())).channel().voidPromise();
        }
    }

}
