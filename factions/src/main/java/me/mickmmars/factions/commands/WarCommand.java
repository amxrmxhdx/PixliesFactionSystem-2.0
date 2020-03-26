package me.mickmmars.factions.commands;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.rank.FactionRank;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.util.ItemBuilder;
import me.mickmmars.factions.war.data.CasusBelli;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class WarCommand implements CommandExecutor {

    private Factions instance = Factions.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only executable by a player.");
        }
        Player player = (Player) commandSender;

        switch (strings.length) {
            case 0:
                if (!instance.getPlayerData(player).isInFaction()) {
                    player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                    return false;
                }
                if (instance.getPlayerData(player).getCurrentFactionData().listCbs().size() == 0) {
                    player.sendMessage(Message.NO_APPROVED_CBS.getMessage());
                    return false;
                }
                if (!instance.getPlayerData(player).getFactionRank().equals(FactionRank.LEADER)) {
                    player.sendMessage(Message.PLAYER_NOT_LEADER.getMessage());
                    return false;
                }
                if (instance.getPlayerData(player).getCurrentFactionData().isInWar()) {
                    player.sendMessage(Message.ALREADY_IN_WAR.getMessage());
                    return false;
                }
                List<String> cbSize = new ArrayList<>();
                Inventory cbinv = Bukkit.createInventory(null, 9 * 5, "§cWhich CB do you want to use?");
                for (CasusBelli cbobj : instance.getPlayerData(player).getCurrentFactionData().listCbs()) {
                    if (cbobj.getAccepted()) {
                        cbinv.addItem(new ItemBuilder(Material.HAY_BLOCK).setDisplayName("§b" + cbobj.getId()).addLoreLine("§7§oRequester: §b§o" + instance.getFactionManager().getFactionById(cbobj.getAttackerId()).getName()).addLoreLine("§7§oAgainst: §b§o" + instance.getFactionManager().getFactionById(cbobj.getDefenderId()).getName()).addLoreLine("§7§oReason: §c§l§o" + cbobj.getReason()).addLoreLine(" ").addLoreLine("§7§oWaiting for review: " + cbobj.isPending().toString().replace("true", "§ayes").replace("false", "§cno")).addLoreLine("§7§oRejected: " + cbobj.isRejected().toString().replace("true", "§cyes").replace("false", "§ano")).addLoreLine("§7§oAccepted: " + cbobj.isPending().toString().replace("true", "§ayes").replace("false", "§cno")).addLoreLine("§7§oLeft-click to accept").build());
                        cbSize.add(instance.generateKey(5));
                    }
                }
                for (int i = cbSize.size(); i < cbinv.getSize(); i++)
                    cbinv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setNoName().build());
                player.openInventory(cbinv);
                break;
            case 1:
                if (strings[0].equalsIgnoreCase("surrender")) {
                    if (!instance.getPlayerData(player).isInFaction()) {
                        player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(player).getFactionRank().equals(FactionRank.LEADER)) {
                        player.sendMessage(Message.PLAYER_NOT_LEADER.getMessage());
                        return false;
                    }
                    if (!instance.getPlayerData(player).getCurrentFactionData().isInWar()) {
                        player.sendMessage(Message.NOT_IN_A_WAR.getMessage());
                        return false;
                    }
                    Bukkit.broadcastMessage(Message.FACTION_SURRENDERED.getMessage().replace("%loser%", instance.getPlayerData(player).getCurrentFactionData().getName()).replace("%winner%", instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getOpposingFactionId()).getName()));
                    instance.getWarFactions().get(instance.getPlayerData(player).getCurrentFactionData()).end();
                }
                break;
        }

        return false;
    }
}
