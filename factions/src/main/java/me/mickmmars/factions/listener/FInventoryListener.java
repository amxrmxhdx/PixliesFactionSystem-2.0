package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FInventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals("§aF-Chest")) {
            Factions.getInstance().factionchests.getConfiguration().set(Factions.getInstance().getPlayerData(player).getFactionId(), event.getView().getTopInventory().getContents());
            Factions.getInstance().factionchests.save();
            Factions.getInstance().factionchests.reload();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().equals("§aF-Chest")) {
            Factions.getInstance().factionchests.getConfiguration().set(Factions.getInstance().getPlayerData(player).getFactionId(), event.getView().getTopInventory().getContents());
            Factions.getInstance().factionchests.save();
            Factions.getInstance().factionchests.reload();
        }
    }

}
