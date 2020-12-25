package io.dexiron.cloud.master;

import com.google.common.collect.Lists;
import com.google.common.eventbus.AsyncEventBus;
import io.dexiron.cloud.lib.config.ascii.AsciiPrinter;
import io.dexiron.cloud.lib.config.auth.NettySession;
import io.dexiron.cloud.lib.config.config.Config;
import io.dexiron.cloud.lib.config.database.DatabaseConectionFactory;
import io.dexiron.cloud.lib.config.database.MongoDatabaseConection;
import io.dexiron.cloud.lib.config.networking.decoder.PacketDecoder;
import io.dexiron.cloud.lib.config.networking.encoder.PacketEncoder;
import io.dexiron.cloud.lib.config.networking.registry.PacketRegistry;
import io.dexiron.cloud.lib.config.networking.server.NetworkingServer;
import io.dexiron.cloud.lib.config.networking.server.NetworkingServerImpl;
import io.dexiron.cloud.lib.config.zip.ZipFileHandler;
import io.dexiron.cloud.master.auth.ClientAuthAdapter;
import io.dexiron.cloud.master.group.ServerGroup;
import io.dexiron.cloud.master.listener.ListenerServerInfo;
import io.dexiron.cloud.master.manager.CloudManager;
import io.dexiron.cloud.master.network.handler.NetworkHandler;
import io.dexiron.cloud.master.network.packets.in.*;
import io.dexiron.cloud.master.network.packets.out.*;
import io.dexiron.cloud.master.proxy.Proxy;
import io.dexiron.cloud.master.web.WebServer;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudBootsrap {

    @Getter
    private NetworkingServer networkingServer = new NetworkingServerImpl();

    @Getter
    private DatabaseConectionFactory databaseConectionFactory = new MongoDatabaseConection();

    @Getter
    public static CloudBootsrap instance;

    @Getter
    private ClientAuthAdapter clientAuthAdapter = new ClientAuthAdapter();

    @Getter
    public CloudManager cloudManager;

    @Getter
    private Config proxyConfig;

    @Getter
    @Setter
    private Proxy proxy;

    @Getter
    private ZipFileHandler zipFileHandler;


    @Getter
    private ExecutorService asyncEventBusExecutor = Executors.newSingleThreadExecutor();

    @Getter
    private List<Channel> gameserverChannels = Lists.newArrayList();

    @Getter
    private AsyncEventBus asyncEventBus = new AsyncEventBus(asyncEventBusExecutor);

    public void startMaster() {

        instance = this;

        new AsciiPrinter().print();

        this.databaseConectionFactory.connect("localhost", 27017, "cloud");

        this.clientAuthAdapter.loadFromDatabase();

        this.loadConfig();

        this.zipFileHandler = new ZipFileHandler();
        this.cloudManager = new CloudManager();
        this.cloudManager.loadGroups();

        networkingServer.start(30000, channel -> {
            channel.pipeline().addLast(new PacketEncoder()).addLast(new PacketDecoder()).addLast(new NetworkHandler());
        });

        new WebServer();

        registerOutGoingPackets();
        registerIncomingPackets();

        this.registerTask();

        this.proxy = new Proxy();
        this.startProxy();

        registerListener();

    }

    private void registerListener() {
        asyncEventBus.register(new ListenerServerInfo());
    }

    private void registerIncomingPackets() {
        PacketRegistry.PacketDirection.IN.addPacket(1000, PacketInAuthClient.class);
        PacketRegistry.PacketDirection.IN.addPacket(2000, PacketInWrapperRegister.class);
        PacketRegistry.PacketDirection.IN.addPacket(3000, PacketInRegisterProxy.class);
        PacketRegistry.PacketDirection.IN.addPacket(5000, PacketInWrapperReady.class);
        PacketRegistry.PacketDirection.IN.addPacket(6000, PacketsOutRegisterGameServer.class);
        PacketRegistry.PacketDirection.IN.addPacket(8000, PacketInConnectGameserver.class);
        PacketRegistry.PacketDirection.IN.addPacket(9500, PacketInGameServerAction.class);
        PacketRegistry.PacketDirection.IN.addPacket(9700, PacketInServerInfo.class);
    }

    private void registerOutGoingPackets() {
        PacketRegistry.PacketDirection.OUT.addPacket(4000, PacketOutProxyInformation.class);
        PacketRegistry.PacketDirection.OUT.addPacket(6000, PacketsOutRegisterGameServer.class);
        PacketRegistry.PacketDirection.OUT.addPacket(7000, PacketOutStartGameServer.class);
        PacketRegistry.PacketDirection.OUT.addPacket(9000, PacketOutTemplateData.class);
        PacketRegistry.PacketDirection.OUT.addPacket(9800, PacketOutUpdateAPI.class);
    }

    private void registerTask() {

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                everySecond();

            }
        }, 20, 20);
    }


    private void everySecond() {
        cloudManager.process();
    }

    public void loadConfig() {
        this.proxyConfig = new Config("configs/proxy.json");
        System.out.println("Configs loaded successfully");
    }

    public void stopMaster() {
        asyncEventBusExecutor.shutdownNow();
    }

    public void startProxy() {
        int maxPlayers = (int) ((Long) proxyConfig.get("maxPlayers")).longValue();
        int ram = (int) ((Long) proxyConfig.get("ram")).longValue();
        String motd = proxyConfig.getString("motd");
        this.proxy.setMaxPlayers(maxPlayers);
        this.proxy.setMotd(motd);
        this.proxy.setRam(ram);
        proxy.start();
    }

}
