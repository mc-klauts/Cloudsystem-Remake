package io.dexiron.cloud.server.api.utils;


public class ServerInfo {
    private int maxPlayers, players;
    private String name, extra, motd, state, group, slave;

    public ServerInfo(String name, String motd, String extra, String state, Integer players) {
        this.name =  name;
        this.motd = motd;
        this.extra = extra;
        this.state  = state;
        this.players = players;
    }

    public int getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getExtra() {
        return extra;
    }

    public String getGroup() {
        return group;
    }

    public String getMotd() {
        return motd;
    }

    public String getName() {
        return name;
    }

    public String getSlave() {
        return slave;
    }

    public String getState() {
        return state;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlave(String slave) {
        this.slave = slave;
    }

    public void setState(String state) {
        this.state = state;
    }
}
