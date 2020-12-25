package io.dexiron.cloud.server.api.proxy.commands;

import io.dexiron.cloud.server.api.proxy.CloudProxyAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandServers extends Command {

    private final CloudProxyAPI plugin;

    public CommandServers(CloudProxyAPI plugin) {
        super("servers");
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) cs;
        if (args.length == 2) {
            String arg = args[0];
            String value = args[1];

            if (arg.equalsIgnoreCase("stop")) {
                CloudProxyAPI.getInstance().stopGameServer(value);
                player.sendMessage("§7Der §7Server §8(§e" + value + "§8) §7wird §cgestoppt");
            } else if (arg.equalsIgnoreCase("stopgroup")) {
                CloudProxyAPI.getInstance().stopServerGroup(value);
                player.sendMessage("§7Die §7Server §7der §7Gruppe §8(§e" + value + "§8) §7werden §cgestoppt");
            }

        } else if (args.length == 3) {
            String arg = args[0];
            String group = args[1];
            int amount = Integer.valueOf(args[2]);
            if (arg.equalsIgnoreCase("start")) {
                CloudProxyAPI.getInstance().startGameServer(group, amount);
                player.sendMessage("§7Es werden §7Server §7der §7Gruppe §8(§e" + group.toUpperCase() + "§8|§e" + amount + "§8) §agestartet");
            }
        }
    }
}
