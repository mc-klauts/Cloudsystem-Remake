package io.dexiron.cloud.server.api.utils;

public class ServerGroup {
    private String name, slave; //slave only if static
    private int minServer;
    private int maxServer;
    private int ramPerServer;
    private boolean isStatic;

    public void setName(String name) {
        this.name = name;
    }

    public void setSlave(String slave) {
        this.slave = slave;
    }

    public String getSlave() {
        return slave;
    }

    public void setMaxServer(int maxServer) {
        this.maxServer = maxServer;
    }

    public void setMinServer(int minServer) {
        this.minServer = minServer;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public int getMaxServer() {
        return maxServer;
    }

    public String getName() {
        return name;
    }

    public void setRamPerServer(int ramPerServer) {
        this.ramPerServer = ramPerServer;
    }

    public int getRamPerServer() {
        return ramPerServer;
    }

    public int getMinServer() {
        return minServer;
    }
}
