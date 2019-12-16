package me.mickmmars.factions.listener;

import me.mickmmars.factions.factions.inventory.FMapInventory;
import me.mickmmars.factions.factions.inventory.FactionInventory;
import me.mickmmars.factions.factions.rank.FactionRank;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

import java.util.UUID;

public class PlayerClickEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView view = player.getOpenInventory();
        if (event.getCurrentItem().getType().equals(Material.BLACK_STAINED_GLASS_PANE) && event.getCurrentItem().getItemMeta().getDisplayName().equals(" ")) { event.setCancelled(true); }
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(" ")) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§d§oFaction Discord")) {
            player.sendMessage("");
        }
        if (event.getView().getTitle().equals("§8-= §bYour factions relations §8=-")) {
            event.setCancelled(true);
        }

        for (FactionInventory.GUIPage value : FactionInventory.GUIPage.values()) {
                if (view.getTitle().equals(value.getName())) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack")) {
                        new FactionInventory(player.getUniqueId()).setItems().load();
                        return;
                    }
                    return;
                } else if (view.getTitle().equals("§a§oMembers")) {
                    if (event.getClick().isLeftClick() && !event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack")) {
                        if (FactionRank.getRankId(instance.getPlayerData(player).getFactionRank()) < 3) {
                            player.closeInventory();
                            player.sendMessage(Message.NO_PROMOTE_PERM.getMessage());
                            return;
                        }
                        instance.getFactionManager().promotePlayer(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_GOT_PROMOTED.getMessage().replace("%player%", event.getCurrentItem().getItemMeta().getDisplayName().replace("§e§o", "")).replace("%rank%", instance.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName().replace("§e§o", ""))).getFactionRank().getName()));
                        player.closeInventory();
                        new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.MEMBERS).load();
                    } else if (event.getClick().isRightClick() && !event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack")) {
                        if (FactionRank.getRankId(instance.getPlayerData(player).getFactionRank()) < 3) {
                            player.closeInventory();
                            player.sendMessage(Message.NO_DEMOTION_PERM.getMessage());
                            return;
                        }
                        instance.getFactionManager().demotePlayer(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_GOT_DEMOTED.getMessage().replace("%player%", event.getCurrentItem().getItemMeta().getDisplayName().replace("§e§o", "")).replace("%rank%", instance.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName().replace("§e§o", ""))).getFactionRank().getName()));
                        player.closeInventory();
                        new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.MEMBERS).load();
                    } else if (event.getClick().isLeftClick() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack") || event.getClick().isRightClick() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack")) {
                        event.setCancelled(true);
                        new FactionInventory(player.getUniqueId()).load();
                    }
                } else if (view.getTitle().equalsIgnoreCase("§8Chunk-map §0| §8Facing: §c" + player.getFacing().toString())) {
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§aClaim-fill map")) {
                        event.setCancelled(true);
                        instance.getFactionManager().fillClaimPlayerRadius(player);
                        player.closeInventory();
                        new FMapInventory(player.getUniqueId()).load();
                    }
                    if (!event.getCurrentItem().getType().equals(Material.AIR)) {
                        event.setCancelled(true);
                    }
                } else if (view.getTitle().equals("§a§oFlags")) {
                    if (event.getCurrentItem().getType().equals(Material.GREEN_BANNER)) {
                        event.setCancelled(true);
                        if (FactionRank.getRankId(instance.getPlayerData(player).getFactionRank()) < 3) {
                            player.sendMessage(Message.NO_FLAG_PERM.getMessage());
                            player.closeInventory();
                            return;
                        }
                        instance.getFactionManager().removeFlag(instance.getPlayerData(player).getCurrentFactionData(), instance.getFactionManager().getFlagByName(event.getCurrentItem().getItemMeta().getDisplayName()));
                        player.closeInventory();
                        new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.FLAGS).load();
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CHANGED_FLAG.getMessage().replace("%player%", player.getName()).replace("%flag%", "§c" + event.getCurrentItem().getItemMeta().getDisplayName()).replace("%value%", "§aEnabled"));
                    } else if (event.getCurrentItem().getType().equals(Material.RED_BANNER)) {
                        if (FactionRank.getRankId(instance.getPlayerData(player).getFactionRank()) < 3) {
                            player.sendMessage(Message.NO_FLAG_PERM.getMessage());
                            player.closeInventory();
                            return;
                        }
                        instance.getFactionManager().addFlag(instance.getPlayerData(player).getCurrentFactionData(), instance.getFactionManager().getFlagByName(event.getCurrentItem().getItemMeta().getDisplayName()));
                        player.closeInventory();
                        new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.FLAGS).load();
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CHANGED_FLAG.getMessage().replace("%player%", player.getName()).replace("%flag%", "§c" + event.getCurrentItem().getItemMeta().getDisplayName()).replace("%value%", "§cDisabled"));
                    }
                } else if (view.getTitle().equals("§a§oApplications")) {
                    if (event.getCurrentItem().getType().equals(Material.PLAYER_HEAD) && !event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack")) {
                        event.setCancelled(true);
                        player.performCommand("factions accept " + event.getCurrentItem().getItemMeta().getDisplayName());
                        player.closeInventory();
                    }
                }
        }

        if (view.getTitle().equals("§a§lYour faction")) {
            event.setCancelled(true);
            FactionInventory.GUIPage page = FactionInventory.GUIPage.getPageByName(event.getCurrentItem().getItemMeta().getDisplayName());
            if (page != null)
                new FactionInventory(player.getUniqueId()).setItems(page).load();
        }
        if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§d§oFaction Discord") && view.getTitle().equals("§a§lYour faction")) {
            player.closeInventory();
            player.sendMessage(Message.FACTION_DISCORD.getMessage().replace("%link%", instance.getPlayerData(player).getCurrentFactionData().getDiscordlink()));
        }

        if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§a§oFMap")) {
            player.closeInventory();
            new FMapInventory(player.getUniqueId()).setChunks().load();
        }
    }

}
