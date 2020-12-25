package io.dexiron.cloud.master.group;

public class ServerGroup {

    private String name, wrapper; //slave only if static
    private int minServer;
    private int maxServer, maxPlayers;
    private int ramPerServer;
    private boolean isStatic;

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setWrapper(String wrapper) {
        this.wrapper = wrapper;
    }

    public String getWrapper() {
        return wrapper;
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
