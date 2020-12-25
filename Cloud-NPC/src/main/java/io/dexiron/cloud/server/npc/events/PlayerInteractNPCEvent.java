package io.dexiron.cloud.server.npc.events;

import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;


public class PlayerInteractNPCEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    private Integer id;

    public PlayerInteractNPCEvent(Player who, int id) {
        super(who);
        this.id = id;
    }

    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
