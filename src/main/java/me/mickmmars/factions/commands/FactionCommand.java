package me.mickmmars.factions.commands;


import me.mickmmars.factions.Factions;
import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.flags.FactionFlag;
import me.mickmmars.factions.factions.inventory.FactionInventory;
import me.mickmmars.factions.factions.rank.FactionRank;
import me.mickmmars.factions.home.data.HomeData;
import me.mickmmars.factions.message.Message;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class FactionCommand implements CommandExecutor {

    private static Economy econ = null;

    private Factions instance = Factions.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("That command is only executable as a player.");
            return false;
        }

        Player player = (Player) commandSender;

        int maxHomes = (player.hasPermission("factions.vip") ? (int) Config.MAX_HOMES_VIP.getData() : (int) Config.MAX_HOMES_USER.getData());

        if (strings.length >= 2 && (strings[0].equalsIgnoreCase("desc") || strings[0].equalsIgnoreCase("setdesc") || strings[0].equalsIgnoreCase("description"))) {
            if (!instance.getPlayerData(player).isInFaction()) {
                player.sendMessage("§8» §c§oYou are not in a faction! §e§o/f create <name> || /f join");
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
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Factions.col("&8» &c&oYou are not in a faction &e&o/f create"));
                        return false;
                    }
                    new FactionInventory(player.getUniqueId()).setItems().load();
                } else if (strings[0].equalsIgnoreCase("list")) {
                    new FactionInventory(player.getUniqueId()).setItems(FactionInventory.GUIPage.LIST).removeBackItem().load();
                } else if (strings[0].equalsIgnoreCase("money")) {
                    player.sendMessage("§8» §7§oCorrect usage: §e§o/f money bal§8§o, §e§o/f money deposit <amount>§8§o, §e§o/f money withdraw <amount>");
                } else if (strings[0].equalsIgnoreCase("delete") || strings[0].equalsIgnoreCase("d")) {
                    player.sendMessage("§8» §c§oAre you sure you want to delete that faction? All data will be gone forever (That's a long time!) §e§o/f delete confirm");
                }
                break;
            case 2:
                if (strings[0].equalsIgnoreCase("create") || strings[0].equalsIgnoreCase("c")) {
                    final String name = strings[1];
                    if (instance.getChunkPlayer(player).getPlayerData().isInFaction()) {
                        if (instance.getChunkPlayer(player).getPlayerData().getFactionRank().equals(FactionRank.LEADER))
                            player.sendMessage(Factions.col("&8» &c&oYou already have a Faction."));
                        else
                            player.sendMessage(Factions.col("&8» &7&oYou have to leave your current faction to join a new one. &c&o/f leave"));
                        return false;
                    }
                    if (instance.getFactionManager().getFactionByName(name) != null) {
                        player.sendMessage(Factions.col("&8» &c&oYou can't use that name because it's already taken."));
                        return false;
                    }
                    boolean success = true;
                    final FactionData data = new FactionData(name, instance.generateKey(7), new ArrayList<FactionFlag>(), new ArrayList<ChunkData>(), "", instance.getFactionManager().getRandomDescriptions()[new Random().nextInt(instance.getFactionManager().getRandomDescriptions().length)], 0);
                    if (instance.getFactionManager().getFactionById(data.getId()) != null) {
                        player.sendMessage("ein fehler ist aufgetreten, bitte versuche es erneut");
                        return false;
                    }
                    try {
                        instance.getFactionManager().createNewFaction(data);
                    } catch (IOException e) {
                        success = false;
                        e.printStackTrace();
                    } finally {
                        if (success) {
                            player.sendMessage(Factions.col("&8» &7&oYou successfully created &b&o" + name + "&7&o!"));
                            instance.getChunkPlayer(player).addToFaction(data, FactionRank.LEADER);
                        } else
                            player.sendMessage(Factions.col("&8» &c&oError while creating new faction."));
                    }
                } else if (strings[0].equalsIgnoreCase("invite")) {
                    if (!instance.getChunkPlayer(player).getPlayerData().isInFaction()) {
                        player.sendMessage(Factions.col("&8» &c&oYou are not in a faction &e&o/f create"));
                        return false;
                    }

                    if (FactionRank.getRankId(instance.getChunkPlayer(player).getPlayerData().getFactionRank()) < 3) {
                        player.sendMessage(Factions.col("&8» &c&oYour faction does not allow you to invite members."));
                        return false;
                    }
                    String targetName = strings[1];
                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                    if (offlineTarget == null) {
                        player.sendMessage(Message.PLAYER_NOT_FOUND.getMessage(targetName));
                        return false;
                    }

                    FactionData factionData = instance.getFactionManager().getFactionById(instance.getChunkPlayer(player).getPlayerData().getFactionId());
                    if (instance.getPlayerData(offlineTarget.getUniqueId()).getFactionInvites().contains(factionData.getId())) {
                        player.sendMessage(Factions.col("&8» &c&oThis player has already been invited to your faction. &e&o/f invite revoke (Player)"));
                        return false;
                    }

                    instance.getChunkPlayer(offlineTarget.getUniqueId()).addFactionInvite(factionData.getId());
                    player.sendMessage(Factions.col("&8» &7&oYou successfully invited &a&l&o" + offlineTarget.getName() + " &7&oto your faction!"));

                    if (Bukkit.getPlayer(offlineTarget.getUniqueId()) != null) {
                        Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
                        target.sendMessage(Factions.col("&8» &b&l&o" + factionData.getName() + " &7&oinvited you to join their faction."));
                        target.sendMessage("§7§oFaction Description §8» §c§o" + factionData.getDescription());
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
                            player.sendMessage("§8» §c§oYou are not in a faction! §e§o/f create <name> || /f join");
                            return false;
                        }
                        FactionData data = instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId());
                        int money = data.getMoney();
                        String moneyString = instance.asDecimal(money);
                        player.sendMessage(Factions.col("§8» §7§oYour faction has §a§l§o" + moneyString + "$ §7§oin their bank."));

                    }
                } else if (strings[0].equalsIgnoreCase("kick")) {
                    if (!instance.getChunkPlayer(player).getPlayerData().isInFaction()) {
                        player.sendMessage(Factions.col("&8» &c&oYou are not in a faction &e&o/f create"));
                        return false;
                    }

                    if (FactionRank.getRankId(instance.getChunkPlayer(player).getPlayerData().getFactionRank()) < 3) {
                        player.sendMessage(Factions.col("&8» &c&oYour faction does not allow you to invite members."));
                        return false;
                    }
                    String targetName = strings[1];
                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                    if (offlineTarget == null) {
                        player.sendMessage(Factions.col("&8» &c&oThis player has never been on the server."));
                        return false;
                    }
                    if (!instance.getFactionManager().getMembersFromFaction(instance.getPlayerData(player.getUniqueId()).getCurrentFactionData()).contains(offlineTarget.getUniqueId())) {
                        player.sendMessage("§8» §c§oThis player is not in your faction.");
                        return false;
                    }
                    instance.getChunkPlayer(offlineTarget.getUniqueId()).removeFromFaction();
                    player.sendMessage("§8» §7§oYou kicked §c§l§o" + offlineTarget.getName() + " §7§ofrom your faction.");

                    if (Bukkit.getPlayer(offlineTarget.getUniqueId()) != null)
                        Bukkit.getPlayer(offlineTarget.getUniqueId()).sendMessage("§8» §c§oYou got kicked from §b§l§o" + instance.getPlayerData(player).getCurrentFactionData().getName() + "§c§o!");
                } else if (strings[0].equalsIgnoreCase("delete") || strings[0].equalsIgnoreCase("delete") && strings[1].equalsIgnoreCase("confirm")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Factions.col("&8» &c&oYou are not in a faction &e&o/f create"));
                        return false;
                    }
                    if (!instance.getPlayerData(player).getFactionRank().equals(FactionRank.LEADER)) {
                        player.sendMessage("§8» §c§oInsufficient permissions!");
                        return false;
                    }
                    FactionData data = instance.getPlayerData(player).getCurrentFactionData();
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
                } else if (strings[0].equalsIgnoreCase("home")) {
                    if (strings[1].equalsIgnoreCase("list")) {
                        if (instance.getChunkPlayer(player).getHomeObject().getHomes().isEmpty()) {
                            player.sendMessage(Message.NO_HOMES.getMessage());
                            player.sendMessage(Message.HOMES_LEFT.getMessage((maxHomes - instance.getChunkPlayer(player).getHomeObject().getHomes().size())));
                            return false;
                        }
                        player.sendMessage(Message.YOUR_HOMES.getMessage(new DecimalFormat().format(instance.getChunkPlayer(player).getHomeObject().getHomes().size()).replace(",", ".")));
                        for (HomeData homeData : instance.getChunkPlayer(player).getHomeObject().getHomes()) {
                            final TextComponent textComponent = new TextComponent(Message.HOME_LIST_FORMAT.getMessage(homeData.getName()));
                            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Message.HOME_CLICK_TO_TELEPORT.getMessage(homeData.getName())).create()));
                            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home tp " + homeData.getName()));
                            player.spigot().sendMessage(textComponent);
                        }
                        player.sendMessage(Message.HOMES_LEFT.getMessage((maxHomes - instance.getChunkPlayer(player).getHomeObject().getHomes().size())));
                        player.sendMessage(" ");
                        return false;
                    }
                    final String home = strings[0];
                    if (!instance.getChunkPlayer(player).getHomeObject().existsHome(home)) {
                        player.sendMessage(Message.HOME_DOES_NOT_EXISTS.getMessage());
                        return false;
                    }
                    player.teleport(instance.getChunkPlayer(player).getHomeObject().getHomeDataByName(home).getLocation().toBukkitLocation());
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 2);
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
                            player.sendMessage(Factions.col("&8» &c&oYou have no invitation open by that faction."));
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



                    }
                } else if (strings[1].equalsIgnoreCase("deposit")) {
                if (!instance.getPlayerData(player).isInFaction()) {
                    player.sendMessage("§8» §c§oYou are not in a faction! §e§o/f create <name> || /f join");
                    return false;
                }
                FactionData data = instance.getPlayerData(player).getCurrentFactionData();
                int money = data.getMoney();
                String moneyString = strings[2];
                int result = Integer.parseInt(moneyString);
                EconomyResponse r = econ.withdrawPlayer(player, result);
                if (r.transactionSuccess()) {
                    commandSender.sendMessage(String.format(Message.BANK_DEPOSIT.getMessage().replace("$deposit$", strings[2]).replace("$balance$", String.valueOf(data.getMoney())), econ.format(econ.getBalance(player.getName()))));
                    data.setMoney(data.getMoney() + result);
                    instance.getFactionManager().updateFactionData(data);
                } else {
                    commandSender.sendMessage(String.format(Message.TRANSACTION_ERROR.getMessage()));
                }
            }else if (strings[1].equalsIgnoreCase("withdraw")) {
                if (!instance.getPlayerData(player).isInFaction()) {
                    player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                    return false;
                }
                FactionData data = instance.getPlayerData(player).getCurrentFactionData();
                int money = data.getMoney();
                String moneyString = strings[2];
                int result = Integer.parseInt(moneyString);
                EconomyResponse r = econ.depositPlayer(player, result);
                if (instance.getPlayerData(player).getFactionRank().equals(FactionRank.LEADER)) {
                    if (data.getMoney() >= result) {
                        if (r.transactionSuccess()) {
                            commandSender.sendMessage(String.format(Message.BANK_WITHDRAW.getMessage().replace("$withdraw", strings[2]).replace("$balance$", String.valueOf(data.getMoney())), econ.format(r.amount)));
                        } else {
                            commandSender.sendMessage(Message.TRANSACTION_ERROR.getMessage());
                        }
                    }else {
                        commandSender.sendMessage(Message.NOT_ENOUGH_MONEY_IN_FAC.getMessage());
                    }
                }else {
                    commandSender.sendMessage(Message.NO_PERMISSIONS.getMessage());
                }
            } else if (strings[0].equalsIgnoreCase("home")) {
                    final String home = strings[2];
                    if (strings[1].equalsIgnoreCase("set")) {
                        if (!instance.getChunkPlayer(player).getHomeObject().getHomes().isEmpty() && instance.getChunkPlayer(player).getHomeObject().getHomes().size() == maxHomes) {
                            player.sendMessage(Message.HOME_MAX.getMessage(maxHomes));
                            return false;
                        }
                        if (instance.getChunkPlayer(player).getHomeObject().existsHome(home)) {
                            player.sendMessage(Message.HOME_ALREADY_EXISTS.getMessage(home));
                            return false;
                        }
                        instance.getChunkPlayer(player).getHomeObject().addHome(home, player.getLocation());
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                        player.sendMessage(Message.HOME_SET_SUCCESSFULLY.getMessage(home));
                    } else if (strings[1].equalsIgnoreCase("delete")) {
                        if (!instance.getChunkPlayer(player).getHomeObject().existsHome(home)) {
                            player.sendMessage(Message.HOME_DOES_NOT_EXISTS.getMessage());
                            return false;
                        }
                        instance.getChunkPlayer(player).getHomeObject().removeHome(home);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                        player.sendMessage(Message.HOME_REMOVE_SUCCESSFULLY.getMessage(home));
                    } else if (strings[1].equalsIgnoreCase("teleport") || strings[1].equalsIgnoreCase("tp")) {
                        if (!instance.getChunkPlayer(player).getHomeObject().existsHome(home)) {
                            player.sendMessage(Message.HOME_DOES_NOT_EXISTS.getMessage());
                            return false;
                        }
                        player.teleport(instance.getChunkPlayer(player).getHomeObject().getHomeDataByName(home).getLocation().toBukkitLocation());
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 2);
                    } else if (strings[1].equalsIgnoreCase("help")) {
                        try {
                            final int page = Integer.parseInt(strings[1]);
                            sendHelp(player, page);
                        } catch (NumberFormatException e) {
                            player.sendMessage(Message.NOT_A_NUMBER.getMessage());
                        }
                    }
                }
                break;
            case 4:
                break;
            default:
                sendHelp(player, 1);
                break;
        }

        return false;
    }


    private void sendHelp(Player player, int page) {
        player.sendMessage("§b§lPixliesFactionSystem §7help §8[§cPage: " + page + "§8]");
        switch (page) {
            case 1:
                player.sendMessage("§e§o/f create §c§o<name>");
                player.sendMessage("§e§o/f invite §c§o<player>");
                player.sendMessage("§e§o/f menu");
                player.sendMessage("§e§o/f kick §c§o<player>");
                break;
            case 2:
                player.sendMessage("§e§o/f desc | setdesc | description §c§o<Description>");
                player.sendMessage("§e§o/f delete");
                break;
            default:
                player.sendMessage("§c§othis page doesn't exists!");
                break;
        }
    }
}
