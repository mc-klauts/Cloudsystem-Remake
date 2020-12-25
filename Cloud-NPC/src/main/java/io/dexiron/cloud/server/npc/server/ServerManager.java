package io.dexiron.cloud.server.npc.server;

import io.dexiron.cloud.server.api.servergroup.CloudAPI;
import io.dexiron.cloud.server.npc.NPCPlugin;
import io.dexiron.cloud.server.npc.inventories.ServerInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {
    public ConcurrentHashMap<Player, ConcurrentHashMap<String, ServerInventory>> serverInventories = new ConcurrentHashMap<>();

    public ServerManager() {
    }

    public ServerInventory getServerInventory(Player player, String group) {
        ServerInventory inv = serverInventories.get(player).get(group);
        if (inv != null) {
            return inv;
        }

        return null;
    }

    public ConcurrentHashMap<String, ServerInventory> getServerInventorys(Player player) {
        return serverInventories.get(player);
    }

    public ConcurrentHashMap<Player, ConcurrentHashMap<String, ServerInventory>> getServerInventories() {
        return serverInventories;
    }

    public void setServerInventoriesFromPlayer(Player player, String group, ServerInventory serverInventory) {
        this.serverInventories.get(player).put(group, serverInventory);
    }

}
