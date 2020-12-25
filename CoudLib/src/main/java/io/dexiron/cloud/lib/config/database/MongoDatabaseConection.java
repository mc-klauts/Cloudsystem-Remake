package io.dexiron.cloud.lib.config.database;

import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDatabaseConection implements DatabaseConectionFactory {

    @Getter
    private MongoClient mongoClient;

    @Getter
    private MongoDatabase mongoDatabase;

    @Getter
    private final Map<String, MongoCollection<Document>> collections = Maps.newHashMap();

    @Override
    public void connect(String host, int port, String database) {
        this.mongoClient = new MongoClient(host, port);
        this.mongoDatabase = this.mongoClient.getDatabase(database);
        System.out.println("Connected to database " + mongoDatabase.getName());
        Logger mongoLogger = Logger.getLogger("org.mongodb");
        mongoLogger.setLevel(Level.OFF);
    }

    @Override
    public void connect(String host, int port, String user, String password) {

    }

    @Override
    public void createCollection(String name) {
        if (!this.collections.containsKey(name)) {
            this.collections.put(name, this.mongoDatabase.getCollection(name));
        }
    }

    @Override
    public MongoCollection getCollection(String name) {
        if (this.collections.containsKey(name)) {
            return this.collections.get(name);
        } else {
            createCollection(name);
            return getCollection(name);
        }
    }

    @Override
    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Disconnected to database!");
        }
    }

    @Override
    public boolean notNull(Object object) {
        if (object == null) {
           return true;
        }else {
            return false;
        }
    }
}
