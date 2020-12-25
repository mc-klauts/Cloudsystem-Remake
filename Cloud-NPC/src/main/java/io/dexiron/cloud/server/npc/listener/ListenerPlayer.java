package io.dexiron.cloud.server.npc.listener;

import io.dexiron.cloud.lib.config.networking.Packet;
import io.dexiron.cloud.server.npc.NPCPlugin;
import io.dexiron.cloud.server.npc.inventories.ServerInventory;
import io.dexiron.cloud.server.npc.packetreader.PacketReader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;

public class ListenerPlayer implements Listener {

    private final NPCPlugin plugin;


    public ListenerPlayer(NPCPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PacketReader.inject(player);

        if (!NPCPlugin.getInstance().getNpcManager().getNpcs().isEmpty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ConcurrentHashMap<String, ServerInventory> invs = new ConcurrentHashMap<>();

                    NPCPlugin.getInstance().getNpcManager().getNpcs().values().forEach(npc -> {
                        npc.spawn(player);
                        invs.put(npc.getGroup(), new ServerInventory(player, npc.getGroup()));
                    });
                    NPCPlugin.getInstance().getServerManager().serverInventories.put(player, invs);

                }
            }.runTaskLater(NPCPlugin.getInstance(), 20);
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PacketReader.uninject();

        NPCPlugin.getInstance().getServerManager().serverInventories.remove(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        PacketReader.uninject();

        NPCPlugin.getInstance().getServerManager().serverInventories.remove(event.getPlayer());
    }

}
