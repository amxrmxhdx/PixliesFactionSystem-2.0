package me.mickmmars.factions.commands;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.war.data.CasusBelli;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TreatyCommand implements CommandExecutor {

    private Factions instance = Factions.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        switch (strings.length) {
            case 2:
            if (strings[0].equalsIgnoreCase("finished")) {
                if (!instance.getStaffmode().contains(player.getUniqueId())) {
                    player.sendMessage(Message.NO_PERMISSIONS.getMessage());
                    return false;
                }
                if (CasusBelli.getCbById(strings[1]) == null) {
                    player.sendMessage(Message.CB_DOESNT_EXIST.getMessage());
                    return false;
                }
                instance.getWarFactions().get(instance.getPlayerData(player).getCurrentFactionData()).announceWarPlayers(Message.TREATY_TIME_ENDED.getMessage());
                instance.getWarFactions().get(instance.getPlayerData(player).getCurrentFactionData()).end();
            }
            break;
        }

        return false;
    }


}
