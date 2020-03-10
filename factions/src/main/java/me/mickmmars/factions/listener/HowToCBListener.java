package me.mickmmars.factions.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class HowToCBListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals("§bHow to request a CB") && !event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
            event.setCancelled(true);
        }
    }

}
