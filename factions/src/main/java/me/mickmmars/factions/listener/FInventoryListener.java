package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FInventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals("§aF-Chest")) {
            List<ItemStack> itemstacks = new ArrayList<ItemStack>();
            for (ItemStack items : event.getInventory().getContents())
                itemstacks.add(items);
            Factions.getInstance().factionchests.getConfiguration().set(Factions.getInstance().getPlayerData(player).getFactionId(), itemstacks);
            Factions.getInstance().factionchests.save();
            Factions.getInstance().factionchests.reload();
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                if (viewer.getOpenInventory().getTitle().equals("$aF-Chest") && Factions.getInstance().getPlayerData(viewer).getCurrentFactionData().equals(Factions.getInstance().getPlayerData(player).getCurrentFactionData()))
                viewer.closeInventory();
                viewer.performCommand("f chest");
            }

        }
    }

}
