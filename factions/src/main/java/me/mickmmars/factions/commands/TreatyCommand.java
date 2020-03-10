package me.mickmmars.factions.commands;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.war.WarManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TreatyCommand implements CommandExecutor {

    private Factions instance = Factions.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        if (!instance.getStaffmode().contains(player.getUniqueId())) {
            player.sendMessage(Message.NO_PERMISSIONS.getMessage());
            return false;
        }
        if (new WarManager().getCbById(strings[1]) == null) {
            player.sendMessage(Message.CB_DOESNT_EXIST.getMessage());
            return false;
        }
        if (strings[0].equalsIgnoreCase("finished")) {
            new WarManager().announceWarPlayers(new WarManager().getCbById(strings[1]), Message.TREATY_TIME_ENDED.getMessage());
            new WarManager().endWar(new WarManager().getCbById(strings[1]), instance.getFactionManager().getFactionById(strings[2]));
        }

        return false;
    }


}
