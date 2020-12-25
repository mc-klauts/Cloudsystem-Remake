package io.dexiron.cloud.server.api.servergroup.listener;

import io.dexiron.cloud.server.api.servergroup.CloudAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ListenerPlayer implements Listener {

    private final CloudAPI plugin;

    public ListenerPlayer(CloudAPI plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.updateServerInformation();
            }
        }.runTaskLater(plugin, 10L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.updateServerInformation();
            }
        }.runTaskLater(plugin, 10L);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.updateServerInformation();
            }
        }.runTaskLater(plugin, 10L);
    }
}
