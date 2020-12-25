package io.dexiron.cloud.server.api.servergroup.events;

import io.dexiron.cloud.server.api.utils.ServerInfo;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerInfoUpdateEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    private final ServerInfo serverInfo;

    public ServerInfoUpdateEvent(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
