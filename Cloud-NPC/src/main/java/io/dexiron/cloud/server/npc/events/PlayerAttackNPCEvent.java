package io.dexiron.cloud.server.npc.events;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;


public class PlayerAttackNPCEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Integer id;

    public PlayerAttackNPCEvent(Player who, int id) {
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
