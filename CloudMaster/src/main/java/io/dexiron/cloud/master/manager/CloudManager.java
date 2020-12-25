package io.dexiron.cloud.master.manager;

import com.google.common.collect.Maps;
import com.mongodb.Block;
import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import io.dexiron.cloud.master.CloudBootsrap;
import io.dexiron.cloud.master.game.GameServer;
import io.dexiron.cloud.master.group.ServerGroup;
import io.dexiron.cloud.master.network.packets.out.PacketOutTemplateData;
import io.dexiron.cloud.master.network.packets.out.PacketOutUpdateAPI;
import io.dexiron.cloud.master.wrapper.Wrapper;
import lombok.Getter;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.bson.Document;

import java.io.*;
import java.util.*;

public class CloudManager {

    @Getter
    private File globalDirectory, templatesDirectory, deployDirectory;

    @Getter
    private HashMap<String, Wrapper> wrappperMap = Maps.newHashMap();

    @Getter
    private HashMap<String, ServerGroup> serverGroups = new HashMap<>();

    public void removeWrapper(Wrapper wrapper) {
        wrappperMap.remove(wrapper.getName());
    }

    @Getter
    private HashMap<String, GameServer> servers = new HashMap<>();

    public CloudManager() {
        this.globalDirectory = new File("templates/global/");
        this.templatesDirectory = new File("templates/");
        this.deployDirectory = new File("deploy/");
        if (!this.deployDirectory.exists()) {
            this.deployDirectory.mkdirs();
        }
        if (!this.globalDirectory.exists()) {
            this.globalDirectory.mkdirs();
        }

        if (!this.templatesDirectory.exists()) {
            this.templatesDirectory.mkdirs();
            return;
        }

        this.buildZips();
    }

    public void buildZips() {

        System.out.println("Deploys loaed");
        File templateZip = new File(deployDirectory, "templates.zip");
        //new ZipFile(templateZip).addFolder(templatesDirectory);

        try {
            new ZipFile(deployDirectory + "/" + "templates.zip").addFolder(templatesDirectory);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        System.out.println("Deploys builded");
    }

    public void loadGroups() {
        HashMap<String, ServerGroup> serverGroup = new HashMap<String, ServerGroup>();

        MongoCollection<Document> sessionsCollection = CloudBootsrap.getInstance().getDatabaseConectionFactory().getCollection("template");

        FindIterable<Document> cursor = sessionsCollection.find().cursorType(CursorType.NonTailable);

        cursor.forEach(new Block<Document>() {
            @Override
            public void apply(Document document) {
                ServerGroup group = new ServerGroup();
                group.setName(document.getString("name"));
                group.setMaxServer(document.getInteger("maxServer"));
                group.setMinServer(document.getInteger("minServer"));
                group.setRamPerServer(document.getInteger("ramPerServer"));
                group.setStatic(document.getBoolean("static"));

                group.setWrapper(document.getString("wrapper"));

                group.setMaxPlayers(document.getInteger("maxPlayers"));
                serverGroup.put(group.getName(), group);
            }
        });

        System.out.println("Cloud load " + serverGroup.size() + " groups!");
        this.serverGroups = serverGroup;

    }


    public void addServer(GameServer server) {
        this.servers.put(server.getName(), server);
    }

    public void removeServer(GameServer server) {
        this.servers.remove(server.getName());
    }


    public Wrapper getFreeWrapper(ServerGroup group) {

        if (group.isStatic() && group.getWrapper() == null) {
            return null;
        }
        return getWrappperMap().values().stream().filter(slave -> group.getWrapper() == null || group.getWrapper().equalsIgnoreCase(slave.getName())).filter(slave -> slave.getAvailableRam() >= group.getRamPerServer()).filter(slave -> slave.isReady()).min(Comparator.comparingInt(Wrapper::getAvailableRam)).orElse(null);
    }

    private int serversNeeded(ServerGroup group) {
        List<GameServer> serv = getServersByGroup(group);
        int running = (int) serv.stream().filter(server -> isActive(server.getState()) || server.isStarting()).count();
        int needed = group.getMinServer() - running;
        return group.getMaxServer() > 0 ? Math.min(needed, group.getMaxServer() - serv.size()) : needed;
    }


    private boolean isActive(String state) {
        return !(state.equalsIgnoreCase("OFFLINE") || state.equalsIgnoreCase("INGAME") || state.equalsIgnoreCase("STARTING"));
    }

    public GameServer startServer(ServerGroup group, Wrapper wrapper) {
        String name = getName(group);
        GameServer server = new GameServer(name, wrapper, group);
        server.start();
        return server;
    }

    public void process() {

        for (ServerGroup group : getServerGroups().values()) {
            int needed = serversNeeded(group);
            if (needed > 0) {
                Wrapper slave = getFreeWrapper(group);
                if (slave != null) {
                    startServer(group, slave);
                }
            }
        }

        //this.sendDataToServer();
    }


    public void sendDataToServer() {
        if (CloudBootsrap.getInstance().getCloudManager().getServerGroups().size() != 0) {
            for (ServerGroup value : CloudBootsrap.getInstance().getCloudManager().getServerGroups().values()) {
                CloudBootsrap.getInstance().getGameserverChannels().forEach(channel -> {
                    channel.writeAndFlush(new PacketOutTemplateData(value.getName(), value.getRamPerServer(), value.isStatic(), value.getMaxServer(), value.getMinServer())).channel().voidPromise();
                    System.out.println("send template data " + value.getName() + value.isStatic() + value.getMaxServer() + value.getMinServer());
                });
            }
        }

        if (servers.size() != 0) {
            for (GameServer gameServer : servers.values()) {
                CloudBootsrap.getInstance().getGameserverChannels().forEach(channel -> {
                    channel.writeAndFlush(new PacketOutUpdateAPI(gameServer.getName(), gameServer.getExtra(), gameServer.getMotd(), gameServer.getState(), gameServer.getPlayers())).channel().voidPromise();
                });
            }
        }
    }

    public ArrayList<GameServer> getServersByGroup(ServerGroup group) {
        ArrayList<GameServer> serverList = new ArrayList<>();
        getServers().values().stream().forEach(server -> {
            if (server.getGroup().getName().equals(group.getName())) {
                serverList.add(server);
            }
        });
        return serverList;
    }


    private boolean nameExists(String name) {
        return getServerByName(name) != null;
    }

    public GameServer getServerByName(String name) {
        for (GameServer server : getServers().values()) {
            if (server != null && server.getName().equalsIgnoreCase(name)) {
                return server;
            }
        }
        return null;
    }


    private String getName(ServerGroup group) {
        for (int i = 1; i <= 2500; i++) {
            String name = generateName(group, i);
            if (nameExists(name)) {
                continue;
            }
            return name;
        }
        return null;
    }


    private String generateName(ServerGroup group, int n) {
        return group.isStatic() ? group.getName() : group.getName() + "-" + n;
    }

    public ArrayList<GameServer> getServersFromSlave(Wrapper slave) {
        ArrayList<GameServer> serverList = new ArrayList<>();
        getServers().values().stream().forEach(server -> {
            if (server.getWrapper().getName().equalsIgnoreCase(slave.getName())) {
                serverList.add(server);
            }
        });
        return serverList;
    }
}
