package me.mickmmars.minimick;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.config.ConfigManager;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.war.data.CasusBelli;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;

public class bot {

    public static DiscordApi api;
    private Factions instance = Factions.getInstance();

    public void startBot() {
        String token = Config.BOT_TOKEN.getData().toString();

        if (!token.equals("TOKEN_HERE")) {
            api = new DiscordApiBuilder().setToken(token).login().join();
            api.updateActivity("Powered by PixliesEarth!");
        } else {
            System.out.println(ChatColor.RED + "Invalid bot token.");
        }
        api.addMessageCreateListener(event -> {
            if (event.getMessageAuthor().isServerAdmin()) {
                String[] msgsplit = event.getMessageContent().split(" ");
                if (event.getMessageContent().equalsIgnoreCase("$setcbchannel")) {
                    if (event.getMessageAuthor().isServerAdmin()) {
                        Config.CB_NOTIFICATION_CHANNEL.setData(event.getChannel().getIdAsString());
                        instance.getConfigManager().reload();
                        event.getChannel().sendMessage("Changed CB-notify channel.");
                    }
                } else if (event.getMessageContent().equalsIgnoreCase("$setwarchannel")) {
                    if (event.getMessageAuthor().isServerAdmin()) {
                        Config.WAR_NOTIFICATION_CHANNEL.setData(event.getChannel().getIdAsString());
                        instance.getConfigManager().reload();
                        event.getChannel().sendMessage("Changed warchannel.");
                    }
                }
                TextChannel cbnotify = api.getTextChannelById(Config.CB_NOTIFICATION_CHANNEL.getData().toString()).get();
                if (msgsplit[0].equalsIgnoreCase("$cbaccept") && event.getChannel().equals(cbnotify)) {
                    if (CasusBelli.exists(CasusBelli.getCbById(msgsplit[1]))) {
                        CasusBelli.acceptCb(CasusBelli.getCbById(msgsplit[1]));
                        event.getChannel().sendMessage("I accepted the CB with the Id **" + msgsplit[1] + "**");
                        instance.getFactionManager().getFactionById(CasusBelli.getCbById(msgsplit[1]).getAttackerId()).sendMessageToMembers(Message.CB_ACCEPTED_FAC.getMessage().replace("%fac%", instance.getFactionManager().getFactionById(CasusBelli.getCbById(msgsplit[1]).getDefenderId()).getName()).replace("%id%", CasusBelli.getCbById(msgsplit[1]).getId()));
                    } else {
                        event.getChannel().sendMessage("This CB does not exist.");
                    }
                } else if (msgsplit[0].equalsIgnoreCase("$cbdeny") && event.getChannel().equals(cbnotify)) {
                    if (CasusBelli.exists(CasusBelli.getCbById(msgsplit[1]))) {
                        CasusBelli.rejectCb(CasusBelli.getCbById(msgsplit[1]));
                        event.getChannel().sendMessage("I rejected the CB with the Id **" + msgsplit[1] + "**");
                        instance.getFactionManager().getFactionById(CasusBelli.getCbById(msgsplit[1]).getAttackerId()).sendMessageToMembers(Message.CB_DENIED_FAC.getMessage().replace("%fac%", instance.getFactionManager().getFactionById(CasusBelli.getCbById(msgsplit[1]).getDefenderId()).getName()).replace("%id%", CasusBelli.getCbById(msgsplit[1]).getId()));
                    } else {
                        event.getChannel().sendMessage("This CB does not exist.");
                    }
                }
            }
        });

    }
}
