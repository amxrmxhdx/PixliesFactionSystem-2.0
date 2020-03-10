package me.mickmmars.factions.commands;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConstructTabCompleter implements TabCompleter {

    Factions instance = Factions.getInstance();

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        String[] factionNeedingCmds = {"ally", "enemy", "neutral", "apply"};
        if (command.getName().equalsIgnoreCase("factions") && strings.length == 1 || command.getName().equalsIgnoreCase("f") && strings.length == 1 ) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;

                List<String> all = Arrays.asList("forcedisband", "handover", "passport", "create", "invite", "menu", "kick", "desc", "description", "delete", "setdesc", "help", "leave", "sethome", "home", "delhome", "unclaim", "claim", "promote", "demote", "discord", "forcedisband", "staff", "override", "perms", "capital", "home", "setcapital", "sethome", "apply", "accept", "enemy", "ally", "rank", "chest", "join", "setflag", "top", "puppet", "release");

                List<String> list = new ArrayList<>();
                for (String string : all) {
                    if (string.startsWith(strings[0])) {
                        list.add(string);
                    }
                }
                return list;
            }
        }
        if (command.getName().equalsIgnoreCase("factions") && strings[0].equalsIgnoreCase("create") && strings.length == 2) {
            List<String> namelist = new ArrayList<>();
            namelist.add("§c<name>");
            return namelist;
        }
        for (String sss : factionNeedingCmds)
            if (command.getName().equalsIgnoreCase("factions") && sss.startsWith(strings[0])) {
                List<String> facnames = new ArrayList<>();
                for (FactionData factions : instance.getFactionManager().getFactions())
                    facnames.add("§b" + factions.getName());
                return facnames;
            }


        return null;
    }
}
