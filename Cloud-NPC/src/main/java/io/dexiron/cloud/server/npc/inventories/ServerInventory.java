package io.dexiron.cloud.server.npc.inventories;

import io.dexiron.cloud.server.api.servergroup.CloudAPI;
import io.dexiron.cloud.server.api.utils.ServerInfo;
import io.dexiron.cloud.server.npc.filter.Filter;
import io.dexiron.cloud.server.npc.paginator.Paginator;
import io.dexiron.cloud.server.npc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerInventory {
    private Player player;
    private Inventory inv;
    private String group;
    private Filter filter;
    private int page;
    private Paginator<ServerInfo> paginator;
    private CopyOnWriteArrayList<ServerInfo> servers;

    public ServerInventory(Player player, String group) {
        this.player = player;
        this.group = group;
        this.filter = new Filter(player, group);
        this.page = 0;
        this.servers = new CopyOnWriteArrayList<>();
        generateGui();
    }

    public void generateGui() {
        if (inv == null) {
            inv = Bukkit.createInventory(null, InventoryType.CHEST.getDefaultSize() * 2, "§7Server§8: §6§l" + group);

            for (int i = 0; i <= 8; i++) {
                inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).setDisplayName("§b").build());
            }

            for (int i = 9; i <= 35; i++) {
                inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayName("§b").build());
            }

            for (int i = 45; i <= 53; i++) {
                inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 0).setDisplayName("§b").build());
            }



            inv.setItem(4, new ItemBuilder(Material.SIGN, 1).setDisplayName("§6" + group).build());
            inv.setItem(47, new ItemBuilder(Material.SKULL_ITEM, 1, (short) 3).setDisplayName("§8« §cZurück").setOwner("MHF_ArrowLeft").build());
            inv.setItem(49, new ItemBuilder(Material.NETHER_STAR, 1).setDisplayName("§9Quick-Join").build());
            inv.setItem(51, new ItemBuilder(Material.SKULL_ITEM, 1, (short) 3).setDisplayName("§cWeiter §8»").setOwner("MHF_ArrowRight").build());
        }

        synchronized (this) {
            CopyOnWriteArrayList<ServerInfo> servers = new CopyOnWriteArrayList<ServerInfo>();
            servers.addAll(CloudAPI.getInstance().getServerFromGroup(group));

            this.servers = servers;

            if (servers.isEmpty()) {
                return;
            }

            this.paginator = new Paginator<ServerInfo>(27, servers);

            List<ServerInfo> page = paginator.getPage(this.page);
            for (int i = 9; i < 9 + page.size(); i++) {
                inv.setItem(i, getItem(page.get(i - 9)));
            }
        }
    }

    private ItemStack getItem(ServerInfo info) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.getMaterial(159), 1, (short) 5);
        itemBuilder.setDisplayName("§9" + info.getName());
        itemBuilder.setLore(Arrays.asList("§7Map§8: §a" + info.getExtra(), "§7Spieler§8: §a" + info.getPlayers() + "§8/§a" + 20));
        return itemBuilder.build();
    }

    public List<ServerInfo> getServers() {
        return servers;
    }

    public Paginator<ServerInfo> getPaginator() {
        return paginator;
    }

    public int getPage() {
        return page;
    }

    public Filter getFilter() {
        return filter;
    }

    public String getGroup() {
        return group;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInv() {
        return inv;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
