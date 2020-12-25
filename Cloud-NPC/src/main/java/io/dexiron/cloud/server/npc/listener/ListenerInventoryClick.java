package io.dexiron.cloud.server.npc.listener;

import io.dexiron.cloud.server.api.servergroup.CloudAPI;
import io.dexiron.cloud.server.api.utils.ServerInfo;
import io.dexiron.cloud.server.npc.NPCPlugin;
import io.dexiron.cloud.server.npc.filter.Filter;
import io.dexiron.cloud.server.npc.inventories.ServerInventory;
import io.dexiron.cloud.server.npc.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ListenerInventoryClick implements Listener {

    private final NPCPlugin plugin;

    public ListenerInventoryClick(NPCPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() != null && event.getClickedInventory().getTitle() != null && event.getClickedInventory().getTitle().contains("§7Server")) {

            String group = ChatColor.stripColor(event.getClickedInventory().getTitle()).split(":")[1].replace(" ", "");
            ServerInventory inventory = NPCPlugin.getInstance().getServerManager().getServerInventory(player, group);
            Filter filter = inventory.getFilter();
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getTypeId() == 159) {
                    String server = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

                    ServerInfo info = CloudAPI.getInstance().getServerInfo(server);
                    if (info.getState().equalsIgnoreCase("ONLINE")) {
                        if (info.getPlayers() <= info.getMaxPlayers()) {
                            CloudAPI.getInstance().sendToServer(player, server);
                            player.closeInventory();
                        } else {
                            player.sendMessage("§cServer ist voll");
                        }
                    } else if (info.getPlayers() < (info.getMaxPlayers() + 8)) {
                        CloudAPI.getInstance().sendToServer(player, server);
                        player.closeInventory();
                    } else {
                        player.sendMessage("§cEs sind nicht genügend Spectator Plätze verfügbar ist voll");
                    }
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Weiter")) {
                    if (inventory.getPaginator().totalPages() >= (inventory.getPage() + 1)) {
                        return;
                    } else {
                        inventory.setPage(inventory.getPage() + 1);
                    }
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Zurück")) {
                    if (inventory.getPage() == 0) {
                        return;
                    } else {
                        inventory.setPage(inventory.getPage() - 1);
                    }
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Zeige Volle Server")) {
                    if (filter.isFullServer()) {
                        filter.setFullServer(false);
                        inventory.getInv().setItem(49, new ItemBuilder(Material.GOLD_INGOT, 1).setDisplayName("§cZeige Volle Server").build());
                    } else {
                        filter.setFullServer(true);
                        inventory.getInv().setItem(49, new ItemBuilder(Material.GOLD_INGOT, 1).setDisplayName("§aZeige Volle Server").build());
                    }
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Quick-Join")) {
                    if (inventory.getServers().isEmpty()) return;
                    ServerInfo server = inventory.getServers().get(0);
                    if (server != null) {
                        CloudAPI.getInstance().sendToServer(player, server.getName());
                    }
                }
            }

        }
    }

}
