package me.mickmmars.factions.war.listeners;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.util.ItemBuilder;
import me.mickmmars.factions.war.WarManager;
import me.mickmmars.factions.war.data.CasusBelli;
import me.mickmmars.factions.war.events.CBDeletationEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class CBInvListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals("§bCBs")) {
            if (event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;
            if (event.getCurrentItem() == null) return;
            if (!event.getCurrentItem().getType().equals(Material.CAMPFIRE)) return;
            if (event.getClick().equals(ClickType.MIDDLE)) {
                event.setCancelled(true);
                CasusBelli cb = new WarManager().getCbById(event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", ""));
                new WarManager().acceptCb(cb);
                player.sendMessage(Message.CB_ACCEPTED.getMessage().replace("%id%", cb.getId()));
                event.getInventory().setItem(event.getSlot(), new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
            } else if (event.getClick().equals(ClickType.DROP)) {
                event.setCancelled(true);
                CasusBelli cb = new WarManager().getCbById(event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", ""));
                new WarManager().rejectCb(cb);
                player.sendMessage(Message.CB_ACCEPTED.getMessage().replace("%id%", cb.getId()));
                event.getInventory().setItem(event.getSlot(), new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
            } else if (event.getClick().equals(ClickType.LEFT)) {
                event.setCancelled(true);
                CasusBelli cb = new WarManager().getCbById(event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", ""));
                player.sendMessage("§7Evidences for CB §c" + cb.getId());
                for (String evidence : cb.getEvidence())
                    player.sendMessage("§b" + evidence);
            }
        } else if (event.getView().getTitle().equals("§bYour CBs")) {
            if (event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;
            if (event.getCurrentItem() == null) return;
            if (!event.getCurrentItem().getType().equals(Material.CAMPFIRE)) return;
            event.setCancelled(true);
            if (event.getClick().equals(ClickType.RIGHT)) {
                CasusBelli cb = instance.getWarManager().getCbById(event.getCurrentItem().getItemMeta().getDisplayName().replace("§b", ""));
                Bukkit.getPluginManager().callEvent(new CBDeletationEvent(cb));
                new WarManager().deleteCb(cb);
                player.sendMessage(Message.CB_REVOKED.getMessage().replace("%id%", cb.getId()));
                event.getInventory().setItem(event.getSlot(), new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
            }
        }

    }

}
