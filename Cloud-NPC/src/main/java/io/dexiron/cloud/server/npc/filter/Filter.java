package io.dexiron.cloud.server.npc.filter;

import org.bukkit.entity.Player;

public class Filter {
    private Player player;
    private String map;
    private boolean fullServer;
    private String group;

    public Filter(Player player, String group) {
        this.player = player;
        this.group = group;
        this.fullServer = true;
        this.map = "None";
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }


    public boolean isFullServer() {
        return fullServer;
    }

    public void setFullServer(boolean fullServer) {
        this.fullServer = fullServer;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

}
