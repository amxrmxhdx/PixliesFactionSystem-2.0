package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.inventory.FactionInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

public class PlayerClickEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView view = player.getOpenInventory();

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        for (FactionInventory.GUIPage value : FactionInventory.GUIPage.values()) {
            if (view.getTitle().equals("§a§l" + value.getName())) {
                event.setCancelled(true);

                if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Back")) {
                    new FactionInventory(player.getUniqueId()).setItems().load();
                    return;
                }
                return;
            }
        }

        if (view.getTitle().equals("§a§lFactions menu" + (instance.getPlayerData(player).isInFaction() ? " §8» §b§o" + instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()).getName() : ""))) {
            event.setCancelled(true);
            FactionInventory.GUIPage page = FactionInventory.GUIPage.getPageByMaterial(event.getCurrentItem().getType());
            if (page != null)
                new FactionInventory(player.getUniqueId()).setItems(page).load();
        }
    }

}
