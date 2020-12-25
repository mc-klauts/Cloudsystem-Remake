package io.dexiron.cloud.server.api.proxy;

import io.dexiron.cloud.lib.config.adress.NetworkInterfaceAdapter;
import io.dexiron.cloud.lib.config.networking.client.NetworkingClient;
import io.dexiron.cloud.lib.config.networking.client.NetworkingClientImpl;
import io.dexiron.cloud.lib.config.networking.decoder.PacketDecoder;
import io.dexiron.cloud.lib.config.networking.encoder.PacketEncoder;
import io.dexiron.cloud.lib.config.networking.registry.PacketRegistry;
import io.dexiron.cloud.server.api.proxy.commands.CommandHub;
import io.dexiron.cloud.server.api.proxy.commands.CommandServers;
import io.dexiron.cloud.server.api.proxy.listener.ProxyListener;
import io.dexiron.cloud.server.api.proxy.listener.ServerConnectListener;
import io.dexiron.cloud.server.api.proxy.network.ProxyNetworkHandler;
import io.dexiron.cloud.server.api.proxy.network.packets.in.PacketInProxyInformation;
import io.dexiron.cloud.server.api.proxy.network.packets.in.PacketInRegisterGameserver;
import io.dexiron.cloud.server.api.proxy.network.packets.in.PacketInTemplateData;
import io.dexiron.cloud.server.api.proxy.network.packets.out.PacketOutAuthClient;
import io.dexiron.cloud.server.api.proxy.network.packets.out.PacketOutRegisterProxy;
import io.dexiron.cloud.server.api.proxy.network.packets.out.PacketOutGameServerAction;
import io.dexiron.cloud.server.api.utils.ServerGroup;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.UUID;

public class CloudProxyAPI extends Plugin {

    @Getter
    @Setter
    private Channel channel;

    @Getter
    private NetworkingClient networkingClient = new NetworkingClientImpl();

    @Getter
    private String proxyName = "Proxy-1";

    @Getter
    public static CloudProxyAPI instance;

    @Getter
    @Setter
    private int maxPlayers;

    @Getter
    @Setter
    private boolean maintenance = false;

    @Getter
    private HashMap<String, ServerGroup> serverGroups = new HashMap<>();

    @Getter
    @Setter
    private String motd;

    @Override
    public void onEnable() {

        instance = this;

        setChannel(networkingClient.bindOnServer("127.0.0.1", 30000, channel -> {
            channel.pipeline().addLast(new PacketEncoder()).addLast(new PacketDecoder()).addLast(new ProxyNetworkHandler());
        }));

        try {
            getChannel().writeAndFlush(new PacketOutAuthClient(proxyName, NetworkInterfaceAdapter.getIPv4InetAddress().toString().split("/")[1], UUID.randomUUID().toString(), System.currentTimeMillis())).channel().voidPromise();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        getChannel().writeAndFlush(new PacketOutRegisterProxy(this.proxyName, PacketOutRegisterProxy.State.ONLINE)).channel().voidPromise();

        registerOutGoingPackets();
        registerIncomingPackets();

        new ProxyListener(this);
        new ServerConnectListener(this);

        new CommandServers(this);
        new CommandHub(this);
    }

    @Override
    public void onDisable() {
        getChannel().writeAndFlush(new PacketOutRegisterProxy(this.proxyName, PacketOutRegisterProxy.State.OFFLINE)).channel().voidPromise();

    }

    public ServerGroup getServerGroup(String name) {
        return getServerGroups().get(name);
    }

    public void startGameServer(String serverGroup, int amount) {
        getChannel().writeAndFlush(new PacketOutGameServerAction(serverGroup, amount, PacketOutGameServerAction.Direction.START)).channel().voidPromise();
    }

    public void stopGameServer(String serverName) {
        getChannel().writeAndFlush(new PacketOutGameServerAction(serverName, 1, PacketOutGameServerAction.Direction.STOP)).channel().voidPromise();
    }

    public void stopServerGroup(String serverGroup) {
        getChannel().writeAndFlush(new PacketOutGameServerAction(serverGroup, 1, PacketOutGameServerAction.Direction.STOPGROUP)).channel().voidPromise();
    }

    private void registerIncomingPackets() {
        PacketRegistry.PacketDirection.OUT.addPacket(1000, PacketOutAuthClient.class);
        PacketRegistry.PacketDirection.OUT.addPacket(3000, PacketOutRegisterProxy.class);
        PacketRegistry.PacketDirection.OUT.addPacket(6000, PacketInRegisterGameserver.class);
        PacketRegistry.PacketDirection.OUT.addPacket(9500, PacketOutGameServerAction.class);
    }

    private void registerOutGoingPackets() {
        PacketRegistry.PacketDirection.IN.addPacket(4000, PacketInProxyInformation.class);
        PacketRegistry.PacketDirection.IN.addPacket(6000, PacketInRegisterGameserver.class);
        PacketRegistry.PacketDirection.IN.addPacket(9000, PacketInTemplateData.class);
    }
}
