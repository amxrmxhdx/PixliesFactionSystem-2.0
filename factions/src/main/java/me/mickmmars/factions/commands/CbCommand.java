package me.mickmmars.factions.commands;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.perms.FactionPerms;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.util.ItemBuilder;
import me.mickmmars.factions.war.CBReason;
import me.mickmmars.factions.war.WarManager;
import me.mickmmars.factions.war.data.CasusBelli;
import me.mickmmars.factions.war.events.CBRequestEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CbCommand implements CommandExecutor {

    Factions instance = Factions.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        switch (strings.length) {
            default:
                Inventory explaininv = Bukkit.createInventory(null, 9 * 3, "§bHow to request a CB");
                for (int i = 0; i < explaininv.getSize(); i++)
                    explaininv.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
                ItemBuilder whatCbsI = new ItemBuilder(Material.MAP).setDisplayName("§b§oWhat CBs are out there?");
                for (CBReason cbs : CBReason.listCbs())
                    whatCbsI.addLoreLine("§c§o" + cbs.getName().toUpperCase());
                explaininv.setItem(11, whatCbsI.build());
                explaininv.setItem(13, new ItemBuilder(Material.ANVIL).setDisplayName("§b§oHow to request a CB?").addLoreLine("§7§o/cb §b§o<faction> §e§o<reason> §a§o<evidence(Link)>").build());
                explaininv.setItem(15, new ItemBuilder(Material.BARREL).setDisplayName("§b§oHow do i check my CBs?").addLoreLine("§7§o/cb status").build());
                player.openInventory(explaininv);
                break;
            case 1:
                if (strings[0].equalsIgnoreCase("list")) {
                    if (!instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                        return false;
                    }
                    if (new WarManager().listCBs().size() == 0) {
                        player.sendMessage(Message.NO_OPEN_CBS_ATM.getMessage());
                        return false;
                    }
                    List<String> cbSize = new ArrayList<>();
                    Inventory cbinv = Bukkit.createInventory(null, 9 * 5, "§bCBs");
                    for (CasusBelli cbobj : new WarManager().listCBs()) {
                        if (cbobj.isPending()) {
                            cbinv.addItem(new ItemBuilder(Material.CAMPFIRE).setDisplayName("§b" + cbobj.getId()).addLoreLine("§7§oRequester: §b§o" + instance.getFactionManager().getFactionById(cbobj.getAttackerId()).getName()).addLoreLine("§7§oAgainst: §b§o" + instance.getFactionManager().getFactionById(cbobj.getDefenderId()).getName()).addLoreLine("§7§oReason: §c§l§o" + cbobj.getReason()).addLoreLine(" ").addLoreLine("§7§oMouse-wheel click for accept").addLoreLine("§7§oQ for reject").addLoreLine("§7§oLeft-click for evidences").build());
                            cbSize.add(instance.generateKey(5));
                        }
                    }
                    for (int i = cbSize.size(); i < cbinv.getSize(); i++)
                        cbinv.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
                    player.openInventory(cbinv);
                } else if (strings[0].equalsIgnoreCase("status")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.CB_FACTIONS)) {
                        player.sendMessage(Message.NO_CB_PERM.getMessage());
                        return false;
                    }
                    List<String> cbSize = new ArrayList<>();
                    Inventory cbinv = Bukkit.createInventory(null, 9 * 5, "§bYour CBs");
                    for (CasusBelli cbobj : instance.getPlayerData(player).getCurrentFactionData().listCbs()) {
                            cbinv.addItem(new ItemBuilder(Material.CAMPFIRE).setDisplayName("§b" + cbobj.getId()).addLoreLine("§7§oRequester: §b§o" + instance.getFactionManager().getFactionById(cbobj.getAttackerId()).getName()).addLoreLine("§7§oAgainst: §b§o" + instance.getFactionManager().getFactionById(cbobj.getDefenderId()).getName()).addLoreLine("§7§oReason: §c§l§o" + cbobj.getReason()).addLoreLine(" ").addLoreLine("§7§oRight-click to revoke.").addLoreLine("§7§oWaiting for review: " + cbobj.isPending().toString().replace("true", "§ayes").replace("false", "§cno")).addLoreLine("§7§oRejected: " + cbobj.isRejected().toString().replace("true", "§cyes").replace("false", "§ano")).addLoreLine("§7§oAccepted: " + cbobj.isPending().toString().replace("true", "§ayes").replace("false", "§cno")).build());
                            cbSize.add(instance.generateKey(5));
                    }
                    for (int i = cbSize.size(); i < cbinv.getSize(); i++)
                        cbinv.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
                    player.openInventory(cbinv);
                }
                break;
            case 3:
                if (instance.getFactionManager().getFactionByName(strings[0]) == null) {
                    player.sendMessage(Message.FACTION_DOESNT_EXIST.getMessage());
                    return false;
                }
                if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.CB_FACTIONS)) {
                    player.sendMessage(Message.NO_CB_PERM.getMessage());
                    return false;
                }
                if (new WarManager().checkIfFactionAlreadySentCB(instance.getPlayerData(player).getCurrentFactionData(), instance.getFactionManager().getFactionByName(strings[0]))) {
                    player.sendMessage(Message.ALREADY_HAVE_A_CB.getMessage());
                    return false;
                }
                if (CBReason.getReasonByName(strings[1]) == null) {
                    player.sendMessage(Message.CB_DOESNT_EXIST.getMessage());
                    return false;
                }
                new WarManager().createCb(instance.getPlayerData(player).getCurrentFactionData(), instance.getFactionManager().getFactionByName(strings[0]), CBReason.getReasonByName(strings[1]), strings[2]);
                player.sendMessage(Message.CB_SUCCESSFULLY_SENT.getMessage());
                Bukkit.getPluginManager().callEvent(new CBRequestEvent(WarManager.latestCb, player));
                for (UUID uuid : instance.getPlayerData(player).getCurrentFactionData().listOnlineMembers())
                    Bukkit.getPlayer(uuid).sendMessage(Message.FAC_MEMBER_SENT_CB.getMessage().replace("%player%", player.getName()).replace("%faction%", strings[0]));
                instance.getFactionManager().getFactionByName(strings[0]).broadcastMessage(Message.FAC_REQUESTED_CB_AGAINST_U.getMessage().replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()).replace("%cb%", strings[1]));
                break;
        }
        return false;
    }
}
