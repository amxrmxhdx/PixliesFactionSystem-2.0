package me.mickmmars.factions.commands;


import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.chunk.location.ChunkLocation;
import me.mickmmars.factions.config.Config;
import org.dynmap.factions.DynmapFactionsPlugin;
import me.mickmmars.factions.factions.flags.FactionFlag;
import me.mickmmars.factions.factions.itemstacks.BannerData;
import me.mickmmars.factions.factions.upgrades.FactionUpgrades;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.publicwarps.data.WarpData;
import me.mickmmars.factions.util.FlickerlessScoreboard;
import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.inventory.FMapInventory;
import me.mickmmars.factions.factions.inventory.FactionInventory;
import me.mickmmars.factions.factions.perms.FactionPerms;
import me.mickmmars.factions.factions.rank.FactionRank;
import me.mickmmars.factions.util.ItemBuilder;
import me.mickmmars.factions.util.ItemStackSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.dynmap.factions.DynmapFactionsPlugin;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class FactionCommand implements CommandExecutor {

    private Economy economy;

    private Factions instance = Factions.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("That command is only executable as a player.");
            return false;
        }

        Player player = (Player) commandSender;

        if (strings.length >= 2 && (strings[0].equalsIgnoreCase("desc") || strings[0].equalsIgnoreCase("setdesc") || strings[0].equalsIgnoreCase("description"))) {
            if (!instance.getPlayerData(player).isInFaction()) {
                player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                return false;
            }
            if (FactionRank.getRankId(instance.getPlayerData(player).getFactionRank()) < 3) {

                return false;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < strings.length; i++)
                stringBuilder.append(strings[i]).append(" ");
            FactionData data = instance.getPlayerData(player).getCurrentFactionData();
            data.setDescription(stringBuilder.toString());
            instance.getFactionManager().updateFactionData(data);

            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(data))
                if (Bukkit.getPlayer(uuid) != null)
                    Bukkit.getPlayer(uuid).sendMessage(Message.CHANGED_FAC_DESCRIPTION.getMessage(player).replace("$desc$", stringBuilder.toString()));

            return false;
        }

        switch (strings.length) {

            case 1:
                if (strings[0].equalsIgnoreCase("help")) {
                    sendHelp(player, 1);
                } else if (strings[0].equalsIgnoreCase("menu")) {
                    new FactionInventory(player.getUniqueId()).setItems().load();
                } else if (strings[0].equalsIgnoreCase("list")) {
                    new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.LIST).removeBackItem().load();
                } else if (strings[0].equalsIgnoreCase("money")) {
                    player.sendMessage("§8» §7Usage: §7/f money §cbal§8, §7/f money §cdeposit §a<amount>§8, §7/f money §cwithdraw §a<amount>");
                } else if (strings[0].equalsIgnoreCase("delete") || strings[0].equalsIgnoreCase("d")) {
                    TextComponent disband = new TextComponent(Message.CONFIRM_DISBAND_HOVER.getMessage());
                    disband.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oThink about it!").create()));
                    disband.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f delete confirm"));
                    player.sendMessage(Message.CONFIRM_DISBAND.getMessage());
                    player.spigot().sendMessage(disband);
                } else if (strings[0].equalsIgnoreCase("map")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    new FMapInventory(player.getUniqueId()).setChunks().load();

                } else if (strings[0].equalsIgnoreCase("reloadconfigs")) {
                    if (player.hasPermission("factions.reload")) {
                        instance.getConfigManager().reload();
                        instance.getMessageManager().reload();
                        commandSender.sendMessage(Message.PREFIX + "§7§oConfigs §a§oMessages §7§oand §a§oconfig.yml §7§oreloaded.");
                    }
                } else if (strings[0].equalsIgnoreCase("version") || strings[0].equalsIgnoreCase("ver")) {
                    if (player.hasPermission("factions.seeversion")) {
                        player.sendMessage("§bPixlies§3Faction§fSystem §7written by §6MickMMars§7.");
                        player.sendMessage("§7§oVersion: §b" + Bukkit.getPluginManager().getPlugin("PixliesFactionSystem").getDescription().getVersion() + " §c" + Message.DEV_VERSION.getMessage());
                    }
                } else if (strings[0].equalsIgnoreCase("plugininfo")) {
                    player.sendMessage("§bPixlies§3Faction§fSystem §7written by §6MickMMars§7.");
                    player.sendMessage("§6Special thanks to§7:");
                    player.sendMessage("§8* §bTylix §7- §3Ideas and helped with the chunk-claiming");
                    player.sendMessage("§8* §bButterKing5000 §7& §bRsl1122 §7- §3Helped with the fill-claim-system");
                    player.sendMessage("§8* §bMikeprimm §7- §3His plugin Dynmap-factions was the foundation of PFS-Dynmap");
                    player.sendMessage("§8* §bBukkit Developer forums§7, §bSpigot Forums §7& §bStackoverflow §7- §3Helped me with some bugfixes");
                    player.sendMessage("§8* §bPixliesEarth staffteam §7- §3Emotional support");
                    player.sendMessage("§8* §bEnergydrinks §7- §3Helped me when i was sleepy");
                    player.sendMessage("§7§oAnd all the other people who helped me in any shape or form to develop this plugin...");
                } else if (strings[0].equalsIgnoreCase("leave")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (instance.getPlayerData(player).getFactionRank().equals(FactionRank.LEADER)) {
                        player.sendMessage(Message.CANT_LEAVE.getMessage());
                        return false;
                    }
                    player.sendMessage(Message.SUCCESSFULLY_LEFT_FACTION.getMessage().replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()));
                    instance.getChunkPlayer(player).removeFromFaction();
                } else if (strings[0].equalsIgnoreCase("info") || strings[0].equalsIgnoreCase("f")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    String factionName = instance.getPlayerData(player).getCurrentFactionData().getName();
                    FactionData factionData = instance.getFactionManager().getFactionByName(factionName);
                    StringJoiner sj = new StringJoiner("§8, ");
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(factionData)) {
                        Player member = Bukkit.getPlayer(uuid);
                        if (Bukkit.getServer().getOnlinePlayers().contains(member)) {
                            sj.add(instance.getPlayerData(member).getFactionRank().getPrefix() + member.getName());
                        }
                    }
                    int chunks = instance.getPlayerData(player).getCurrentFactionData().getChunks().size();
                    player.sendMessage("§8-§b+§8---------------------------------------------§b+§8-");
                    player.sendMessage("§7Faction: §b" + instance.getPlayerData(player).getCurrentFactionData().getName());
                    player.sendMessage("§7Land: §b" + chunks + "§7/§b" + instance.getPlayerData(player).getCurrentFactionData().getPower());
                    player.sendMessage("§7Factionbank: §2§l$§a" + instance.getPlayerData(player).getCurrentFactionData().getMoney());
                    player.sendMessage("§7Total landcost: §2§l$§a" + chunks * 5);
                    player.sendMessage("§7Online members: " + sj.toString());
                    player.sendMessage("§8-§b+§8---------------------------------------------§b+§8-");
                } else if (strings[0].equalsIgnoreCase("chest")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.factionchests.getConfiguration().contains(instance.getPlayerData(player).getFactionId())) {
                        instance.factionchests.getConfiguration().set(instance.getPlayerData(player).getFactionId(), new ArrayList<ItemStack>());
                        instance.factionchests.save();
                        instance.factionchests.reload();
                    }
                    Inventory inventory = Bukkit.createInventory(null, 9, "§aF-Chest");
                    List<ItemStack> itemstacks = (List<ItemStack>) instance.factionchests.getConfiguration().get(instance.getPlayerData(player).getFactionId());
                    for (ItemStack item : itemstacks)
                        if (item != null)
                            inventory.addItem(item);
                    player.openInventory(inventory);

                } else if (strings[0].equalsIgnoreCase("staff") || strings[0].equalsIgnoreCase("override")) {
                    if (!player.hasPermission("factions.staff")) {
                        player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                        return false;
                    }
                    if (instance.getStaffmode().contains(player.getUniqueId())) {
                        instance.getStaffmode().remove(player.getUniqueId());
                        player.sendMessage(Message.STAFFMODE_DISABLED.getMessage());
                        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    } else {
                        instance.getStaffmode().add(player.getUniqueId());
                        player.sendMessage(Message.STAFFMODE_ENABLED.getMessage());
                        FlickerlessScoreboard.Track staff = new FlickerlessScoreboard.Track("staff", "§7Staffmode: §aenabled", 0, "", "");
                        FlickerlessScoreboard fs = new FlickerlessScoreboard("§6" + player.getName(), DisplaySlot.SIDEBAR, staff);
                        player.setScoreboard(fs.getScoreboard());
                    }
                } else if (strings[0].equalsIgnoreCase("chat")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (instance.getFactionChatPlayers().contains(player.getUniqueId())) {
                        instance.getFactionChatPlayers().remove(player.getUniqueId());
                        player.sendMessage(Message.FACTION_CHAT_DISABLED.getMessage());
                        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1);
                    } else {
                        instance.getFactionChatPlayers().add(player.getUniqueId());
                        player.sendMessage(Message.FACTION_CHAT_ENABLED.getMessage());
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    }
                } else if (strings[0].equalsIgnoreCase("createsafezone")) {
                    if (player.hasPermission("factions.staff")) {
                        if (instance.getFactionManager().getFactionByName("SafeZone") == null) {
                            boolean success = true;
                            final FactionData data = new FactionData("SafeZone", "safezone", new ArrayList<String>(), new ArrayList<ChunkData>(), 999999999, "Dont worry, you are safe here.", new ArrayList<FactionPerms>(), " ", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), null, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), null, 9999, new ArrayList<String>(), new ArrayList<UUID>(), null, false);
                            try {
                                instance.getFactionManager().createNewFaction(data);
                            } catch (IOException e) {
                                success = false;
                                e.printStackTrace();
                            } finally {
                                if (success) {
                                    player.sendMessage(ChatColor.RED + "Created SafeZone.");
                                    instance.getFactionManager().setFactionFlag(instance.getFactionManager().getFactionByName("SafeZone"), new ItemStack(Material.LIME_BANNER));
                                } else
                                    player.sendMessage(Message.FACTION_CREATION_ERROR.getMessage());
                            }
                        } else {
                            player.sendMessage(Message.SAFEZONE_ALREADY_CREATED.getMessage());
                        }
                    } else {
                        player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                    }
                } else if (strings[0].equalsIgnoreCase("ally")) {
                    player.sendMessage(Message.PREFIX.getMessageRaw().toString() + "§7Usage: §7/f ally §c<faction>");
                } else if (strings[0].equalsIgnoreCase("neutral")) {
                    player.sendMessage(Message.PREFIX.getDefaultMessage() + "§7Usage: §7/f neutral §c<faction>");
                } else if (strings[0].equalsIgnoreCase("enemy")) {
                    player.sendMessage(Message.PREFIX.getDefaultMessage() + "§7Usage: §7/f enemy §c<faction>");
                } else if (strings[0].equalsIgnoreCase("sethome") || strings[0].equalsIgnoreCase("setcapital")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.EDITCAPITAL)) {
                        player.sendMessage(Message.NO_PERMISSION_TO_SETHOME.getMessage());
                        return false;
                    }
                    if (instance.getPlayerData(player).getCurrentFactionData().getCapitalLocation() != null) {
                        player.sendMessage(Message.HOME_ALREADY_EXISTS.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(player).getCurrentFactionData().getChunks().contains(instance.getChunkManager().getChunkDataByChunk(player.getLocation().getChunk()))) {
                        player.sendMessage(Message.HOME_MUST_BE_WITHIN_FAC_TERRITORY.getMessage());
                        return false;
                    }
                    instance.getFactionManager().setCapitalLocation(instance.getPlayerData(player).getCurrentFactionData(), new ChunkLocation(player.getLocation()));
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                        Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_SET_FAC_HOME.getMessage().replace("%player%", player.getName()).replace("%loc%", "§a" + player.getLocation().getBlockX() + "§7, §a" + player.getLocation().getBlockY() + "§7, §a" + player.getLocation().getBlockZ()));
                } else if (strings[0].equalsIgnoreCase("home") || strings[0].equalsIgnoreCase("capital")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (instance.getPlayerData(player).getCurrentFactionData().getCapitalLocation() == null) {
                        player.sendMessage(Message.FACTION_HAS_NO_HOME.getMessage());
                        return false;
                    }
                    instance.getTeleportingPlayers().add(player.getUniqueId());
                    player.sendMessage(Message.YOU_WILL_BE_TPD_IN_N_SECONDS.getMessage().replace("%sec%", "5"));
                    instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
                        public void run() {
                            if (instance.getTeleportingPlayers().contains(player.getUniqueId())) {
                                player.teleport(instance.getPlayerData(player).getCurrentFactionData().getCapitalLocation().toBukkitLocation());
                                player.sendMessage(Message.TELEPORTED_TO_FAC_HOME.getMessage());
                                instance.getTeleportingPlayers().remove(player.getUniqueId());
                            }
                        }
                    }, 100L);
                } else if (strings[0].equalsIgnoreCase("delhome") || strings[0].equalsIgnoreCase("delcapital")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.EDITCAPITAL)) {
                        player.sendMessage(Message.NO_PERMISSION_TO_DELETE_CAPITAL.getMessage());
                        return false;
                    }
                    if (instance.getPlayerData(player).getCurrentFactionData().getCapitalLocation() == null) {
                        player.sendMessage(Message.FACTION_HAS_NO_HOME.getMessage());
                        return false;
                    }
                    instance.getFactionManager().deleteCapital(instance.getPlayerData(player).getCurrentFactionData());
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                        Bukkit.getPlayer(uuid).sendMessage(Message.DELETED_HOME.getMessage().replace("%player%", player.getName()));
                } else if (strings[0].equalsIgnoreCase("rellist") || strings[0].equalsIgnoreCase("relationlist")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    instance.getFactionManager().createRelInv(player);
                } else if (strings[0].equalsIgnoreCase("dynmapcolour")) {
                    player.sendMessage(Message.AVAILABLE_COLOURS_DYNCOL.getMessage());
                } else if (strings[0].equalsIgnoreCase("fly")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().hasPurchasedUpgrade(instance.getPlayerData(player).getCurrentFactionData(), FactionUpgrades.FACTION_FLY) && !instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_FLY_UPGRADE.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.FACTIONFLY) && !instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_PERM_FOR_FFLY.getMessage());
                        return false;
                    }
                    if (!instance.getFactionfly().contains(player.getUniqueId())) {
                        instance.getFactionfly().add(player.getUniqueId());
                        player.sendMessage(Message.FFLY_ACTIVATED.getMessage());
                    } else {
                        instance.getFactionfly().remove(player.getUniqueId());
                        player.sendMessage(Message.FFLY_DEACTIVATED.getMessage());
                        player.setAllowFlight(false);
                        player.setFlying(false);
                    }
                } else if (strings[0].equalsIgnoreCase("setflag")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.SETFLAG)) {
                        player.sendMessage(Message.NO_SETFLAG_PERM.getMessage());
                        return false;
                    }
                    if (!player.getInventory().getItemInMainHand().getType().name().contains("BANNER")) {
                        player.sendMessage(Message.ITEM_IS_NOT_A_BANNER.getMessage());
                        return false;
                    }
                    instance.getFactionManager().setFactionFlag(instance.getPlayerData(player).getCurrentFactionData(), player.getInventory().getItemInMainHand());
                    for (UUID uuid : instance.getPlayerData(player).getCurrentFactionData().getOnlineMembers())
                      Bukkit.getPlayer(uuid).sendMessage(Message.SET_FLAG.getMessage().replace("%player%", player.getName()));
                } else if (strings[0].equalsIgnoreCase("banlist")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (instance.getPlayerData(player).getCurrentFactionData().getBannedplayer().size() == 0) {
                        player.sendMessage(Message.NO_BANNED_PLAYERS.getMessage());
                        return false;
                    }
                    Inventory inventory = Bukkit.createInventory(null, 9 * 5, "§8-= §bBanned players §8=-");
                    for (UUID uuid : instance.getPlayerData(player).getCurrentFactionData().getBannedplayer()) {
                        inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Bukkit.getOfflinePlayer(uuid).getName()).setDisplayName(Bukkit.getOfflinePlayer(uuid).getName()).build());
                    }
                    player.openInventory(inventory);
                } else if (strings[0].equalsIgnoreCase("warp")) {
                    player.sendMessage(Message.WARP_HELP.getMessage());
                } else if (strings[0].equalsIgnoreCase("bal")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    player.sendMessage(Message.BANK_BALANCE.getMessage().replace("%balance%", String.valueOf(instance.getPlayerData(player).getCurrentFactionData().getMoney())));
                } else if (strings[0].equalsIgnoreCase("flag")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.FLAGS).load();
                } else {
                    player.sendMessage(Message.CMD_DOESNT_EXIST.getMessage());
                }
                break;
            case 2:
                if (strings[0].equalsIgnoreCase("name") || strings[0].equalsIgnoreCase("setname")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.RENAME)) {
                        player.sendMessage(Message.NO_PERM_CHANGENAME.getMessage());
                        return false;
                    }
                    instance.getPlayerData(player).getCurrentFactionData().setName(strings[1]);
                    instance.getFactionManager().updateFactionData(instance.getPlayerData(player).getCurrentFactionData());
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())))
                        Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CHANGED_FACTIONNAME.getMessage().replace("%player%", player.getName()).replace("%name%", instance.getPlayerData(player).getCurrentFactionData().getName()));

                } else if (strings[0].equalsIgnoreCase("setdiscord") || strings[0].equalsIgnoreCase("discord")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.DISCORD)) {
                        player.sendMessage(Message.NO_PERM_CHANGEDISCORD.getMessage());
                        return false;
                    }
                    instance.getPlayerData(player).getCurrentFactionData().setDiscordlink(strings[1]);
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())))
                        Bukkit.getPlayer(uuid).sendMessage(Message.CHANGED_DISCORDLINK.getMessage().replace("%player%", player.getName()).replace("%link%", instance.getPlayerData(player).getCurrentFactionData().getDiscordlink()));
                } else if (strings[0].equalsIgnoreCase("create")) {
                    final String name = strings[1];
                    if (instance.getChunkPlayer(player).getPlayerData().isInFaction()) {
                        if (instance.getChunkPlayer(player).getPlayerData().getFactionRank().equals(FactionRank.LEADER))
                            player.sendMessage(Factions.col(Message.ALREADY_HAS_FAC.getMessage()));
                        else
                            player.sendMessage(Factions.col(Message.ALREADY_IN_FAC.getMessage()));
                        return false;
                    }
                    if (instance.getFactionManager().getFactionByName(name) != null) {
                        player.sendMessage(Factions.col(Message.NAME_ALREADY_TAKEN.getMessage()));
                        return false;
                    }
                    boolean success = true;
                    final FactionData data = new FactionData(name, instance.generateKey(7), new ArrayList<String>(), new ArrayList<ChunkData>(), (Integer) Config.DEFAULT_PLAYER_POWER.getData(), instance.getFactionManager().getRandomDescriptions()[new Random().nextInt(instance.getFactionManager().getRandomDescriptions().length)], new ArrayList<FactionPerms>(), " ", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), null, instance.getFactionManager().getDefaultAdminPerms(), instance.getFactionManager().getDefaultMemberPerms(), instance.getFactionManager().getDefaultNewbiePerms(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<WarpData>(),(Integer) Config.DEFAULT_BALANCE.getData(), new ArrayList<String>(), new ArrayList<UUID>(), null, false);
                    if (instance.getFactionManager().getFactionById(data.getId()) != null) {
                        player.sendMessage(Message.UNKNOWN_ERROR.getMessage());
                        return false;
                    }
                    try {
                        instance.getFactionManager().createNewFaction(data);
                    } catch (IOException e) {
                        success = false;
                        e.printStackTrace();
                    } finally {
                        if (success) {
                            player.sendMessage(Factions.col(Message.FACTION_CREATED.getMessage().replace("%faction_name%", name)));
                            instance.getChunkPlayer(player).addToFaction(data, FactionRank.LEADER);
                            instance.getFactionManager().setFactionFlag(data, new ItemStack(Material.WHITE_BANNER));
                        } else
                            player.sendMessage(Factions.col(Message.FACTION_CREATION_ERROR.getMessage()));
                    }
                } else if (strings[0].equalsIgnoreCase("invite")) {
                    if (!instance.getChunkPlayer(player).getPlayerData().isInFaction()) {
                        player.sendMessage(Factions.col(Message.NOT_IN_A_FACTION.getMessage()));
                        return false;
                    }

                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.INVITE)) {
                        player.sendMessage(Message.NO_INVITE_PERM.getMessage());
                        return false;
                    }
                    String targetName = strings[1];
                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                    if (offlineTarget == null) {
                        player.sendMessage(Message.PLAYER_NOT_FOUND.getMessage(targetName));
                        return false;
                    }

                    if (!(Config.MAX_FACTION_MEMBERS.getData().equals(-1)) && instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())).size() >= (Integer) Config.MAX_FACTION_MEMBERS.getData()) {
                        player.sendMessage(Message.MAX_MEMBER_COUNT.getMessage());
                        return false;
                    }
                    if (offlineTarget == player) {
                        player.sendMessage(Message.CANT_INVITE_YOURSELF.getMessage());
                        return false;
                    }

                    if (instance.getPlayerData((Player) offlineTarget).isInFaction()) {
                        player.sendMessage(Message.PLAYER_ALREADY_HAS_FAC.getMessage());
                        return false;
                    }

                    FactionData factionData = instance.getFactionManager().getFactionById(instance.getChunkPlayer(player).getPlayerData().getFactionId());
                    if (instance.getPlayerData(offlineTarget.getUniqueId()).getFactionInvites().contains(factionData.getId())) {
                        player.sendMessage(Factions.col(Message.PLAYER_ALREADY_INVITED.getMessage().replace("%target%", offlineTarget.getName())));
                        return false;
                    }

                    instance.getChunkPlayer(offlineTarget.getUniqueId()).addFactionInvite(factionData.getId());
                    offlineTarget.getPlayer().playSound(offlineTarget.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                    player.sendMessage(Factions.col(Message.SUCCESSFULLY_INVITED.getMessage().replace("%target%", offlineTarget.getName())));

                    if (Bukkit.getPlayer(offlineTarget.getUniqueId()) != null) {
                        Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
                        target.sendMessage(Factions.col(Message.PLAYER_GOT_INVITED.getMessage().replace("%faction%", factionData.getName())));
                        target.sendMessage("§7§oFaction Description §8» §b§o" + factionData.getDescription());
                        TextComponent accept = new TextComponent("§aAccept");
                        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a§oClick me to accept the invitation").create()));
                        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f invite accept " + factionData.getName()));
                        target.spigot().sendMessage(accept);

                        TextComponent deny = new TextComponent("§cDeny");
                        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c§oClick me to deny the invitation").create()));
                        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f invite deny " + factionData.getName()));
                        target.spigot().sendMessage(deny);
                    }
                } else if (strings[0].equalsIgnoreCase("money") || strings[0].equalsIgnoreCase("m")) {
                    if (strings[1].equalsIgnoreCase("balance") || strings[1].equalsIgnoreCase("bal")) {
                        if (!instance.getPlayerData(player).isInFaction()) {
                            player.sendMessage(Message.NOT_IN_A_FACTION.getMessage().replace("$prefix$", Message.PREFIX.getMessage()));
                            return false;
                        }
                        FactionData data = instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId());
                        int money = data.getMoney();
                        String moneyString = instance.asDecimal(money);
                        player.sendMessage(Factions.col(Message.BANK_BALANCE.getMessage().replace("%balance%", moneyString).replace("$prefix$", Message.PREFIX.getMessage())));

                    }
                } else if (strings[0].equalsIgnoreCase("kick")) {
                    if (!instance.getChunkPlayer(player).getPlayerData().isInFaction()) {
                        player.sendMessage(Factions.col(Message.NOT_IN_A_FACTION.getMessage()));
                        return false;
                    }

                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.MODERATION)) {
                        player.sendMessage(Message.NOPERM_KICK.getMessage());
                        return false;
                    }
                    String targetName = strings[1];
                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                    if (offlineTarget == null) {
                        player.sendMessage(Message.PLAYER_NOT_FOUND.getMessage().replace("$player$", strings[1]));
                        return false;
                    }
                    if (offlineTarget.getName().equals(player.getName())) {
                        player.sendMessage(Message.YOU_CANT_KICK_YOURSELF.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player.getUniqueId()).getCurrentFactionData()).contains(offlineTarget.getUniqueId())) {
                        player.sendMessage(Message.PLAYER_NOT_IN_YOUR_FACTION.getMessage().replace("$player$", strings[1]));
                        return false;
                    }
                    instance.getChunkPlayer(offlineTarget.getUniqueId()).removeFromFaction();
                    player.sendMessage(Message.YOU_KICKED_PLAYER.getMessage().replace("%player%", offlineTarget.getName()));

                    if (Bukkit.getPlayer(offlineTarget.getUniqueId()) != null)
                        Bukkit.getPlayer(offlineTarget.getUniqueId()).sendMessage(Message.PLAYER_KICKED.getMessage().replace("$player$", offlineTarget.getName()));
                } else if (strings[0].equalsIgnoreCase("delete") || strings[0].equalsIgnoreCase("delete") && strings[1].equalsIgnoreCase("confirm")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Factions.col(Message.NOT_IN_A_FACTION.getMessage()));
                        return false;
                    }
                    if (!instance.getPlayerData(player).getFactionRank().equals(FactionRank.LEADER)) {
                        player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                        return false;
                    }
                    FactionData data = instance.getPlayerData(player).getCurrentFactionData();
                    EconomyResponse r = Factions.econ.depositPlayer(player, instance.getPlayerData(player).getCurrentFactionData().getMoney());
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(data)) {
                        instance.getChunkPlayer(uuid).removeFromFaction();


                        if (Bukkit.getPlayer(uuid) != null)
                            Bukkit.getPlayer(uuid).sendMessage("§8» §c§oThe faction §b§l§o" + data.getName() + " §c§ogot disbanded!");
                    }
                    instance.getFactionManager().removeFaction(data);
                } else if (strings[0].equalsIgnoreCase("help")) {
                    try {
                        int page = Integer.parseInt(strings[1]);
                        sendHelp(player, page);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§8» §7§oPlease specify a page number. ");
                    }
                } else if (strings[0].equalsIgnoreCase("claim")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.CLAIM) && !instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_CLAIM_PERM.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkIfClaimIsConnected(player.getLocation().getChunk(), instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getId())) && Config.ALLOW_UNCONNECTED_CLAIMS.getData().equals(false) && !instance.getStaffmode().contains(player.getUniqueId()) && !strings[1].equalsIgnoreCase("fill")) {
                        player.sendMessage(Message.CLAIMS_MUST_BE_CONNECTED.getMessage());
                        return false;
                    }
                    if (strings[1].equalsIgnoreCase("one")) {
                        if (instance.getChunkManager().getFactionDataByChunk(player.getLocation().getChunk()) != null) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.ALREADY_CLAIMED.getMessage()));
                            return false;
                        }
                        if (instance.getPlayerData(player).getCurrentFactionData().getChunks().size() + 1 > instance.getPlayerData(player).getCurrentFactionData().getPower() && !instance.getStaffmode().contains(player.getUniqueId())) {
                            player.sendMessage(Message.NO_CLAIMING_POWER.getMessage());
                            return false;
                        }
                        FactionData factionData = instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId());
                        int price = (instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()).getChunks().size() >= 100 ? 5 * instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())).size() : 5);
                        if (!(factionData.getMoney() >= price)) {
                            int need = (price - instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()).getMoney());
                            String needString = instance.asDecimal(need);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NEED_MORE_TO_CLAIM.getMessage().replace("%need%", needString)));
                            return false;
                        }
                        instance.getFactionManager().claimChunk(player, player.getLocation().getChunk(), instance.getChunkPlayer(player).getPlayerData().getFactionId());
                        System.out.println("Chunk claimed at " + player.getLocation().toString() + " for player " + player.getName());
                        int x = player.getLocation().getChunk().getX();
                        int z = player.getLocation().getChunk().getZ();
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()))) {
                            if (Bukkit.getOnlinePlayers().contains(Bukkit.getOfflinePlayer(uuid))) {
                                Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CLAIMED.getMessage().replace("%player%", player.getName()).replace("%location%", x + ", " + z));
                            }
                        }
                        if (Bukkit.getServer().getPluginManager().getPlugin("PFS-Dynmap").isEnabled()) {
                            DynmapFactionsPlugin.getInstance().updateFactions();
                        }

                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col("&a&oChunk claimed")));
                    } else if (strings[1].equalsIgnoreCase("auto")) {
                        if (instance.getAutoClaim().contains(player.getUniqueId())) {
                            instance.getAutoClaim().remove(player.getUniqueId());
                            player.sendMessage(Message.AUTOCLAIM_DISABLED.getMessage());
                            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1);


                            for (Chunk chunk : instance.getAutoClaimChunks().get(player.getUniqueId())) {
                                instance.getFactionManager().claimChunk(player, chunk, instance.getPlayerData(player).getFactionId());
                            }
                            instance.getAutoClaimChunks().remove(player.getUniqueId());
                            if (Bukkit.getServer().getPluginManager().getPlugin("PFS-Dynmap").isEnabled()) {
                                DynmapFactionsPlugin.getInstance().updateFactions();
                            }
                        } else {
                            instance.getAutoClaim().add(player.getUniqueId());
                            player.sendMessage(Message.AUTOCLAIM_ENABLED.getMessage());
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }

                    } else if (strings[1].equalsIgnoreCase("safezone")) {
                        if (!instance.getStaffmode().contains(player.getUniqueId())) {
                            player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                            return false;
                        }
                        if (instance.getChunkManager().getFactionDataByChunk(player.getLocation().getChunk()) != null) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.ALREADY_CLAIMED.getMessage()));
                            return false;
                        }
                        instance.getFactionManager().claimChunk(player, player.getLocation().getChunk(), instance.getFactionManager().getFactionByName("SafeZone").getId());
                        player.sendMessage(Message.SAFEZONE_CLAIMED.getMessage().replace("%loc%", "§6" + player.getLocation().getChunk().getX() + "§7, §6" + player.getLocation().getChunk().getZ()));
                        if (Bukkit.getServer().getPluginManager().getPlugin("PFS-Dynmap").isEnabled()) {
                            DynmapFactionsPlugin.getInstance().updateFactions();
                        }
                    } else if (strings[1].equalsIgnoreCase("fill")) {
                        instance.getFillClaimPlayers().add(player.getUniqueId());
                        Set<Chunk> floodSearchRes = new HashSet<Chunk>();
                        Set<Chunk> alreadyChecked = new HashSet<Chunk>();
                        try {
                            instance.getFactionManager().floodSearch(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), floodSearchRes, alreadyChecked);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        int floodSearchResInt = floodSearchRes.size();
                        if (instance.getPlayerData(player).getCurrentFactionData().getChunks().size() + floodSearchResInt > instance.getPlayerData(player).getCurrentFactionData().getPower() && !instance.getStaffmode().contains(player.getUniqueId())) {
                            player.sendMessage(Message.NO_CLAIMING_POWER.getMessage());
                            return false;
                        }
                        FactionData factionData = instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId());
                        int price = floodSearchResInt * 5;
                        if (!(factionData.getMoney() >= price)) {
                            int need = (price - instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()).getMoney());
                            String needString = instance.asDecimal(need);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NEED_MORE_TO_CLAIM.getMessage().replace("%need%", needString)));
                            return false;
                        }
                        instance.getFactionManager().claimFill(floodSearchRes, player, factionData);
                        factionData.sendMessageToMembers(Message.PLAYER_FILL_CLAIMED_BROADCASTFAC.getMessage().replace("%player%", player.getName()).replace("%x%", Integer.toString(floodSearchResInt)));
                        instance.getFillClaimPlayers().remove(player.getUniqueId());
                        if (Bukkit.getServer().getPluginManager().getPlugin("PFS-Dynmap").isEnabled()) {
                            DynmapFactionsPlugin.getInstance().updateFactions();
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("promote")) {
                    String targetName = strings[1];
                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                    Player target = Bukkit.getPlayer(targetName);
                    if (offlineTarget == null) {
                        player.sendMessage(Message.PLAYER_NOT_FOUND.getMessage(targetName).replace("$player$", targetName));
                        return false;
                    }
                    if (FactionRank.getRankId(instance.getChunkPlayer(player).getPlayerData().getFactionRank()) < 3) {
                        player.sendMessage(Message.NO_PROMOTE_PERM.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(target).getFactionId().equals(instance.getPlayerData(player).getFactionId())) {
                        player.sendMessage(Message.PLAYER_NOT_IN_YOUR_FACTION.getMessage().replace("$player$", targetName));
                        return false;
                    }
                    instance.getFactionManager().promotePlayer(target);
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData())) {
                        if (Bukkit.getOnlinePlayers().contains(Bukkit.getOfflinePlayer(uuid))) {
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_GOT_PROMOTED.getMessage().replace("%player%", targetName).replace("%rank%", instance.getPlayerData(Bukkit.getPlayer(targetName)).getFactionRank().getName()));
                        }
                    }
                    if (instance.getPlayerData(target).getFactionRank().equals(FactionRank.ADMIN)) {
                        player.sendMessage(Message.PROMOTION_NOT_POSSIBLE.getMessage());
                        return false;
                    }

                } else if (strings[0].equalsIgnoreCase("demote")) {
                    String targetName = strings[1];
                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                    Player target = Bukkit.getPlayer(targetName);
                    if (offlineTarget == null) {
                        player.sendMessage(Message.PLAYER_NOT_FOUND.getMessage(targetName).replace("$player$", targetName));
                        return false;
                    }
                    if (FactionRank.getRankId(instance.getChunkPlayer(player).getPlayerData().getFactionRank()) < 3) {
                        player.sendMessage(Message.NO_PROMOTE_PERM.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(target).getFactionId().equals(instance.getPlayerData(player).getFactionId())) {
                        player.sendMessage(Message.PLAYER_NOT_IN_YOUR_FACTION.getMessage().replace("$player$", targetName));
                        return false;
                    }
                    instance.getFactionManager().demotePlayer(target);
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                        Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_GOT_DEMOTED.getMessage().replace("%player%", targetName).replace("%rank%", instance.getPlayerData(Bukkit.getPlayer(targetName)).getFactionRank().getName()));
                    if (instance.getPlayerData(target).getFactionRank().equals(FactionRank.LEADER)) {
                        player.sendMessage(Message.DEMOTION_NOT_POSSIBLE.getMessage());
                        return false;
                    }
                    if (instance.getPlayerData(target).getFactionRank().equals(FactionRank.NEWBIE)) {
                        player.sendMessage(Message.DEMOTION_NOT_POSSIBLE.getMessage());
                        return false;
                    }

                } else if (strings[0].equalsIgnoreCase("unclaim")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.UNCLAIM) && !instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_UNCLAIM_PERM.getMessage());
                        return false;
                    }
                    if (strings[1].equalsIgnoreCase("one")) {
                        Chunk chunk = player.getLocation().getChunk();
                        if (instance.getChunkManager().getFactionDataByChunk(chunk) == null || !instance.getChunkManager().getFactionDataByChunk(chunk).equals(instance.getPlayerData(player).getCurrentFactionData()) && !instance.getChunkManager().isFree(chunk) && !instance.getStaffmode().contains(player.getUniqueId())) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.CHUNK_IS_NOT_YOURS.getMessage()));
                            return false;
                        }
                        if (instance.getStaffmode().contains(player.getUniqueId())) {
                            instance.getFactionManager().unclaimChunk(player, player.getLocation().getChunk(), instance.getChunkManager().getFactionDataByChunk(player.getLocation().getChunk()).getId());
                            System.out.println("Chunk unclaimed at " + player.getLocation().toString() + " from faction " + instance.getChunkManager().getFactionDataByChunk(player.getLocation().getChunk()).getName());
                            int x = player.getLocation().getChunk().getX();
                            int z = player.getLocation().getChunk().getZ();
                            //Player members = (Player) instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()));
                            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getChunkManager().getFactionDataByChunk(player.getLocation().getChunk()).getId())))
                                Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_UNCLAIMED.getMessage().replace("%player%", player.getName()).replace("%location%", x + ", " + z));
                            instance.getChunkManager().getFactionDataByChunk(player.getLocation().getChunk()).setMoney(instance.getChunkManager().getFactionDataByChunk(player.getLocation().getChunk()).getMoney() + 5);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col("&a&oChunk unclaimed")));
                            return false;
                        }
                        instance.getFactionManager().unclaimChunk(player, player.getLocation().getChunk(), instance.getChunkPlayer(player).getPlayerData().getFactionId());
                        System.out.println("Chunk unclaimed at " + player.getLocation().toString() + " from faction " + instance.getPlayerData(player).getCurrentFactionData().getName());
                        int x = player.getLocation().getChunk().getX();
                        int z = player.getLocation().getChunk().getZ();
                        //Player members = (Player) instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()));
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())))
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_UNCLAIMED.getMessage().replace("%player%", player.getName()).replace("%location%", x + ", " + z));
                        int result = 5;
                        EconomyResponse r = Factions.econ.depositPlayer(player, result);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col("&a&oChunk unclaimed")));
                        if (Bukkit.getServer().getPluginManager().getPlugin("PFS-Dynmap").isEnabled()) {
                            DynmapFactionsPlugin.getInstance().updateFactions();
                        }
                    } else if (strings[1].equalsIgnoreCase("auto")) {
                        if (instance.getAutoUnclaim().contains(player.getUniqueId())) {
                            instance.getAutoUnclaim().remove(player.getUniqueId());
                            player.sendMessage(Message.AUTOUNCLAIM_DISABLED.getMessage());


                            for (Chunk chunk : instance.getAutoUnclaimChunks().get(player.getUniqueId())) {
                                instance.getFactionManager().unclaimChunk(player, chunk, instance.getPlayerData(player).getFactionId());
                            }
                            instance.getAutoUnclaimChunks().remove(player.getUniqueId());
                            if (Bukkit.getServer().getPluginManager().getPlugin("PFS-Dynmap").isEnabled()) {
                                DynmapFactionsPlugin.getInstance().updateFactions();
                            }
                        } else {
                            instance.getAutoUnclaim().add(player.getUniqueId());
                            player.sendMessage(Message.AUTOUNCLAIM_ENABLED.getMessage());
                        }

                    }
                } else if (strings[0].equalsIgnoreCase("handover")) {
                    String targetName = strings[1];
                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                    Player target = Bukkit.getPlayer(targetName);
                    if (offlineTarget == null) {
                        player.sendMessage(Message.PLAYER_NOT_FOUND.getMessage(targetName).replace("$player$", targetName));
                        return false;
                    }
                    if (FactionRank.getRankId(instance.getChunkPlayer(player).getPlayerData().getFactionRank()) < 3) {
                        player.sendMessage(Message.PLAYER_NOT_LEADER.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(target).getFactionId().equals(instance.getPlayerData(player).getFactionId())) {
                        player.sendMessage(Message.PLAYER_NOT_IN_YOUR_FACTION.getMessage().replace("$player$", targetName));
                        return false;
                    }
                    if (target == player) {
                        player.sendMessage(Message.PLAYER_ALREADY_LEADER.getMessage());
                        return false;
                    }
                    instance.getPlayerData(player).setFactionRank(FactionRank.ADMIN);
                    instance.getChunkPlayer(player).updatePlayerData(instance.getPlayerData(player));
                    instance.getPlayerData(target).setFactionRank(FactionRank.LEADER);
                    instance.getChunkPlayer(target).updatePlayerData(instance.getPlayerData(target));
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        players.sendMessage(Message.LEADERSHIP_TRANSFERED.getMessage().replace("%player%", player.getName().replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()).replace("%target%", targetName)));
                    }

                } else if (strings[0].equalsIgnoreCase("ally")) {
                    FactionData data = instance.getFactionManager().getFactionByName(strings[1]);
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (FactionRank.getRankId(instance.getPlayerData(player).getFactionRank()) < 3) {
                        player.sendMessage(Message.NO_PERMISSION_TO_ALLY.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().getFactionByName(strings[1]) == null) {
                        player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (instance.getPlayerData(player).getCurrentFactionData().getAllyRequests().contains(data.getId())) {
                        instance.getFactionManager().Ally(data.getId(), instance.getPlayerData(player).getFactionId(), data, instance.getPlayerData(player).getCurrentFactionData());
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionByName(instance.getPlayerData(player).getCurrentFactionData().getName())))
                            Bukkit.getPlayer(uuid).sendMessage(Message.FACTIONS_ALLIED.getMessage().replace("%ally%", strings[1]));
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(data))
                            Bukkit.getPlayer(uuid).sendMessage(Message.FACTIONS_ALLIED.getMessage().replace("%ally%", instance.getPlayerData(player).getCurrentFactionData().getName()));
                        return false;
                    }

                    if (data.getAllyRequests().contains(instance.getPlayerData(player).getFactionId())) {
                        player.sendMessage(Message.ALREADY_SENT_ALLYREQUEST.getMessage());
                        return false;
                    }
                    if (data.getAllies().contains(instance.getPlayerData(player).getFactionId())) {
                        player.sendMessage(Message.ALREADY_ALLIED.getMessage().replace("%ally%", strings[1]));
                        return false;
                    }
                    if (strings[1].equalsIgnoreCase(instance.getPlayerData(player).getCurrentFactionData().getName())) {
                        player.sendMessage(Message.CANT_ALLY_YOURSELF.getMessage());
                        return false;
                    }
                    if (strings[1].equalsIgnoreCase("safezone")) {
                        player.sendMessage(Message.CANT_ALLY_SAFEZONE.getMessage());
                        return false;
                    }
                    if (strings[1].equalsIgnoreCase("wilderness")) {
                        player.sendMessage(Message.CANT_ALLY_WILDERNESS.getMessage());
                        return false;
                    }
                    instance.getFactionManager().addAllyRequest(instance.getPlayerData(player).getFactionId(), data);
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionByName(instance.getPlayerData(player).getCurrentFactionData().getName())))
                        Bukkit.getPlayer(uuid).sendMessage(Message.SENT_ALLY_REQUEST.getMessage().replace("%player%", player.getName()).replace("%ally%", strings[1]));
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(data))
                        Bukkit.getPlayer(uuid).sendMessage(Message.RECIEVED_ALLY_REQUEST.getMessage().replace("%requester%", instance.getPlayerData(player).getCurrentFactionData().getName()));
                } else if (strings[0].equalsIgnoreCase("neutral")) {
                    FactionData data = instance.getFactionManager().getFactionByName(strings[1]);
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().getFactionByName(strings[1]).equals(null)) {
                        player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(player).getCurrentFactionData().getEnemies().contains(instance.getFactionManager().getFactionByName(strings[1]).getId()) && !instance.getPlayerData(player).getCurrentFactionData().getAllies().contains(instance.getFactionManager().getFactionByName(strings[1]).getId())) {
                        player.sendMessage(Message.NO_RELATION.getMessage());
                        return false;
                    }
                    if (FactionRank.getRankId(instance.getPlayerData(player).getFactionRank()) < 3) {
                        player.sendMessage(Message.NO_PERMISSION_TO_NEUTRAL.getMessage());
                        return false;
                    }
                    if (data.getAllyRequests().contains(instance.getPlayerData(player).getCurrentFactionData().getId())) {
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())))
                            Bukkit.getPlayer(uuid).sendMessage(Message.ALLY_REQUEST_REVOKED.getMessage().replace("%player%", player.getName()).replace("%faction%", strings[1]));
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(data))
                            Bukkit.getPlayer(uuid).sendMessage(Message.FACTION_REVOKED_ALLY_REQUEST.getMessage().replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()));
                        return false;
                    }
                    instance.getFactionManager().Neutralize(data.getId(), instance.getPlayerData(player).getFactionId(), data, instance.getPlayerData(player).getCurrentFactionData());
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())))
                        Bukkit.getPlayer(uuid).sendMessage(Message.FACTION_NEUTRALED.getMessage().replace("%faction%", data.getName()));
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(data))
                        Bukkit.getPlayer(uuid).sendMessage(Message.FACTION_NEUTRALED.getMessage().replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()));

                } else if (strings[0].equalsIgnoreCase("enemy")) {
                    FactionData faction1data = instance.getFactionManager().getFactionByName(strings[1]);
                    FactionData faction2data = instance.getPlayerData(player).getCurrentFactionData();
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (faction1data == faction2data) {
                        player.sendMessage(Message.CANT_ENEMY_YOURSELF.getMessage());
                        return false;
                    }
                    if (faction1data == null) {
                        player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(player).getFactionRank().equals(FactionRank.LEADER)) {
                        player.sendMessage(Message.FACTION_DOENST_ALLOW_YOU_ENEMY.getMessage());
                        return false;
                    }
                    if (faction1data.getName().equalsIgnoreCase("SafeZone")) {
                        player.sendMessage(Message.ENEMY_SAFEZONE.getMessage());
                        return false;
                    }
                    if (faction1data.getEnemies().contains(faction2data.getId())) {
                        player.sendMessage(Message.ALREADY_ENEMIED.getMessage());
                        return false;
                    }
                    instance.getFactionManager().Enemy(faction1data.getId(), faction2data.getId(), faction1data, faction2data);
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(faction1data))
                        Bukkit.getPlayer(uuid).sendMessage(Message.ENEMIED_FACTIONS.getMessage().replace("%faction%", faction2data.getName()));
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(faction2data))
                        Bukkit.getPlayer(uuid).sendMessage(Message.ENEMIED_FACTIONS.getMessage().replace("%faction%", faction1data.getName()));
                } else if (strings[0].equalsIgnoreCase("forcedisband")) {
                    if (!instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().getFactions().contains(instance.getFactionManager().getFactionByName(strings[1]))) {
                        player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (strings[1].equalsIgnoreCase("safezone")) {
                        player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                        return false;
                    }
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionByName(strings[1])))
                        instance.getChunkPlayer(uuid).removeFromFaction();
                    instance.getFactionManager().removeFaction(instance.getFactionManager().getFactionByName(strings[1]));
                    for (UUID uuid : instance.getStaffmode())
                        Bukkit.getPlayer(uuid).sendMessage(Message.FACTION_FORCEDISBANDED.getMessage().replace("%player%", player.getName()).replace("%faction%", strings[1]));
                } else if (strings[0].equalsIgnoreCase("forcejoin")) {
                    if (!instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().getFactions().contains(instance.getFactionManager().getFactionByName(strings[1]))) {
                        player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    instance.getChunkPlayer(player).addToFaction(instance.getFactionManager().getFactionByName(strings[1]), FactionRank.ADMIN);
                    player.sendMessage(Message.FORCE_JOINED_FAC.getMessage().replace("%fac%", instance.getPlayerData(player).getCurrentFactionData().getName()));
                } else if (strings[0].equalsIgnoreCase("access")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (Bukkit.getPlayer(strings[1]) == null) {
                        player.sendMessage(Message.PLAYER_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.MODERATION)) {
                        player.sendMessage(Message.NO_PERMISSION_TO_GIVE_ACCESS_TO_CHUNK.getMessage());
                        return false;
                    }
                    instance.getFactionManager().addChunkaccessToPlayer(Bukkit.getPlayer(strings[1]), player, player.getLocation().getChunk());
                } else if (strings[0].equalsIgnoreCase("ICanHasPerm")) {
                    if (instance.getFactionManager().getPermissionByName(strings[1]) == null) {
                        player.sendMessage(Message.PERMISSION_DOES_NOT_EXIST.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().checkForPlayergroupPermission(player, instance.getFactionManager().getPermissionByName(strings[1]))) {
                        player.sendMessage("§a§lTrue");
                    } else {
                        player.sendMessage("§c§lFalse");
                    }
                } else if (strings[0].equalsIgnoreCase("perms")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (strings[1].equalsIgnoreCase("list")) {
                        player.sendMessage("§8-§b+§8---------§7[ §bPermissionList §7]§8---------§b+§8-");
                        for (FactionPerms perm : instance.getFactionManager().listPerms()) {
                            player.sendMessage("§c" + perm.getName().toUpperCase());
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("apply")) {
                    if (instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.ALREADY_IN_FAC.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().getFactionByName(strings[1]).equals(null)) {
                        player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    instance.getFactionManager().addPlayerApplication(player, instance.getFactionManager().getFactionByName(strings[1]));
                    player.sendMessage(Message.APPLICATION_SUCCESSFULLY_SENT.getMessage().replace("%faction%", strings[1]));
                    if (instance.getFactionManager().getFactionByName(strings[1]).getOnlineMembers().size() >= 1) {
                        for (UUID uuid : instance.getFactionManager().getFactionByName(strings[1]).getOnlineMembers()) {
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_SENT_APPLICATION.getMessage().replace("%player%", player.getName()));
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("accept")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.INVITE)) {
                        player.sendMessage(Message.NO_INVITE_PERM.getMessage());
                        return false;
                    }
                    if (Bukkit.getPlayer(strings[1]).equals(null)) {
                        player.sendMessage(Message.PLAYER_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(strings[1]))) {
                        player.sendMessage(Message.PLAYER_NOT_FOUND.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(player).getCurrentFactionData().getApplications().contains(Bukkit.getPlayer(strings[1]).getUniqueId().toString())) {
                        player.sendMessage(Message.APPLICATION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    instance.getFactionManager().removePlayerApplication(Bukkit.getPlayer(strings[1]), instance.getPlayerData(player).getCurrentFactionData());
                    instance.getChunkPlayer(Bukkit.getPlayer(strings[1]).getUniqueId()).addToFaction(instance.getPlayerData(player).getCurrentFactionData(), FactionRank.NEWBIE);
                    if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(strings[1]))) {
                        Bukkit.getPlayer(strings[1]).sendMessage(Message.SUCCESSFULLY_JOINED_FACTION.getMessage().replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()));
                    }
                    if (instance.getFactionManager().getFactionByName(strings[1]).getOnlineMembers().size() >= 1) {
                        for (UUID uuid : instance.getFactionManager().getFactionByName(strings[1]).getOnlineMembers()) {
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_JOINED_YOUR_FACTION.getMessage().replace("%player%", strings[1]));
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("dynmapcolour") || strings[0].equalsIgnoreCase("dcol")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().hasPurchasedUpgrade(instance.getPlayerData(player).getCurrentFactionData(), FactionUpgrades.DYNMAPCOLOUR) && !instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_DYNMAP_UPGRADE.getMessage());
                        return false;
                    }
                    if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PFS-Dynmap")) {
                        player.sendMessage(Message.PREFIX.getDefaultMessage().toString() + "§7You §cdo not §7have the §bDynmap extension§7.");
                    } else {
                        if (!instance.getPlayerData(player).isInFaction()) {
                            player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                            return false;
                        }
                        if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.DYNMAPCOLOUR)) {
                            player.sendMessage(Message.NO_PERM_DYNMAPCOLOUR.getMessage());
                            return false;
                        }
                        FileConfiguration dynconfig = Bukkit.getPluginManager().getPlugin("PFS-Dynmap").getConfig();
                        if (strings[1].equalsIgnoreCase("blue")) {
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".strokeColor", "#369C9C");
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".fillColor", "#38D3D3");
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".boost", false);
                            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                                if (Bukkit.getPlayer(uuid) != null)
                                Bukkit.getPlayer(uuid).sendMessage(Message.CHANGED_DYNMAPCOLOUR.getMessage().replace("%player%", player.getName()).replace("%col%", "§b§lBlue"));
                            Bukkit.getPluginManager().getPlugin("PFS-Dynmap").saveConfig();
                            Bukkit.getPluginManager().getPlugin("PFS-Dynmap").reloadConfig();
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "dynmap reload");
                        } else if (strings[1].equalsIgnoreCase("red")) {
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".strokeColor", "#CA1919");
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".fillColor", "#F24B4B");
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".boost", false);
                            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                                if (Bukkit.getPlayer(uuid) != null)
                                Bukkit.getPlayer(uuid).sendMessage(Message.CHANGED_DYNMAPCOLOUR.getMessage().replace("%player%", player.getName()).replace("%col%", "§4§lRed"));
                            Bukkit.getPluginManager().getPlugin("PFS-Dynmap").saveConfig();
                            Bukkit.getPluginManager().getPlugin("PFS-Dynmap").reloadConfig();
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "dynmap reload");
                        } else if (strings[1].equalsIgnoreCase("orange")) {
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".strokeColor", "#E96E06");
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".fillColor", "#EA7A1C");
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".boost", false);
                            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                                if (Bukkit.getPlayer(uuid) != null)
                                Bukkit.getPlayer(uuid).sendMessage(Message.CHANGED_DYNMAPCOLOUR.getMessage().replace("%player%", player.getName()).replace("%col%", "§6§lOrange"));
                            Bukkit.getPluginManager().getPlugin("PFS-Dynmap").saveConfig();
                            Bukkit.getPluginManager().getPlugin("PFS-Dynmap").reloadConfig();
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "dynmap reload");
                        } else if (strings[1].equalsIgnoreCase("green")) {
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".strokeColor", "#45C96F");
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".fillColor", "#28F569");
                            dynconfig.set("custstyle." + instance.getPlayerData(player).getCurrentFactionData().getName() + ".boost", false);
                            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                                if (Bukkit.getPlayer(uuid) != null)
                                Bukkit.getPlayer(uuid).sendMessage(Message.CHANGED_DYNMAPCOLOUR.getMessage().replace("%player%", player.getName()).replace("%col%", "§a§lGreen"));
                            Bukkit.getPluginManager().getPlugin("PFS-Dynmap").saveConfig();
                            Bukkit.getPluginManager().getPlugin("PFS-Dynmap").reloadConfig();
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "dynmap reload");
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("join")) {
                    if (instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.ALREADY_IN_FAC.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().getFactionByName(strings[1]) == null) {
                        player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(player).getFactionInvites().contains(instance.getFactionManager().getFactionByName(strings[1]).getId()) && !instance.getFactionManager().getFactionByName(strings[1]).getAllowedFlags().contains(FactionFlag.OPEN.getName())) {
                        player.sendMessage(Message.PLAYER_WAS_NEVER_INVITED.getMessage());
                        return false;
                    }
                    instance.getChunkPlayer(player.getUniqueId()).addToFaction(instance.getFactionManager().getFactionByName(strings[1]), FactionRank.NEWBIE);
                    player.sendMessage(Message.SUCCESSFULLY_JOINED_FACTION.getMessage().replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()));
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionByName(strings[1])))
                        if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)))
                        Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_JOINED_YOUR_FACTION.getMessage().replace("%player%", player.getName()));
                } else if (strings[0].equalsIgnoreCase("setwarp")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().hasPurchasedUpgrade(instance.getPlayerData(player).getCurrentFactionData(), FactionUpgrades.ONEPUBLICWARP) && !instance.getStaffmode().contains(player.getUniqueId()) && instance.getPlayerData(player).getCurrentFactionData().getWarps().size() == 0) {
                        player.sendMessage(Message.NO_1PW_UPGRADE.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().hasPurchasedUpgrade(instance.getPlayerData(player).getCurrentFactionData(), FactionUpgrades.TWOPUBLICWARPS) && !instance.getStaffmode().contains(player.getUniqueId()) && instance.getPlayerData(player).getCurrentFactionData().getWarps().size() == 1) {
                        player.sendMessage(Message.NO_2PW_UPGRADE.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().hasPurchasedUpgrade(instance.getPlayerData(player).getCurrentFactionData(), FactionUpgrades.THREEPUBLICWARPS) && !instance.getStaffmode().contains(player.getUniqueId()) && instance.getPlayerData(player).getCurrentFactionData().getWarps().size() == 2) {
                        player.sendMessage(Message.NO_3PW_UPGRADE.getMessage());
                        return false;
                    }
                    if (instance.getPlayerData(player).getCurrentFactionData().getWarps().size() == 3 && !instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.TOO_MANY_WARPS.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.SETWARP)) {
                        player.sendMessage(Message.NO_PERM_SETWARP.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(player).getCurrentFactionData().getChunks().contains(instance.getChunkManager().getChunkDataByChunk(player.getLocation().getChunk()))) {
                        player.sendMessage(Message.WARP_MUST_BE_IN_TERRITORY.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().warpWithNameAlreadyExists(instance.getPlayerData(player).getCurrentFactionData(), strings[1])) {
                        player.sendMessage(Message.WARP_ALREADY_EXISTS.getMessage());
                        return false;
                    }
                    instance.getFactionManager().setPublicWarp(player, strings[1]);
                    for (UUID uuid : instance.getPlayerData(player).getCurrentFactionData().getOnlineMembers())
                        Bukkit.getPlayer(uuid).sendMessage(Message.WARP_SET.getMessage().replace("%player%", player.getName()).replace("%warp%", strings[1]));
                } else if (strings[0].equalsIgnoreCase("warp")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (strings[1].equalsIgnoreCase("list")) {
                        if (instance.getPlayerData(player).getCurrentFactionData().getWarps().size() == 0) {
                            player.sendMessage("§7Your faction §cdoes not §7have any warps.");
                            return false;
                        }
                        player.sendMessage("§8-§b+§8-----§7[ §bFaction warps §7]§8-----§b+§8-");
                        for (WarpData warps : instance.getPlayerData(player).getCurrentFactionData().getWarps()) {
                            TextComponent warp = new TextComponent("§b" + warps.getName());
                            warp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oClick me to teleport to §b§o" + warps.getName()).create()));
                            warp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f warp " + warps.getName()));
                            player.spigot().sendMessage(warp);
                        }
                        return false;
                    }
                    if (!instance.getFactionManager().warpWithNameAlreadyExists(instance.getPlayerData(player).getCurrentFactionData(), strings[1])) {
                        player.sendMessage(Message.WARP_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    instance.getTeleportingPlayers().add(player.getUniqueId());
                    player.sendMessage(Message.YOU_WILL_BE_TPD_IN_N_SECONDS.getMessage().replace("%sec%", "5"));
                    instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
                        public void run() {
                            if (instance.getTeleportingPlayers().contains(player.getUniqueId())) {
                                instance.getTeleportingPlayers().remove(player.getUniqueId());
                                player.teleport(instance.getFactionManager().getWarpByName(strings[1], instance.getPlayerData(player).getCurrentFactionData()).getLocation().toBukkitLocation());
                                player.sendMessage(Message.TPD_TO_WARP.getMessage().replace("%warp%", strings[1]));
                            }
                        }
                    }, 100L);
                } else if (strings[0].equalsIgnoreCase("delwarp")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().warpWithNameAlreadyExists(instance.getPlayerData(player).getCurrentFactionData(), strings[1])) {
                        player.sendMessage(Message.WARP_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.SETWARP)) {
                        player.sendMessage(Message.NO_PERM_SETWARP.getMessage());
                        return false;
                    }
                    instance.getFactionManager().removePublicWarp(player, strings[1]);
                    for (UUID uuid : instance.getPlayerData(player).getCurrentFactionData().getOnlineMembers())
                        Bukkit.getPlayer(uuid).sendMessage(Message.REMOVED_WARP.getMessage().replace("%player%", player.getName()).replace("%warp%", strings[1]));
                } else if (strings[0].equalsIgnoreCase("puppet")) {
                    FactionData data = instance.getPlayerData(player).getCurrentFactionData();
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.PUPPET)) {
                        player.sendMessage(Message.NO_PUPPET_PERM.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().getFactionByName(strings[1]) == null) {
                        player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().checkIfAlreadyPuppeted(instance.getFactionManager().getFactionByName(strings[1]))) {
                        player.sendMessage(Message.ALREADY_PUPPETED.getMessage());
                        return false;
                    }
                } else if (strings[0].equalsIgnoreCase("release")) {

                } else if (strings[0].equalsIgnoreCase("ban")) {
                    Player target = Bukkit.getPlayer(strings[1]);
                    if (target.equals(null)) {
                        player.sendMessage(Message.PLAYER_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.MODERATION)) {
                        player.sendMessage(Message.NO_BAN_PERM.getMessage());
                        return false;
                    }
                    instance.getFactionManager().banPlayerFromFac(instance.getPlayerData(player).getCurrentFactionData(), target);
                    instance.getPlayerData(player).getCurrentFactionData().broadcastMessage(Message.PLAYER_GOT_BANNED.getMessage().replace("%player%", target.getDisplayName()));
                } else if (strings[0].equalsIgnoreCase("unban")) {
                    Player target = Bukkit.getPlayer(strings[1]);
                    if (target.equals(null)) {
                        player.sendMessage(Message.PLAYER_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.MODERATION)) {
                        player.sendMessage(Message.NO_UNBAN_PERM.getMessage());
                        return false;
                    }
                    instance.getFactionManager().unBanPlayerFromFac(instance.getPlayerData(player).getCurrentFactionData(), target);
                    instance.getPlayerData(player).getCurrentFactionData().broadcastMessage(Message.PLAYER_GOT_UNBANNED.getMessage().replace("%player%", target.getDisplayName()));
                } else {
                    player.sendMessage(Message.CMD_DOESNT_EXIST.getMessage());
                }
                break;
            case 3:
                if (strings[0].equalsIgnoreCase("invite")) {
                    if (strings[1].equalsIgnoreCase("accept")) {
                        String factionName = strings[2];
                        FactionData factionData = instance.getFactionManager().getFactionByName(factionName);
                        if (factionData == null) {
                            player.sendMessage("§8» §c§oCould not find that faction.");
                            return false;
                        }
                        if (!instance.getPlayerData(player).getFactionInvites().contains(factionData.getId())) {
                            player.sendMessage("§8» §c§oYou don't have an invitation from your faction open.");
                            return false;
                        }
                        instance.getChunkPlayer(player).removeFactionInvite(factionData.getId());
                        instance.getChunkPlayer(player).addToFaction(factionData, FactionRank.NEWBIE);
                        player.sendMessage(Factions.col("&8» &a&oYou successfully joined the faction &b&l&o" + factionData.getName() + "&7&o."));

                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(factionData)) {
                            if (Bukkit.getPlayer(uuid) != null) {
                                Player member = Bukkit.getPlayer(uuid);
                                if (member != player)
                                    member.sendMessage(Factions.col("§8» §a§l§o" + player.getName() + " §7§ojoined your faction."));
                            }
                        }

                    } else if (strings[1].equalsIgnoreCase("deny")) {
                        String factionName = strings[2];
                        FactionData factionData = instance.getFactionManager().getFactionByName(factionName);
                        if (factionData == null) {
                            player.sendMessage(Factions.col("&8» &c&oCannot find this faction."));
                            return false;
                        }
                        if (!instance.getPlayerData(player).getFactionInvites().contains(factionData.getId())) {
                            player.sendMessage("§8» §c§oYou don't have an invitation from your faction open.");
                            return false;
                        }
                        instance.getChunkPlayer(player).removeFactionInvite(factionData.getId());
                        player.sendMessage(Factions.col("&8» &a&oYou successfully denied the request faction &b&l&o" + factionData.getName() + "&7&o."));

                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(factionData)) {
                            if (Bukkit.getPlayer(uuid) != null) {
                                if (instance.getPlayerData(uuid).getFactionRank().equals(FactionRank.LEADER)) {
                                    Player member = Bukkit.getPlayer(uuid);
                                    member.sendMessage(Factions.col("&8» &a&l&o" + player.getName() + " &7&odenied the invitation."));
                                }
                            }
                        }
                    } else if (strings[1].equalsIgnoreCase("revoke")) {
                        Player target = Bukkit.getPlayer(strings[2]);
                        instance.getPlayerData(target).getFactionInvites().remove(instance.getPlayerData(player).getCurrentFactionData().getId());
                        player.sendMessage(Message.INVITE_REVOKED.getMessage().replace("%player%", target.getName()));
                    }
                } else if (strings[1].equalsIgnoreCase("deposit")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    FactionData data = instance.getPlayerData(player).getCurrentFactionData();
                    int money = data.getMoney();
                    String moneyString = strings[2];
                    int result = Integer.parseInt(moneyString);
                    EconomyResponse r = Factions.econ.withdrawPlayer(player, result);
                    if (r.transactionSuccess()) {
                        commandSender.sendMessage(String.format(Message.BANK_DEPOSIT.getMessage().replace("$deposit$", strings[2]).replace("$balance$", String.valueOf(data.getMoney())), Factions.econ.format(Factions.econ.getBalance(player.getName()))));
                        data.setMoney(data.getMoney() + result);
                        instance.getFactionManager().updateFactionData(data);

                    } else {
                        commandSender.sendMessage(String.format(Message.TRANSACTION_ERROR.getMessage()));
                    }
                } else if (strings[1].equalsIgnoreCase("withdraw")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    FactionData data = instance.getPlayerData(player).getCurrentFactionData();
                    int money = data.getMoney();
                    String moneyString = strings[2];
                    int result = Integer.parseInt(moneyString);
                    EconomyResponse r = Factions.econ.depositPlayer(player, result);
                    if (instance.getPlayerData(player).getFactionRank().equals(FactionRank.LEADER)) {
                        if (data.getMoney() >= result) {
                            if (r.transactionSuccess()) {
                                data.setMoney(data.getMoney() - result);
                                commandSender.sendMessage(String.format(Message.BANK_WITHDRAW.getMessage().replace("$withdraw", strings[2]).replace("$balance$", String.valueOf(data.getMoney())), Factions.econ.format(r.amount)));
                                instance.getFactionManager().updateFactionData(data);
                            } else {
                                commandSender.sendMessage(Message.TRANSACTION_ERROR.getMessage());
                            }
                        } else {
                            commandSender.sendMessage(Message.NOT_ENOUGH_MONEY_IN_FAC.getMessage());
                        }
                    } else {
                        commandSender.sendMessage(Message.NO_PERMISSIONS.getMessage());
                    }
                } else if (strings[0].equalsIgnoreCase("warp")) {
                    if (instance.getFactionManager().getFactionByName(strings[2]) == null) {
                        player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().getWarpByName(strings[1], instance.getFactionManager().getFactionByName(strings[2])) == null) {
                        player.sendMessage(Message.WARP_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().getFactionByName(strings[2]).getBannedplayer().contains(player.getUniqueId()) && !instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.PLAYER_BANNED.getMessage());
                        return false;
                    }
                    player.sendMessage(Message.YOU_WILL_BE_TPD_IN_N_SECONDS.getMessage().replace("%sec%", "5"));
                    instance.getTeleportingPlayers().add(player.getUniqueId());
                    instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
                        public void run() {
                            if (instance.getTeleportingPlayers().contains(player.getUniqueId())) {
                                instance.getTeleportingPlayers().remove(player.getUniqueId());
                                player.teleport(instance.getFactionManager().getWarpByName(strings[1], instance.getFactionManager().getFactionByName(strings[2])).getLocation().toBukkitLocation());
                                player.sendMessage(Message.TPD_TO_WARP.getMessage().replace("%warp%", strings[1]));
                            }
                        }
                    }, 100L);
                } else if(strings[0].equalsIgnoreCase("powerboost")) {
                    if (!instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                        return false;
                    }
                    if (instance.getFactionManager().getFactionByName(strings[1]) == null) {
                        player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                        return false;
                    }
                    instance.getFactionManager().getFactionByName(strings[1]).setPowerboost(Integer.parseInt(strings[2]));
                    player.sendMessage(Message.POWERBOOSTED_FACTION.getMessage().replace("%faction%", instance.getFactionManager().getFactionByName(strings[1]).getName()).replace("%boost%", strings[2]));

                } else {
                    player.sendMessage(Message.CMD_DOESNT_EXIST.getMessage());
                }
                break;
            case 4:
                if (strings[0].equalsIgnoreCase("perms")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.EDITPERMS)) {
                        player.sendMessage(Message.NO_PERMISSION_TO_EDIT_PERMISSIONS.getMessage());
                        return false;
                    }
                    if (strings[1].equalsIgnoreCase("set")) {
                        if (!instance.getFactionManager().listPerms().contains(instance.getFactionManager().getPermissionByName(strings[2]))) {
                            player.sendMessage(Message.PERMISSION_DOES_NOT_EXIST.getMessage());
                            return false;
                        }
                        if (!(strings[3].equalsIgnoreCase("admin") || strings[3].equalsIgnoreCase("member") || strings[3].equalsIgnoreCase("newbie"))) {
                            player.sendMessage(Message.RANK_DOES_NOT_EXIST.getMessage());
                            return false;
                        }
                        if (instance.getFactionManager().checkIfRankHasPerm(player, strings[3], strings[2])) {
                            player.sendMessage(Message.ALREADY_HAS_PREMISSION.getMessage());
                            return false;
                        }
                        instance.getFactionManager().addPermToGroup(player, strings[3], strings[2]);
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_ADDED_PERMISSION_TO_GROUP_FAC.getMessage().replace("%player%", player.getName()).replace("%rank%", instance.getFactionManager().getRankByName(strings[3]).getName()).replace("%perm%", strings[2]));
                    } else if (strings[1].equalsIgnoreCase("unset")) {
                        if (!instance.getFactionManager().listPerms().contains(instance.getFactionManager().getPermissionByName(strings[2]))) {
                            player.sendMessage(Message.PERMISSION_DOES_NOT_EXIST.getMessage());
                            return false;
                        }
                        if (!(strings[3].equalsIgnoreCase("admin") || strings[3].equalsIgnoreCase("member") || strings[3].equalsIgnoreCase("newbie"))) {
                            player.sendMessage(Message.RANK_DOES_NOT_EXIST.getMessage());
                            return false;
                        }
                        if (!instance.getFactionManager().checkIfRankHasPerm(player, strings[3], strings[2])) {
                            player.sendMessage(Message.RANK_DOESNT_HAVE_PERMISSION.getMessage());
                            return false;
                        }
                        instance.getFactionManager().removePermFromGroup(player, strings[3], strings[2]);
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player).getCurrentFactionData()))
                            Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_REMOVED_PERMISSION_FROM_GROUP_FAC.getMessage().replace("%player%", player.getName()).replace("%rank%", instance.getFactionManager().getRankByName(strings[3]).getName()).replace("%perm%", strings[2]));
                    }
                } else {
                    player.sendMessage(Message.CMD_DOESNT_EXIST.getMessage());
                }
                break;
            default:
                sendHelp(player, 1);
                break;
        }

        return false;
    }


    private void sendHelp(Player player, int page) {
        switch (page) {
            case 1:
                player.sendMessage("§8-§b+§8------------§7[ §b§lPixlies§3§lFaction§f§lSystem §7]§8----------§b+§8-");
                player.sendMessage("§7§o/f create §c§o<name>");
                player.sendMessage("§7§o/f invite §c§o<player>§8§o/§c§orevoke §e§o<player>");
                player.sendMessage("§7§o/f menu");
                player.sendMessage("§7§o/f claim §c§oone§8§o/§c§oauto§8§o/§c§ofill§8§o/§c§osafezone");
                player.sendMessage("§7§o/f unclaim §c§oone§8§o/§c§oauto§8§o/§c§ofill");
                player.sendMessage("§7§o/f kick §c§o<player>");
                player.sendMessage("§7§o/f money §c§odeposit§8§o/§c§owithdraw§8§o/§c§obalance");
                player.sendMessage("§7§o/f desc | setdesc | description §c§o<Description>");
                player.sendMessage("§7§o/f delete");
                player.sendMessage("§7§o/f leave");
                player.sendMessage("§7§o/f info");
                player.sendMessage("§7§o/f map");
                player.sendMessage("§7§o/f demote §c§o<player>");
                player.sendMessage("§7§o/f promote §c§o<player>");
                player.sendMessage("§8-§b+§8------------------§7[§cPage " + page + "§8/§c4§7]§8------------------§b+§8-");
                break;
            case 2:
                player.sendMessage("§8-§b+§8------------§7[ §b§lPixlies§3§lFaction§f§lSystem §7]§8----------§b+§8-");
                player.sendMessage("§7§o/f handover §c§ohandover §c§o<player>");
                player.sendMessage("§7§o/f name§8§o/§7§osetname §c§o<name>");
                player.sendMessage("§7§o/f staff§8§o/§7§ooverride");
                player.sendMessage("§7§o/f discord§8§o/§7§osetdiscord §c§o<link>");
                player.sendMessage("§7§o/f chat");
                player.sendMessage("§7§o/f chest");
                player.sendMessage("§7§o/f ally §c§o<faction>");
                player.sendMessage("§7§o/f neutral §c§o<faction>");
                player.sendMessage("§7§o/f enemy §c§o<faction>");
                player.sendMessage("§7§o/f forcedisband §c§o<faction>");
                player.sendMessage("§7§o/f sethome§8§o/§7§osetcapital §c<faction>");
                player.sendMessage("§7§o/f home§8§o/§7§ocapital §c<faction>");
                player.sendMessage("§7§o/f delhome§8§o/§7§odelcapital §c§o<faction>");
                player.sendMessage("§7§o/f forcejoin §c§o<faction>");
                player.sendMessage("§8-§b+§8------------------§7[§cPage " + page + "§8/§c4§7]§8------------------§b+§8-");
                break;
            case 3:
                player.sendMessage("§8-§b+§8------------§7[ §b§lPixlies§3§lFaction§f§lSystem §7]§8----------§b+§8-");
                player.sendMessage("§7§o/f perms §c§oset§8§o/§c§ounset§8§o/§c§olist §e§o<permission> §e§o<rank>");
                player.sendMessage("§7§o/f fly");
                player.sendMessage("§7§o/f dynmapcolour §c<colour>");
                player.sendMessage("§7§o/f join §c<faction>");
                player.sendMessage("§7§o/f plugininfo");
                player.sendMessage("§7§o/f ban §c§o<player>");
                player.sendMessage("§7§o/f unban §c§o<player>");
                player.sendMessage("§7§o/f banlist");
                player.sendMessage("§7§o/f setwarp §c§o<name>");
                player.sendMessage("§7§o/f warp §c§o<name> §e§o<factionname> §8/ §7§o/f warp list");
                player.sendMessage("§7§o/f setwarp §c§o<name>");
                player.sendMessage("§7§o/f delwarp §c§o<name>");
                player.sendMessage("§7§o/f setflag");
                player.sendMessage("§7§o/f puppet §c§o<faction>");
                player.sendMessage("§8-§b+§8------------------§7[§cPage " + page + "§8/§c4§7]§8------------------§b+§8-");
                break;
            case 4:
                player.sendMessage("§8-§b+§8------------§7[ §b§lPixlies§3§lFaction§f§lSystem §7]§8----------§b+§8-");
                player.sendMessage("§7§o/f rellist");
                player.sendMessage("§8-§b+§8------------------§7[§cPage " + page + "§8/§c4§7]§8------------------§b+§8-");
            default:
                player.sendMessage("§c§othis page doesn't exists!");
                break;
        }
    }
}
