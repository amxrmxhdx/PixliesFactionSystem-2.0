package me.mickmmars.factions.factions.puppets;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.upgrades.FactionUpgrades;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.List;

public class PuppetManager {

    private Factions instance = Factions.getInstance();

    public void createPuppetsInventory(Player player) {
        FactionData faction = instance.getPlayerData(player).getCurrentFactionData();
        Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§bPuppets");
        for (FactionData puppet : listPuppets(faction)) {
            ItemStack item = new ItemStack(instance.getFactionManager().getFactionFlag(puppet).getType());
            BannerMeta meta = (BannerMeta) instance.getFactionManager().getFactionFlag(puppet).getItemMeta();
            meta.setDisplayName("§c§l" + puppet.getName());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            item.setItemMeta(meta);
            inventory.addItem(item);
        }
        player.openInventory(inventory);
    }

    public void addPuppet(FactionData overlord, FactionData puppet, Player player) {
        if (listPuppets(overlord).contains(puppet)) return;
        puppet.setOverlord(overlord.getId());
        instance.getFactionManager().updateFactionData(puppet);
    }

    public void removePuppet(FactionData overlord, FactionData puppet) {
        if (!listPuppets(overlord).contains(puppet)) return;
        puppet.setOverlord("");
        instance.getFactionManager().updateFactionData(puppet);
    }

    public void movePuppet(FactionData newLord, FactionData puppet) {
        if (listPuppets(newLord).contains(puppet)) return;
        puppet.setOverlord(newLord.getId());
        instance.getFactionManager().updateFactionData(puppet);
    }

    public List<FactionData> listPuppets(FactionData faction) {
        List<FactionData> puppets = new ArrayList<>();
        for (FactionData data : instance.getFactionManager().getFactions())
                if (data.getOverlord().equals(faction.getId()))
                    puppets.add(data);
        return puppets;
    }

}
