package io.dexiron.cloud.wrapper;

import io.dexiron.cloud.lib.config.CloudWrapperConfig;
import io.dexiron.cloud.lib.config.adress.NetworkInterfaceAdapter;
import io.dexiron.cloud.lib.config.ascii.AsciiPrinter;
import io.dexiron.cloud.lib.config.command.CommandExecutor;
import io.dexiron.cloud.lib.config.networking.client.NetworkingClient;
import io.dexiron.cloud.lib.config.networking.client.NetworkingClientImpl;
import io.dexiron.cloud.lib.config.networking.decoder.PacketDecoder;
import io.dexiron.cloud.lib.config.networking.encoder.PacketEncoder;
import io.dexiron.cloud.lib.config.networking.registry.PacketRegistry;
import io.dexiron.cloud.lib.config.zip.ZipFileHandler;
import io.dexiron.cloud.wrapper.commands.CommandStop;
import io.dexiron.cloud.wrapper.config.CloudWrapperConfigAdapter;
import io.dexiron.cloud.wrapper.manager.ServerManager;
import io.dexiron.cloud.wrapper.network.NetworkHandler;
import io.dexiron.cloud.wrapper.network.packets.in.PacketInStartGameServer;
import io.dexiron.cloud.wrapper.network.packets.out.PacketOutAuthClient;
import io.dexiron.cloud.wrapper.network.packets.out.PacketOutWrapperReady;
import io.dexiron.cloud.wrapper.network.packets.out.PacketOutWrapperRegister;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.lingala.zip4j.ZipFile;

import java.io.*;
import java.net.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudBootsrap {

    @Getter
    private CloudWrapperConfigAdapter cloudWrapperConfigAdapter;

    @Getter
    private CloudWrapperConfig cloudWrapperConfig;

    @Getter
    public static CloudBootsrap instance;

    @Getter
    @Setter
    private Channel channel;

    @Getter
    private NetworkingClient networkingClient = new NetworkingClientImpl();

    @Getter
    private ExecutorService asyncCommandExecutor = Executors.newSingleThreadExecutor();

    @Getter
    private final CommandExecutor commandExecutor = new CommandExecutor();

    @Getter
    private ServerManager serverManager;

    public CloudBootsrap() throws UnknownHostException {
    }

    public void startWrapper() throws IOException {
        instance = this;

        new AsciiPrinter().print();

        registerCommandThread();
        registerCommands();


        loadConfig();

        setChannel(networkingClient.bindOnServer(cloudWrapperConfig, channel -> {
            channel.pipeline().addLast(new PacketEncoder()).addLast(new PacketDecoder()).addLast(new NetworkHandler());
        }));

        this.serverManager = new ServerManager();

        getChannel().writeAndFlush(new PacketOutAuthClient(getCloudWrapperConfig().getName(), NetworkInterfaceAdapter.getIPv4InetAddress().toString().split("/")[1], UUID.randomUUID().toString(), System.currentTimeMillis())).channel().voidPromise();

        getChannel().writeAndFlush(new PacketOutWrapperRegister(cloudWrapperConfig.getName(), PacketOutWrapperRegister.State.ONLINE, cloudWrapperConfig.getRam())).channel().voidPromise();

        registerOutGoingPackets();
        registerIncomingPackets();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        downloadTemplates();
                    }
                },
                5000
        );

    }

    private void registerIncomingPackets() {
        PacketRegistry.PacketDirection.IN.addPacket(7000, PacketInStartGameServer.class);
    }

    private void registerOutGoingPackets() {
        PacketRegistry.PacketDirection.OUT.addPacket(1000, PacketOutAuthClient.class);
        PacketRegistry.PacketDirection.OUT.addPacket(2000, PacketOutWrapperRegister.class);
        PacketRegistry.PacketDirection.OUT.addPacket(5000, PacketOutWrapperReady.class);
    }

    public void stopWrapper() {
        getChannel().writeAndFlush(new PacketOutWrapperRegister(cloudWrapperConfig.getName(), PacketOutWrapperRegister.State.OFFLINE, cloudWrapperConfig.getRam())).channel().voidPromise();
        asyncCommandExecutor.shutdownNow();
    }

    private void registerCommandThread() {
        asyncCommandExecutor.execute(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    //System.out.println(Color.YELLOW + "Master" + Color.WHITE + "» ");
                    boolean result = commandExecutor.executeLine(reader.readLine());
                    if (!result) {
                        System.out.println("Unkown Command!");
                    }
                } catch (IOException ex) {

                }
            }
        });
    }

    private void registerCommands() {
        commandExecutor.registerCommand(new CommandStop());
    }


    public void loadConfig() {

        this.cloudWrapperConfigAdapter = new CloudWrapperConfigAdapter();

        this.cloudWrapperConfig = cloudWrapperConfigAdapter.getCloudWrapperConfig();
    }

    public void downloadTemplates() {
        try {
           System.out.println("Downloading...");
            try (BufferedInputStream inputStream = new BufferedInputStream(new URL("http://" + cloudWrapperConfig.getHost() + ":8081/template").openStream());
                 FileOutputStream fileOS = new FileOutputStream(new File(this.serverManager.getDeployDirectory(), "templates.zip"))) {
                byte data[] = new byte[2048];
                int byteContent;
                while ((byteContent = inputStream.read(data, 0, 2048)) != -1) {
                    fileOS.write(data, 0, byteContent);
                }
            } catch (IOException e) {
            }

            new ZipFile(new File(this.serverManager.getDeployDirectory() ,"templates.zip")).extractAll("server/");

           System.out.println("Downloaded!");

           getChannel().writeAndFlush(new PacketOutWrapperReady(cloudWrapperConfig.getName(), true)).channel().voidPromise();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
