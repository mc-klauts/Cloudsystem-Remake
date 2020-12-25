package io.dexiron.cloud.lib.config.database;

import com.mongodb.client.MongoCollection;

public interface DatabaseConectionFactory {

    void connect(String host, int port, String database);

    void connect(String host, int port, String user, String password);

    void createCollection(String name);

    MongoCollection getCollection(String name);

     void disconnect();

     boolean notNull(Object object);

}
