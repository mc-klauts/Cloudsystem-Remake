package io.dexiron.cloud.master.network.packets.in;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.master.CloudBootsrap;
import io.dexiron.cloud.master.game.GameServer;
import io.dexiron.cloud.master.group.ServerGroup;
import io.dexiron.cloud.master.wrapper.Wrapper;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.io.IOException;
import java.util.List;

public class PacketInGameServerAction implements Packet {

    private  String name;
    private  int amount;
    private  Direction direction;

    public PacketInGameServerAction() {
    }

    @Override
    public void write(ByteBufOutputStream byteBuf) throws IOException {
        byteBuf.writeUTF(this.name);
        byteBuf.writeInt(this.amount);
        byteBuf.writeInt(this.direction.ordinal());
    }

    @Override
    public void read(ByteBufInputStream byteBuf) throws IOException {
        this.name = byteBuf.readUTF();
        this.amount = byteBuf.readInt();
        this.direction = Direction.class.getEnumConstants()[byteBuf.readInt()];
    }

    @Override
    public Packet handle(Channel channel) throws IOException {
        switch (direction){
            case START:
                ServerGroup group = CloudBootsrap.getInstance().getCloudManager().getServerGroups().get(this.name);
                if (group != null) {
                    for (int i = 0; i < this.amount; i++) {
                        Wrapper wrapper = CloudBootsrap.getInstance().getCloudManager().getFreeWrapper(group);
                        if (wrapper != null) {
                            CloudBootsrap.getInstance().getCloudManager().startServer(group, wrapper);
                        }
                    }
                }
                break;

            case STOP:
                GameServer server = CloudBootsrap.getInstance().getCloudManager().getServerByName(this.name);
                if (server != null) {
                    if (!server.isStarting()) {
                        server.stop();
                    }
                }
                break;

            case STOPGROUP:
                ServerGroup servergroup = CloudBootsrap.getInstance().getCloudManager().getServerGroups().get(this.name);
                if (servergroup != null) {
                    List<GameServer> servers = CloudBootsrap.getInstance().getCloudManager().getServersByGroup(servergroup);
                    if (servers.size() != 0) {
                        servers.stream().forEach(gameServer -> {
                            gameServer.stop();
                        });
                    }
                }
                break;
        }
        return null;
    }

    public enum Direction {
        START, STOP, STOPGROUP;
    }

}
