package me.mickmmars.factions.listener;

import me.mickmmars.earth.base;
import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.inventory.FMapInventory;
import me.mickmmars.factions.factions.inventory.FactionInventory;
import me.mickmmars.factions.factions.perms.FactionPerms;
import me.mickmmars.factions.factions.rank.FactionRank;
import me.mickmmars.factions.factions.upgrades.FactionUpgrades;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.Factions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

import java.util.UUID;

public class PlayerClickEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    private Economy economy;

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType().equals(Material.AIR)) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        if (event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getType().equals(Material.BLACK_STAINED_GLASS_PANE) && event.getCurrentItem().getItemMeta().getDisplayName().equals(" ")) { event.setCancelled(true); }
        if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(" ")) return;
        Player player = (Player) event.getWhoClicked();
        InventoryView view = player.getOpenInventory();
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
                    if (event.getClick().isLeftClick() && !event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack") && !event.getCurrentItem().getType().equals(Material.AIR)) {
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
                    } else if (event.getClick().isRightClick() && !event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack") && !event.getCurrentItem().getType().equals(Material.AIR)) {
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
                    } else if (event.getClick().isLeftClick() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack") && !event.getCurrentItem().getType().equals(Material.AIR) || event.getClick().isRightClick() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack") && !event.getCurrentItem().getType().equals(Material.AIR)) {
                        event.setCancelled(true);
                        new FactionInventory(player.getUniqueId()).setItems().load();
                    }
                } else if (view.getTitle().equalsIgnoreCase("§8Chunk-map §0| §8Facing: §c" + player.getFacing().toString())) {
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§aClaim-fill map")) {
                        event.setCancelled(true);
                        instance.getFactionManager().fillClaimPlayerRadius(player);
                        player.closeInventory();
                        new FMapInventory(player.getUniqueId()).setChunks().load();
                    } else if (event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
                        event.setCancelled(true);
                        String[] arr = event.getCurrentItem().getItemMeta().getDisplayName().split("§7, ", 2);
                        int x = Integer.parseInt(arr[0].replace("§6", ""));
                        int z = Integer.parseInt(arr[1].replace("§6", ""));
                        Chunk chunk = Bukkit.getWorld(Config.FACTION_WORLD.getData().toString()).getChunkAt(x, z);
                        if (!instance.getPlayerData(player).isInFaction()) {
                            player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                            return;
                        }
                        if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.CLAIM) && !instance.getStaffmode().contains(player.getUniqueId())) {
                            player.sendMessage(Message.NO_CLAIM_PERM.getMessage());
                            return;
                        }
                        if (instance.getChunkManager().getFactionDataByChunk(chunk) != null) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.ALREADY_CLAIMED.getMessage()));
                            return;
                        }
                        if (instance.getPlayerData(player).getCurrentFactionData().getChunks().size() + 1 > instance.getPlayerData(player).getCurrentFactionData().getMaxPower() && !instance.getStaffmode().contains(player.getUniqueId())) {
                            player.sendMessage(Message.NO_CLAIMING_POWER.getMessage());
                            return;
                        }
                        FactionData factionData = instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId());
                        int price = (instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()).getChunks().size() >= 100 ? 5 * instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())).size() : 5);
                        if (!(factionData.getMoney() >= price)) {
                            int need = (price - instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()).getMoney());
                            String needString = instance.asDecimal(need);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NEED_MORE_TO_CLAIM.getMessage().replace("%need%", needString)));
                            return;
                        }
                        instance.getFactionManager().claimChunk(player, chunk, instance.getChunkPlayer(player).getPlayerData().getFactionId());
                        player.closeInventory();
                        new FMapInventory(player.getUniqueId()).setChunks().load();
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()))) {
                            if (Bukkit.getOnlinePlayers().contains(Bukkit.getOfflinePlayer(uuid))) {
                                Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CLAIMED.getMessage().replace("%player%", player.getName()).replace("%location%", x + ", " + z));
                            }
                        }
                    }
                    if (!event.getCurrentItem().getType().equals(Material.AIR)) {
                        event.setCancelled(true);
                    }
                } else if (view.getTitle().equals("§a§oFlags")) {
                    switch (event.getCurrentItem().getType()) {
                        case GREEN_BANNER:
                            event.setCancelled(true);
                            if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.EDITFLAGS)) {
                                player.sendMessage(Message.NO_FLAG_PERM.getMessage());
                                player.closeInventory();
                                return;
                            }
                            instance.getFactionManager().removeFlag(instance.getPlayerData(player).getCurrentFactionData(), instance.getFactionManager().getFlagByName(event.getCurrentItem().getItemMeta().getDisplayName()));
                            player.closeInventory();
                            new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.FLAGS).load();
                            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                                if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)))
                                    Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CHANGED_FLAG.getMessage().replace("%player%", player.getName()).replace("%flag%", "§c" + event.getCurrentItem().getItemMeta().getDisplayName()).replace("%value%", "§cDisabled"));
                            break;
                        case RED_BANNER:
                            event.setCancelled(true);
                            if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.EDITFLAGS)) {
                                player.sendMessage(Message.NO_FLAG_PERM.getMessage());
                                player.closeInventory();
                                return;
                            }
                            instance.getFactionManager().addFlag(instance.getPlayerData(player).getCurrentFactionData(), instance.getFactionManager().getFlagByName(event.getCurrentItem().getItemMeta().getDisplayName()));
                            player.closeInventory();
                            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                                if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)))
                                    Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CHANGED_FLAG.getMessage().replace("%player%", player.getName()).replace("%flag%", "§c" + event.getCurrentItem().getItemMeta().getDisplayName()).replace("%value%", "§aEnabled"));
                            break;
                        }
                } else if (view.getTitle().equals("§a§oApplications")) {
                    if (event.getCurrentItem().getType().equals(Material.PLAYER_HEAD) && !event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack") && event.isLeftClick()) {
                        event.setCancelled(true);
                        player.performCommand("factions accept " + event.getCurrentItem().getItemMeta().getDisplayName());
                        player.closeInventory();
                    }
                } else if (view.getTitle().equals("§a§oFaction upgrades")) {
                    if (event.getCurrentItem().getType().equals(Material.WHITE_STAINED_GLASS_PANE)) {
                        event.setCancelled(true);
                        Double price = null;
                        FactionUpgrades upgrade = null;
                        if (event.getSlot() == 0) {
                            price = (Double) Config.ONE_PUBLIC_WARP_PRICE.getData();
                            upgrade = FactionUpgrades.ONEPUBLICWARP;
                        } else if (event.getSlot() == 1) {
                            price = (Double) Config.TWO_PUBLIC_WARPS_PRICE.getData();
                            upgrade = FactionUpgrades.TWOPUBLICWARPS;
                        } else if (event.getSlot() == 2) {
                            price = (Double) Config.THREE_PUBLIC_WARPS_PRICE.getData();
                            upgrade = FactionUpgrades.THREEPUBLICWARPS;
                        } else if (event.getSlot() == 3) {
                            price = (Double) Config.FACTION_FLY_PRICE.getData();
                            upgrade = FactionUpgrades.FACTION_FLY;
                        } else if (event.getSlot() == 4) {
                            price = (Double) Config.MOREMEMBERS_PRICE.getData();
                            upgrade = FactionUpgrades.MOREMEMBERS;
                        } else if (event.getSlot() == 5) {
                            price = (Double) Config.DYNMAPCOLOUR_PRICE.getData();
                            upgrade = FactionUpgrades.DYNMAPCOLOUR;
                        }
                        EconomyResponse r = Factions.econ.withdrawPlayer(player, price);
                        if (r.transactionSuccess()) {
                            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                                if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)))
                                Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_PURCHASED_UPGRADE.getMessage().replace("%player%", player.getName()).replace("%upgrade%", event.getCurrentItem().getItemMeta().getDisplayName()));
                                instance.getFactionManager().addUpgradeToFaction(instance.getPlayerData(player).getCurrentFactionData(), upgrade);
                                player.closeInventory();
                                new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.UPGRADES).load();
                        } else {
                            player.sendMessage(Message.TRANSACTION_ERROR.getMessage());
                        }
                    }
                } else if (view.getTitle().equals("§a§oList")) {
                    if ((event.getCurrentItem().getType() == Material.PAPER) && !event.getCurrentItem().getItemMeta().getDisplayName().equals("§c§lSafeZone")) {
                        player.performCommand("f apply " + event.getCurrentItem().getItemMeta().getDisplayName().replace("§c§l", ""));
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
