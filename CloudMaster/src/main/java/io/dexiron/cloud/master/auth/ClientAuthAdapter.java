package io.dexiron.cloud.master.auth;

import com.google.common.collect.Lists;
import com.mongodb.Block;
import com.mongodb.Cursor;
import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import io.dexiron.cloud.lib.config.auth.NettySession;
import io.dexiron.cloud.master.CloudBootsrap;
import io.netty.channel.Channel;
import lombok.Getter;
import org.bson.Document;

import java.util.List;

public class ClientAuthAdapter {

    @Getter
    private List<NettySession> nettySessions = Lists.newArrayList();

    public void loadFromDatabase() {
        MongoCollection<Document> sessionsCollection = CloudBootsrap.getInstance().getDatabaseConectionFactory().getCollection("sessions");

        FindIterable<Document> cursor = sessionsCollection.find().cursorType(CursorType.NonTailable);

        cursor.forEach(new Block<Document>() {
            @Override
            public void apply(Document document) {
                NettySession nettySession = new NettySession(document.getString("ip"), document.getString("name"), "", 0, null);

                nettySessions.add(nettySession);
            }
        });
    }

    public NettySession getSession(String name) {
        return nettySessions.stream().filter(nettySession -> nettySession.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public NettySession getSessionByIp(String ip) {
        return nettySessions.stream().filter(nettySession -> nettySession.getIp().equalsIgnoreCase(ip)).findFirst().orElse(null);
    }

    public void sessionCheck(String name, String ip, Channel channel, long time, String sessionId) {
        NettySession nettySession = getSessionByIp(ip);
        if (nettySession == null) {
            channel.close();
            System.out.println(String.format("Client isn't authorized! Client: %s, %s", name, ip));
        }else {
            nettySession.setTime(time);
            nettySession.setSessionId(sessionId);
            System.out.println(String.format("Client is authorized on Master! Client: %s, %s", name, ip));
        }
    }

}
