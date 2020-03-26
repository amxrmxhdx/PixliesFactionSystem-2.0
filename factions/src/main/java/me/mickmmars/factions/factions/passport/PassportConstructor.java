package me.mickmmars.factions.factions.passport;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class PassportConstructor {

    private Factions instance = Factions.getInstance();

    private final UUID uuid;
    private Inventory inventory;
    private Player player;

    public PassportConstructor(UUID uuid) {
        this.uuid = uuid;
        player = Bukkit.getPlayer(uuid);
    }

    public PassportConstructor setItems() {
        inventory = Bukkit.createInventory(null, 9 * 6, "§bPassport constructor");

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setNoName().build());


        return this;
    }

}
