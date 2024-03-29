package me.mickmmars.factions.util;


import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack item;
    private List<String> lore = new ArrayList<String>();
    private ItemMeta meta;

    public ItemBuilder(Material mat, short subid, int amount) {
        item = new ItemStack(mat, amount, subid);
        meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material mat, short subid) {
        item = new ItemStack(mat, 1, subid);
        meta = item.getItemMeta();
    }

    public ItemBuilder(Material mat, int amount) {
        item = new ItemStack(mat, amount, (short) 0);
        meta = item.getItemMeta();
    }

    public ItemBuilder(Material mat) {
        item = new ItemStack(mat, 1, (short) 0);
        meta = item.getItemMeta();
    }

    public ItemBuilder setAmount(int value) {
        item.setAmount(value);
        return this;
    }

    public ItemBuilder setDurability(short value) {
        ((Damageable) this.meta).setDamage(value);
        return this;
    }

    public ItemBuilder setNoName() {
        meta.setDisplayName(" ");
        return this;
    }

    public ItemBuilder setGlow() {
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder setGlow(boolean value) {
        if (value) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        lore.add(line);
        return this;
    }

    public ItemBuilder addLoreArray(String[] lines) {
        lore.addAll(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder addLoreAll(List<String> lines) {
        lore.addAll(lines);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        ((SkullMeta) meta).setOwningPlayer(Bukkit.getPlayer(owner));
        return this;
    }

    public ItemBuilder setColor(Color c) {
        ((LeatherArmorMeta) meta).setColor(c);
        return this;
    }

    public ItemBuilder setBook(Enchantment enchantment, int level) {
        ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean value) {
        meta.setUnbreakable(value);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment ench, int lvl) {
        meta.addEnchant(ench, lvl, true);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public ItemStack build() {
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
}
