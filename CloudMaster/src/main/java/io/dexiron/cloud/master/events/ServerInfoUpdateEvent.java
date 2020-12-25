package io.dexiron.cloud.master.events;

import io.dexiron.cloud.master.game.GameServer;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ServerInfoUpdateEvent {
    private final GameServer gameServer;
}
