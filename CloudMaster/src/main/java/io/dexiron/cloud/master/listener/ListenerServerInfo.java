package io.dexiron.cloud.master.listener;

import com.google.common.eventbus.Subscribe;
import io.dexiron.cloud.master.CloudBootsrap;
import io.dexiron.cloud.master.events.ServerInfoUpdateEvent;

public class ListenerServerInfo {

    @Subscribe
    public void onHandle(ServerInfoUpdateEvent event) {
        System.out.println("event called" + event.getGameServer().getName() + event.getGameServer().getExtra() + event.getGameServer().getMotd() + event.getGameServer().getState() + event.getGameServer().getPlayers());

        CloudBootsrap.getInstance().getCloudManager().sendDataToServer();

    }

}
