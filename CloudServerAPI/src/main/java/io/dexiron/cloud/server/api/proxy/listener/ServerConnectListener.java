package io.dexiron.cloud.server.api.proxy.listener;

import io.dexiron.cloud.server.api.proxy.CloudProxyAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerConnectListener implements Listener {

    private final CloudProxyAPI plugin;

    public ServerConnectListener(CloudProxyAPI plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player.getServer() == null) {
            ServerInfo info = ProxyServer.getInstance().getServerInfo("Lobby-1");
            if (info == null) {
                return;
            }
            event.setTarget(info);
        }
    }
    @EventHandler
    public void onServerConnect(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        //CloudProxyAPI.getInstance().getCloudPlayerRegistry().update(player.getUniqueId().toString(), player.getName());

        if (CloudProxyAPI.getInstance().isMaintenance()){
            if (!player.hasPermission("dexiron.maintenance.join")) {
                player.disconnect(new TextComponent("§cWartungsarbeiten!"));
            }
        }
    }

}
