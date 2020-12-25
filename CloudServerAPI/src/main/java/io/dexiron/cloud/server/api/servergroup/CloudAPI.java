package io.dexiron.cloud.server.api.servergroup;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.dexiron.cloud.lib.config.adress.NetworkInterfaceAdapter;
import io.dexiron.cloud.lib.config.networking.client.NetworkingClient;
import io.dexiron.cloud.lib.config.networking.client.NetworkingClientImpl;
import io.dexiron.cloud.lib.config.networking.decoder.PacketDecoder;
import io.dexiron.cloud.lib.config.networking.encoder.PacketEncoder;
import io.dexiron.cloud.lib.config.networking.registry.PacketRegistry;
import io.dexiron.cloud.server.api.servergroup.listener.ListenerPlayer;
import io.dexiron.cloud.server.api.servergroup.network.ServerGroupNetworkHandler;
import io.dexiron.cloud.server.api.servergroup.network.packets.in.PacketInTemplateData;
import io.dexiron.cloud.server.api.servergroup.network.packets.in.PacketInUpdateAPI;
import io.dexiron.cloud.server.api.servergroup.network.packets.out.PacketOutAuthClient;
import io.dexiron.cloud.server.api.servergroup.network.packets.out.PacketOutConnectGameserver;
import io.dexiron.cloud.server.api.servergroup.network.packets.out.PacketOutServerInfo;
import io.dexiron.cloud.server.api.utils.ServerGroup;
import io.dexiron.cloud.server.api.utils.ServerInfo;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CloudAPI extends JavaPlugin {

    @Getter
    private NetworkingClient networkingClient = new NetworkingClientImpl();

    @Getter
    @Setter
    private Channel channel;

    @Getter
    private File file = new File("plugins/Connector/cloud.yml");

    @Getter
    public static CloudAPI instance;

    private YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    @Getter
    @Setter
    private String extra = "extraaaaa", motd = "CLOUDSERVER", state = "ONLINE", servername;

    @Getter
    private HashMap<String, ServerInfo> serverInfos = new HashMap<>();

    @Getter
    private HashMap<String, ServerGroup> serverGroups = new HashMap<>();

    @Override
    public void onEnable() {

        instance = this;

        initFile();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        setServername(System.getProperty("cloud-servername"));

        setChannel(networkingClient.bindOnServer(cfg.getString("cloud.host"), cfg.getInt("cloud.port"), channel -> {
            channel.pipeline().addLast(new PacketEncoder()).addLast(new PacketDecoder()).addLast(new ServerGroupNetworkHandler());
        }));

        try {
            getChannel().writeAndFlush(new PacketOutAuthClient(getServername(), NetworkInterfaceAdapter.getIPv4InetAddress().toString().split("/")[1], UUID.randomUUID().toString(), System.currentTimeMillis())).channel().voidPromise();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        getChannel().writeAndFlush(new PacketOutConnectGameserver(getServername(), Bukkit.getPort())).channel().voidPromise();
        updateServerInformation();

        registerOutGoingPackets();
        registerIncomingPackets();

        new ListenerPlayer(this);



    }

    private void registerIncomingPackets() {
        PacketRegistry.PacketDirection.IN.addPacket(9800, PacketInUpdateAPI.class);
        PacketRegistry.PacketDirection.IN.addPacket(9000, PacketInTemplateData.class);
    }

    public void updateServerInformation() {
        getChannel().writeAndFlush(new PacketOutServerInfo(getServername(), getExtra(), getMotd(), getState(), Bukkit.getOnlinePlayers().size())).channel().voidPromise();
    }

    private void registerOutGoingPackets() {
        PacketRegistry.PacketDirection.OUT.addPacket(1000, PacketOutAuthClient.class);
        PacketRegistry.PacketDirection.OUT.addPacket(8000, PacketOutConnectGameserver.class);
        PacketRegistry.PacketDirection.OUT.addPacket(9700, PacketOutServerInfo.class);
    }

    private void initFile() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
                cfg.set("cloud.host", "127.0.0.1");
                cfg.set("cloud.port", "30000");
                cfg.save(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public List<ServerInfo> getServerFromGroup(String group) {
        if(getServerInfos().isEmpty()) return new ArrayList<>();
        List<ServerInfo> servers = new ArrayList<>();

        System.out.println();

        getServerInfos().values().stream().forEach(server -> {
            if (server.getGroup().equalsIgnoreCase(group)) {
                servers.add(server);
            }
        });
        return servers;
    }
    public ServerInfo getServerInfo(String name) {
        return getServerInfos().get(name);
    }
    public void sendToServer(Player player, String target) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(target);
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

}
