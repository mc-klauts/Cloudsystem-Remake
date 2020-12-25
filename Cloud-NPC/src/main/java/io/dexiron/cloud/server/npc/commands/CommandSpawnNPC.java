package io.dexiron.cloud.server.npc.commands;

import io.dexiron.cloud.server.api.servergroup.CloudAPI;
import io.dexiron.cloud.server.npc.NPCPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawnNPC implements CommandExecutor {

    private final NPCPlugin plugin;

    public CommandSpawnNPC(NPCPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("npc").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;
        if (player.hasPermission("dexiron.command.npc")) {
            if (args.length == 4) {
                String arg = args[0];
                if (arg.equalsIgnoreCase("add")) {
                    String displayName = args[1];
                    String skin = args[2];
                    String group = args[3];
                    if (CloudAPI.getInstance().getServerGroups().containsKey(group)) {
                        NPCPlugin.getInstance().getNpcManager().addNPC(player, skin, group, player.getLocation(), displayName);
                        player.sendMessage("§aNPC spawned!");
                    } else {
                        player.sendMessage("§cServer-Gruppe existiert nicht");
                    }
                }
            } else {
                error(player);
            }
        }
        return false;
    }

    private void error(Player player) {
        player.sendMessage("§7/npc add <Displayname> <Skin> <Group>");
    }
}
