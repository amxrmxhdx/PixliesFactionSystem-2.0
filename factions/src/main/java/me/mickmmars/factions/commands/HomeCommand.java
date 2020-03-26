package me.mickmmars.factions.commands;

import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.home.data.HomeData;
import me.mickmmars.factions.Factions;
import me.mickmmars.factions.message.Message;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HomeCommand implements CommandExecutor {

    private Factions instance = Factions.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("That command is only executable as a player.");
            return false;
        }

        Player player = (Player) commandSender;

        int maxHomes = (player.hasPermission("earth.vip") ? (int) Config.MAX_HOMES_VIP.getData() : (int) Config.MAX_HOMES_USER.getData());

        switch (strings.length) {
            case 1:
                    if (strings[0].equalsIgnoreCase("list")) {
                        if (instance.getChunkPlayer(player).getHomeObject().getHomes().isEmpty()) {
                            player.sendMessage(Message.NO_HOMES.getMessage());
                            player.sendMessage(Message.HOMES_LEFT.getMessage().replace("%left%", String.valueOf(maxHomes - instance.getChunkPlayer(player).getHomeObject().getHomes().size())));
                            return false;
                        }
                        player.sendMessage(Message.YOUR_HOMES.getMessage().replace("%size%", String.valueOf(instance.getChunkPlayer(player).getHomeObject().getHomes().size())));
                        for (HomeData homeData : instance.getChunkPlayer(player).getHomeObject().getHomes()) {
                            final TextComponent textComponent = new TextComponent(Message.HOME_LIST_FORMAT.getMessage().replace("%home%", homeData.getName()));
                            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Message.HOME_CLICK_TO_TELEPORT.getMessage().replace("%home%", homeData.getName())).create()));
                            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home tp " + homeData.getName()));
                            player.spigot().sendMessage(textComponent);
                        }
                        player.sendMessage(Message.HOMES_LEFT.getMessage().replace("%left%", String.valueOf(maxHomes - instance.getChunkPlayer(player).getHomeObject().getHomes().size())));
                        player.sendMessage(" ");
                        return false;
                    }
                    final String home = strings[0];
                    if (!instance.getChunkPlayer(player).getHomeObject().existsHome(home)) {
                        player.sendMessage(Message.HOME_DOES_NOT_EXISTS.getMessage());
                        return false;
                    }
                    instance.getTeleportingPlayers().add(player.getUniqueId());
                    player.sendMessage(Message.YOU_WILL_BE_TPD_IN_N_SECONDS.getMessage().replace("%sec%", "5"));
                    instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
                        public void run() {
                            if (instance.getTeleportingPlayers().contains(player.getUniqueId())) {
                                instance.getTeleportingPlayers().remove(player.getUniqueId());
                                player.teleport(instance.getChunkPlayer(player).getHomeObject().getHomeDataByName(home).getLocation().toBukkitLocation());
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 2);
                            }
                        }
                    }, 100L);
                    break;
            case 2:
                final String home1 = strings[1];
                if (strings[0].equalsIgnoreCase("set")) {
                    if (!instance.getChunkPlayer(player).getHomeObject().getHomes().isEmpty() && instance.getChunkPlayer(player).getHomeObject().getHomes().size() == maxHomes) {
                        player.sendMessage(Message.HOME_MAX.getMessage(maxHomes));
                        return false;
                    }
                    if (instance.getChunkPlayer(player).getHomeObject().existsHome(home1)) {
                        player.sendMessage(Message.HOME_ALREADY_EXISTS.getMessage(home1));
                        return false;
                    }
                    instance.getChunkPlayer(player).getHomeObject().addHome(home1, player.getLocation());
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                    player.sendMessage(Message.HOME_SET_SUCCESSFULLY.getMessage().replace("%home%", home1));
                } else if (strings[0].equalsIgnoreCase("delete")) {
                    if (!instance.getChunkPlayer(player).getHomeObject().existsHome(home1)) {
                        player.sendMessage(Message.HOME_DOES_NOT_EXISTS.getMessage());
                        return false;
                    }
                    instance.getChunkPlayer(player).getHomeObject().removeHome(home1);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                    player.sendMessage(Message.HOME_REMOVE_SUCCESSFULLY.getMessage().replace("%home%", home1));
                } else if (strings[0].equalsIgnoreCase("teleport") || strings[0].equalsIgnoreCase("tp")) {
                    if (!instance.getChunkPlayer(player).getHomeObject().existsHome(home1)) {
                        player.sendMessage(Message.HOME_DOES_NOT_EXISTS.getMessage());
                        return false;
                    }
                    instance.getTeleportingPlayers().add(player.getUniqueId());
                    player.sendMessage(Message.YOU_WILL_BE_TPD_IN_N_SECONDS.getMessage().replace("%sec%", "5"));
                    instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
                        public void run() {
                            if (instance.getTeleportingPlayers().contains(player.getUniqueId())) {
                                instance.getTeleportingPlayers().remove(player.getUniqueId());
                                player.teleport(instance.getChunkPlayer(player).getHomeObject().getHomeDataByName(strings[1]).getLocation().toBukkitLocation());
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 2);
                            }
                        }
                    }, 100L);
                } else {
                    player.sendMessage(Message.HOME_HELP_PAGES.getMessage());
                }
                break;
            default:
                player.sendMessage(Message.HOME_HELP_PAGES.getMessage());

        }

        return false;
    }
}
