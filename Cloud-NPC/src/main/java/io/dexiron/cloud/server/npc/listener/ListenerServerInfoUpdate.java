package io.dexiron.cloud.server.npc.listener;

import io.dexiron.cloud.server.api.servergroup.CloudAPI;
import io.dexiron.cloud.server.api.servergroup.events.ServerInfoUpdateEvent;
import io.dexiron.cloud.server.npc.NPCPlugin;
import io.dexiron.cloud.server.npc.inventories.ServerInventory;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.ConcurrentHashMap;

public class ListenerServerInfoUpdate implements Listener {

    private final NPCPlugin plugin;

    @Getter
    public ConcurrentHashMap<Player, ConcurrentHashMap<String, ServerInventory>> serverInventories = NPCPlugin.getInstance().getServerManager().getServerInventories();

    public ListenerServerInfoUpdate(NPCPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onServerInfoUpdate(ServerInfoUpdateEvent event) {
        System.out.println("Server info was updated!");
        if (!CloudAPI.getInstance().getServerGroups().isEmpty() && !serverInventories.isEmpty() && CloudAPI.getInstance().getServerGroups() != null && !CloudAPI.getInstance().getServerGroups().values().isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (serverInventories.containsKey(player)) {
                    NPCPlugin.getInstance().getServerManager().getServerInventorys(player).values().forEach(invs -> {
                        invs.generateGui();
                    });
                }
            });
        }
    }

}
