package me.mickmmars.factions.commands;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConstructTabCompleter implements TabCompleter {

    Factions instance = Factions.getInstance();

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("factions") && strings.length == 1 || command.getName().equalsIgnoreCase("f") && strings.length == 1 ) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;

                List<String> list = new ArrayList<>();
                list.add("create");
                list.add("invite");
                list.add("menu");
                list.add("kick");
                list.add("desc");
                list.add("description");
                list.add("setdesc");
                list.add("delete");
                list.add("help");
                list.add("leave");
                list.add("sethome");
                list.add("home");
                list.add("delhome");
                list.add("unclaim");
                list.add("claim");
                list.add("promote");
                list.add("demote");
                list.add("discord");
                list.add("forcedisband");
                list.add("staff");
                list.add("override");
                list.add("perms");
                list.add("capital");
                list.add("delcapital");
                list.add("setcapital");
                list.add("ally");
                list.add("neutral");
                list.add("apply");
                list.add("accept");
                list.add("enemy");
                return list;
            }
        }
        if (command.getName().equalsIgnoreCase("factions") && strings[0].equalsIgnoreCase("create") && strings.length == 2) {
            List<String> namelist = new ArrayList<>();
            namelist.add("§c<name>");
        }
        if (command.getName().equalsIgnoreCase("factions") && strings[0].equalsIgnoreCase("ally") || command.getName().equalsIgnoreCase("factions") && strings[0].equalsIgnoreCase("enemy") || command.getName().equalsIgnoreCase("factions") && strings[0].equalsIgnoreCase("neutral") || command.getName().equalsIgnoreCase("factions") && strings[0].equalsIgnoreCase("home") || command.getName().equalsIgnoreCase("factions") && strings[0].equalsIgnoreCase("delhome") || command.getName().equalsIgnoreCase("factions") && strings[0].equalsIgnoreCase("sethome") || command.getName().equalsIgnoreCase("factions") && strings[0].equalsIgnoreCase("apply")) {
            List<String> facnames = new ArrayList<>();
            for (FactionData factions : instance.getFactionManager().getFactions())
                facnames.add(factions.getName());
            return facnames;
        }
        return null;
    }
}
