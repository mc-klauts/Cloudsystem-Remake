package io.dexiron.cloud.server.api.proxy.commands;

import io.dexiron.cloud.server.api.proxy.CloudProxyAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandHub extends Command {

    private final CloudProxyAPI plugin;

    public CommandHub(CloudProxyAPI plugin) {
        super("hub", "", "lobby", "l");
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (!player.getServer().getInfo().getName().contains("Lobby")) {
            player.connect(plugin.getProxy().getServerInfo("Lobby-1"));
        } else {
            player.sendMessage("§cDu bist bereits auf einer Lobby");
        }
    }
}
