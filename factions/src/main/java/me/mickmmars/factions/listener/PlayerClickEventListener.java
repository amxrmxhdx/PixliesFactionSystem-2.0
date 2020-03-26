package me.mickmmars.factions.listener;

import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.ideologies.Ideology;
import me.mickmmars.factions.factions.inventory.FMapInventory;
import me.mickmmars.factions.factions.inventory.FactionInventory;
import me.mickmmars.factions.factions.perms.FactionPerms;
import me.mickmmars.factions.factions.rank.FactionRank;
import me.mickmmars.factions.factions.religion.Religion;
import me.mickmmars.factions.factions.upgrades.FactionUpgrades;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.Factions;
import me.mickmmars.factions.util.ItemBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.v1_12_R1.BlockGlass;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.dynmap.factions.DynmapFactionsPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerClickEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    private Economy economy;

    public String rpGetPlayerDirection(Player playerSelf){
        String dir = "";
        float y = playerSelf.getLocation().getYaw();
        if( y < 0 ){y += 360;}
        y %= 360;
        int i = (int)((y+8) / 22.5);
        if(i == 0){dir = "west";}
        else if(i == 1){dir = "west northwest";}
        else if(i == 2){dir = "northwest";}
        else if(i == 3){dir = "north northwest";}
        else if(i == 4){dir = "north";}
        else if(i == 5){dir = "north northeast";}
        else if(i == 6){dir = "northeast";}
        else if(i == 7){dir = "east northeast";}
        else if(i == 8){dir = "east";}
        else if(i == 9){dir = "east southeast";}
        else if(i == 10){dir = "southeast";}
        else if(i == 11){dir = "south southeast";}
        else if(i == 12){dir = "south";}
        else if(i == 13){dir = "south southwest";}
        else if(i == 14){dir = "southwest";}
        else if(i == 15){dir = "west southwest";}
        else {dir = "west";}
        return dir;
    }

    @EventHandler
    public void handleClick(InventoryClickEvent event) throws InterruptedException {
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType().equals(Material.AIR)) return;
        if (event.getView().getTitle().equals("§8-= §bYour factions relations §8=-")) event.setCancelled(true);
        if (event.getView().getTitle().equals("§bPuppets")) event.setCancelled(true);
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        if (event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE) && event.getCurrentItem().getDurability() == 15 && event.getCurrentItem().getItemMeta().getDisplayName().equals(" ")) {
            event.setCancelled(true);
        }
        if (event.getView().getTitle().equals(FactionInventory.GUIPage.SETTINGS.getName())) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType().name().contains("STAINED_GLASS_PANE") && event.getSlot() == 0) {
                if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.CHANGE_POLITY)) {
                    player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                    return;
                }
                List<String> listkp = new ArrayList<>();
                for (Ideology ideology : Ideology.listIdeologies()) {
                    if (!Ideology.listFreeIdeologies().contains(ideology) && !instance.getFactionManager().hasPurchasedUpgrade(instance.getPlayerData(player).getCurrentFactionData(), FactionUpgrades.getByName(ideology.getUpgrade()))) continue;
                    if (!ideology.getName().equalsIgnoreCase(instance.getPlayerData(player).getCurrentFactionData().getIdeology())) {
                        event.getInventory().setItem(9 + listkp.size(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(ideology.getName()).addLoreLine("§7§oClick to change ideology...").build());
                    } else {
                        event.getInventory().setItem(9 + listkp.size(), new ItemBuilder(Material.STAINED_GLASS_PANE).setGlow().setDisplayName(ideology.getName()).addLoreLine("§7§oClick to change ideology...").build());
                    }
                    listkp.add(instance.generateKey(7));
                }
                return;
            } else if (event.getCurrentItem().getType().name().contains("STAINED_GLASS_PANE") && event.getCurrentItem().getItemMeta().getLore().contains("§7§oClick to change ideology...")) {
                instance.getPlayerData(player).getCurrentFactionData().setIdeology(Ideology.getIdeologyByName(event.getCurrentItem().getItemMeta().getDisplayName()));
                new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.SETTINGS).load();
                instance.getPlayerData(player).getCurrentFactionData().sendMessageToMembers(Message.CHANGED_IDEOLOGY.getMessage().replace("%player%", player.getDisplayName()).replace("%ideology%", event.getCurrentItem().getItemMeta().getDisplayName()));
            } else if (event.getCurrentItem().getType().name().contains("STAINED_GLASS_PANE") && event.getSlot() == 1 && event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§7Religion: ")) {
                if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.CHANGE_RELIGION)) {
                    player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                    return;
                }
                List<String> listkp = new ArrayList<>();
                for (Religion religion : Religion.listReligions()) {
                    if (!religion.getName().equalsIgnoreCase(instance.getPlayerData(player).getCurrentFactionData().getReligion())) {
                        event.getInventory().setItem(9 + listkp.size(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(religion.getName()).addLoreLine("§7§oClick to change religion...").setColor(religion.getColor()).build());
                    } else {
                        event.getInventory().setItem(9 + listkp.size(), new ItemBuilder(Material.STAINED_GLASS_PANE).setGlow().setDisplayName(religion.getName()).setColor(religion.getColor()).addLoreLine("§7§oClick to change religion...").build());
                    }
                    listkp.add(instance.generateKey(7));
                }
                return;
            } else if (event.getCurrentItem().getType().name().contains("STAINED_GLASS_PANE") && event.getCurrentItem().getItemMeta().getLore().contains("§7§oClick to change religion...")) {
                instance.getPlayerData(player).getCurrentFactionData().setReligion(Religion.getByName(event.getCurrentItem().getItemMeta().getDisplayName()));
                instance.getPlayerData(player).getCurrentFactionData().updateData();
                new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.SETTINGS).load();
                instance.getPlayerData(player).getCurrentFactionData().sendMessageToMembers(Message.CHANGED_RELIGION.getMessage().replace("%player%", player.getDisplayName()).replace("%religion%", event.getCurrentItem().getItemMeta().getDisplayName()));
            }
        }
        if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(" ")) return;
        InventoryView view = player.getOpenInventory();
        if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§d§oFaction Discord")) {
            player.sendMessage("");
        }
        if (event.getView().getTitle().equals("§8-= §bYour factions relations §8=-")) {
            event.setCancelled(true);
        }
        if (event.getView().getTitle().equals("§8-= §bBanned players §8=-")) event.setCancelled(true);

        for (FactionInventory.GUIPage value : FactionInventory.GUIPage.values()) {
            if (view.getTitle().equals(value.getName()) && !value.getName().equals(FactionInventory.GUIPage.LIST.getName())) {
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
                    if (instance.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName())).getFactionRank().equals(FactionRank.ADMIN)) {
                        player.closeInventory();
                        player.sendMessage(Message.PROMOTION_NOT_POSSIBLE.getMessage());
                        return;
                    }
                    if (instance.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName())).getFactionRank().equals(FactionRank.LEADER)) {
                        player.closeInventory();
                        player.sendMessage(Message.PROMOTION_NOT_POSSIBLE.getMessage());
                        return;
                    }
                    instance.getFactionManager().promotePlayer(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
                    for (UUID uuid : instance.getPlayerData(player).getCurrentFactionData().getOnlineMembers())
                        Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_GOT_PROMOTED.getMessage().replace("%player%", event.getCurrentItem().getItemMeta().getDisplayName().replace("§e§o", "")).replace("%rank%", instance.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName().replace("§e§o", ""))).getFactionRank().getName()));
                    player.closeInventory();
                    new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.MEMBERS).load();
                } else if (event.getClick().isRightClick() && !event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack") && !event.getCurrentItem().getType().equals(Material.AIR)) {
                    if (FactionRank.getRankId(instance.getPlayerData(player).getFactionRank()) < 3) {
                        player.closeInventory();
                        player.sendMessage(Message.NO_DEMOTION_PERM.getMessage());
                        return;
                    }
                    if (instance.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName())).getFactionRank().equals(FactionRank.ADMIN) && instance.getPlayerData(player).getFactionRank().equals(FactionRank.ADMIN)) {
                        player.closeInventory();
                        player.sendMessage(Message.DEMOTION_NOT_POSSIBLE.getMessage());
                        return;
                    }
                    if (instance.getPlayerData(Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName())).getFactionRank().equals(FactionRank.LEADER)) {
                        player.closeInventory();
                        player.sendMessage(Message.DEMOTION_NOT_POSSIBLE.getMessage());
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
            } else if (view.getTitle().equalsIgnoreCase("§8Chunk-map §0| §8Facing: §c" + rpGetPlayerDirection(player))) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§aClaim-fill map")) {
                    event.setCancelled(true);
                    instance.getFactionManager().fillClaimPlayerRadius(player);
                    player.closeInventory();
                    new FMapInventory(player.getUniqueId()).setChunks().load();
                } else if (event.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE) && event.getCurrentItem().getDurability() == 5) {
                    event.setCancelled(true);
                    String[] arr = event.getCurrentItem().getItemMeta().getDisplayName().split("§7, ", 2);
                    int x = Integer.parseInt(arr[0].replace("§6", ""));
                    int z = Integer.parseInt(arr[1].replace("§6", ""));
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.CLAIM) && !instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_CLAIM_PERM.getMessage());
                        return;
                    }
                    if (instance.getChunkManager().getFactionDataByChunk(x, z) != null) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.ALREADY_CLAIMED.getMessage()));
                        return;
                    }
                    if (instance.getPlayerData(player).getCurrentFactionData().getChunks().size() + 1 > instance.getPlayerData(player).getCurrentFactionData().getPower() && !instance.getStaffmode().contains(player.getUniqueId())) {
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
                    instance.getFactionManager().claimChunk(player, x, z, instance.getChunkPlayer(player).getPlayerData().getFactionId(), false);
                    event.getInventory().setItem(event.getSlot(), new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.CYAN).setDisplayName("§6" + x + "§7, §6" + z).addLoreLine("§7Claimed by: §b" + instance.getChunkManager().getFactionDataByChunk(x, z).getName()).build());
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()))) {
                        if (Bukkit.getOnlinePlayers().contains(Bukkit.getOfflinePlayer(uuid))) {
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CLAIMED.getMessage().replace("%player%", player.getName()).replace("%location%", x + ", " + z));
                        }
                    }
                }
                if (!event.getCurrentItem().getType().equals(Material.AIR)) {
                    event.setCancelled(true);
                }
            } else if (view.getTitle().equals("§a§oFlags") && event.getCurrentItem().getType().equals(Material.BANNER)) {
                switch (event.getCurrentItem().getDurability()) {
                    case 5:
                        event.setCancelled(true);
                        if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.EDITFLAGS)) {
                            player.sendMessage(Message.NO_FLAG_PERM.getMessage());
                            player.closeInventory();
                            return;
                        }
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData())) {
                            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)) && instance.getFactionManager().checkIfFacHasFlagEnabled(instance.getPlayerData(player).getCurrentFactionData(), instance.getFactionManager().getFlagByName(event.getCurrentItem().getItemMeta().getDisplayName())))
                                Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CHANGED_FLAG.getMessage().replace("%player%", player.getName()).replace("%flag%", "§c" + event.getCurrentItem().getItemMeta().getDisplayName()).replace("%value%", "§cDisabled"));
                        }
                        instance.getFactionManager().removeFlag(instance.getPlayerData(player).getCurrentFactionData(), instance.getFactionManager().getFlagByName(event.getCurrentItem().getItemMeta().getDisplayName()));
                        new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.FLAGS).load();
                        break;
                    case 14:
                        event.setCancelled(true);
                        if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.EDITFLAGS)) {
                            player.sendMessage(Message.NO_FLAG_PERM.getMessage());
                            player.closeInventory();
                            return;
                        }
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData())) {
                            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid))  && !instance.getFactionManager().checkIfFacHasFlagEnabled(instance.getPlayerData(player).getCurrentFactionData(), instance.getFactionManager().getFlagByName(event.getCurrentItem().getItemMeta().getDisplayName())))
                                Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CHANGED_FLAG.getMessage().replace("%player%", player.getName()).replace("%flag%", "§c" + event.getCurrentItem().getItemMeta().getDisplayName()).replace("%value%", "§aEnabled"));
                        }
                        instance.getFactionManager().addFlag(instance.getPlayerData(player).getCurrentFactionData(), instance.getFactionManager().getFlagByName(event.getCurrentItem().getItemMeta().getDisplayName()));
                        player.closeInventory();
                        new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.FLAGS).load();
                        break;
                }
            } else if (view.getTitle().equals("§a§oApplications")) {
                if (!(!Objects.equals(event.getCurrentItem().getType(), Material.SKULL_ITEM) || event.getCurrentItem().getItemMeta().getDisplayName().equals("§cBack") || !event.isLeftClick())) {
                    event.setCancelled(true);
                    player.performCommand("factions accept " + event.getCurrentItem().getItemMeta().getDisplayName());
                    player.closeInventory();
                }
            } else if (view.getTitle().equals("§a§oFaction shop")) {
                List<Integer> clicked = new ArrayList<>();
                event.setCancelled(true);
                if (event.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE)) {
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§b250+ power") && clicked.size() == 0) {
                            clicked.add(clicked.size() + 1);
                            double chunkUpgradeprice;
                            if (instance.getPlayerData(player).getCurrentFactionData().getChunksPurchased() == 0) {
                                chunkUpgradeprice = 25000D;
                            } else {
                                chunkUpgradeprice = 25000D * (instance.getPlayerData(player).getCurrentFactionData().getChunksPurchased() * 2);
                            }
                            EconomyResponse r = Factions.econ.withdrawPlayer(player, chunkUpgradeprice);
                            if (r.transactionSuccess()) {
                                for (UUID uuid : instance.getPlayerData(player).getCurrentFactionData().getOnlineMembers())
                                    Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_PURCHASED_UPGRADE.getMessage().replace("%player%", player.getName()).replace("%upgrade%", "§b§l250+ chunks"));
                                instance.getPlayerData(player).getCurrentFactionData().setChunksPurchased(instance.getPlayerData(player).getCurrentFactionData().getChunksPurchased() + 1);
                                instance.getPlayerData(player).getCurrentFactionData().setPowerboost(instance.getPlayerData(player).getCurrentFactionData().getPowerboost() + 250);
                                instance.getPlayerData(player).getCurrentFactionData().updateData();
                                player.closeInventory();
                            } else {
                                player.sendMessage(Message.TRANSACTION_ERROR.getMessage());
                            }
                        return;
                    }
                    FactionUpgrades upgrade;
                    upgrade = FactionUpgrades.getUpgradeByGUIName(event.getCurrentItem().getItemMeta().getDisplayName());
                    Double price = upgrade.getPrice();
                    if (clicked.size() == 0) {
                        clicked.add(clicked.size() + 1);
                        EconomyResponse r = Factions.econ.withdrawPlayer(player, price);
                        int checkmsg = 0;
                        if (r.transactionSuccess()) {
                            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                                if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)) && !instance.getFactionManager().hasPurchasedUpgrade(instance.getPlayerData(player).getCurrentFactionData(), upgrade))
                                    Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_PURCHASED_UPGRADE.getMessage().replace("%player%", player.getName()).replace("%upgrade%", event.getCurrentItem().getItemMeta().getDisplayName()));
                            checkmsg = 1;
                            instance.getFactionManager().addUpgradeToFaction(instance.getPlayerData(player).getCurrentFactionData(), upgrade);
                            player.closeInventory();
                        } else {
                            player.sendMessage(Message.TRANSACTION_ERROR.getMessage());
                        }
                    }
                }
            }
        }

        if (view.getTitle().equals("§a§lYour faction")) {
            event.setCancelled(true);
            FactionInventory.GUIPage page = FactionInventory.GUIPage.getPageByName(event.getCurrentItem().getItemMeta().getDisplayName());
            if (page != null && page != FactionInventory.GUIPage.LIST)
                new FactionInventory(player.getUniqueId()).setItems(page).load();
            if (page == FactionInventory.GUIPage.LIST) {
                new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.LIST);
            }
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
