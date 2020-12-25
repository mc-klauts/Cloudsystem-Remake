package io.dexiron.cloud.server.npc;

import com.google.common.collect.Maps;
import io.dexiron.cloud.server.npc.commands.CommandSpawnNPC;
import io.dexiron.cloud.server.npc.filter.FilterManager;
import io.dexiron.cloud.server.npc.listener.ListenerInventoryClick;
import io.dexiron.cloud.server.npc.listener.ListenerNPCInteract;
import io.dexiron.cloud.server.npc.listener.ListenerPlayer;
import io.dexiron.cloud.server.npc.listener.ListenerServerInfoUpdate;
import io.dexiron.cloud.server.npc.npc.NPCManager;
import io.dexiron.cloud.server.npc.server.ServerManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class NPCPlugin extends JavaPlugin {

    @Getter
    public static NPCPlugin instance;

    @Getter
    private Map<String, Location> locationMap = Maps.newHashMap();

    @Getter
    private NPCManager npcManager;

    @Getter
    private ServerManager serverManager;

    @Getter
    private FilterManager filterManager;

    @Override
    public void onEnable() {
        instance = this;

        this.serverManager = new ServerManager();
        this.filterManager = new FilterManager();
        this.npcManager = new NPCManager();

        new ListenerPlayer(this);
        new CommandSpawnNPC(this);
        new ListenerNPCInteract(this);
        new ListenerInventoryClick(this);
        new ListenerServerInfoUpdate(this);
    }
}
