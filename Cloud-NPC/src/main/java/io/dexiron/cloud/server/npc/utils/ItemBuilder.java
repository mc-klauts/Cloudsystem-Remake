package io.dexiron.cloud.server.npc.utils;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
    private final ItemStack item;

    public ItemBuilder(Material material) { this.item = new ItemStack(material); }



    public ItemBuilder(Material material, int amount) { this.item = new ItemStack(material, amount); }



    public ItemBuilder(Material material, int amount, short damage) { this.item = new ItemStack(material, amount, damage); }


    public ItemBuilder setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> list) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setLore(list);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setOwner(String owner) {
        SkullMeta meta = (SkullMeta) this.item.getItemMeta();
        meta.setOwner(owner);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() { return this.item; }
}
