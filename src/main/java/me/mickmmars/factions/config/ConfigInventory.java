package me.mickmmars.factions.config;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class ConfigInventory {
    private Factions instance = Factions.getInstance();
    private Inventory inventory;
    private final UUID uuid;
    private Player player;

    public ConfigInventory(UUID uuid) {
        this.uuid = uuid;
        player = Bukkit.getPlayer(uuid);
    }

    public void setConfigs() {
        inventory = Bukkit.createInventory(null, 9 * 3, "§b§lPixlies§3§lFaction§f§lSystem §8Config");
        inventory.addItem(new ItemBuilder(Material.COMMAND_BLOCK).setDisplayName("§cAutoUpdate").addLoreLine("§aEnabled").build());
        inventory.addItem(new ItemBuilder(Material.COMMAND_BLOCK).setDisplayName("§cAutoUpdate").addLoreLine("§aEnabled").build());
    }

    public void load() {
        player.openInventory(inventory);
    }

}
