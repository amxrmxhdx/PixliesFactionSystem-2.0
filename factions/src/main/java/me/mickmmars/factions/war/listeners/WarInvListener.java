package me.mickmmars.factions.war.listeners;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.war.WarManager;
import me.mickmmars.factions.war.events.StaffNeededEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class WarInvListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;
        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().getType().equals(Material.CAMPFIRE)) return;
        if (event.getView().getTitle().equals("§cWhich CB do you want to use?")) {
            if (event.getClick().equals(ClickType.LEFT)) {
                event.setCancelled(true);
                boolean staffon = false;
                for (Player oplayers : Bukkit.getOnlinePlayers())
                    if (oplayers.hasPermission("factions.staff"))
                        staffon = true;
                if (!staffon) {
                    player.sendMessage(Message.NO_STAFF_ONLINE.getMessage());
                    Bukkit.getPluginManager().callEvent(new StaffNeededEvent(new WarManager().getCbById(event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", ""))));
                    return;
                }
                if (instance.getFactionManager().getFactionById(new WarManager().getCbById(event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", "")).getAttackerId()).getCapitalLocation() == null) {
                    player.sendMessage(Message.FACTION_HOME_NEEDS_TO_BE_SET.getMessage());
                    return;
                }
                if (instance.getFactionManager().getFactionById(new WarManager().getCbById(event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", "")).getDefenderId()).getCapitalLocation() == null) {
                    player.sendMessage(Message.ENEMY_NEEDS_FACTION_HOME.getMessage());
                    return;
                }
                player.closeInventory();
                new WarManager().startWar(new WarManager().getCbById(event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", "")));
            }
        }
    }

}
