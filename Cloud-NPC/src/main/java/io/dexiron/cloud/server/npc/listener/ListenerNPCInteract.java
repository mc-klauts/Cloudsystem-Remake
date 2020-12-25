package io.dexiron.cloud.server.npc.listener;

import io.dexiron.cloud.server.npc.NPCPlugin;
import io.dexiron.cloud.server.npc.events.PlayerInteractNPCEvent;
import io.dexiron.cloud.server.npc.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class ListenerNPCInteract implements Listener {

    private final NPCPlugin plugin;

    public ListenerNPCInteract(NPCPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractNPCEvent event) {
        if (plugin.getNpcManager().getNpcs().containsKey(event.getId())) {
            Player player = event.getPlayer();
            NPC npc = plugin.getNpcManager().getNpcs().get(event.getId());
            Inventory inv = plugin.getServerManager().getServerInventory(player, npc.getGroup()).getInv();
            player.openInventory(inv);
        }
    }

}
