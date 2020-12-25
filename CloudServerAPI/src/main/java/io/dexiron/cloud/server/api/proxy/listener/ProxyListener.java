package io.dexiron.cloud.server.api.proxy.listener;

import io.dexiron.cloud.server.api.proxy.CloudProxyAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public class ProxyListener implements Listener {

    private final CloudProxyAPI plugin;

    public ProxyListener(CloudProxyAPI plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onProxyPIng(ProxyPingEvent event) {
        ServerPing ping = event.getResponse();

        ServerPing.Protocol protocol = ping.getVersion();
        if (plugin.isMaintenance()) {
            protocol.setProtocol(2);
            protocol.setName("§cWartungsarbeiten");
            ping.setDescriptionComponent(new TextComponent("§cWartungsarbeiten"));
            ping.setVersion(protocol);
        } else {
            ping.setDescriptionComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', plugin.getMotd())));
            ping.setPlayers(new ServerPing.Players(plugin.getMaxPlayers(), ProxyServer.getInstance().getOnlineCount(), ping.getPlayers().getSample()));
        }

    }

}
