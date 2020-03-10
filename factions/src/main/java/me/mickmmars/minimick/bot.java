package me.mickmmars.minimick;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.war.WarManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;

public class bot {

    public static DiscordApi api;
    public FileConfiguration discordconf = Factions.getInstance().discordconf.getConfiguration();
    private Factions instance = Factions.getInstance();

    public void startBot() {
        String token = discordconf.getString("bot-token");

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
                        discordconf.set("cbnotify-channel", event.getChannel().getId());
                        Factions.getInstance().discordconf.save();
                        Factions.getInstance().discordconf.reload();
                        event.getChannel().sendMessage("Changed CB-notify channel.");
                    }
                } else if (event.getMessageContent().equalsIgnoreCase("$setwarchannel")) {
                    if (event.getMessageAuthor().isServerAdmin()) {
                        discordconf.set("notification-channel", event.getChannel().getId());
                        Factions.getInstance().discordconf.save();
                        Factions.getInstance().discordconf.reload();
                        event.getChannel().sendMessage("Changed warchannel.");
                    }
                }
                TextChannel cbnotify = api.getTextChannelById(discordconf.getLong("cbnotify-channel")).get();
                if (msgsplit[0].equalsIgnoreCase("$cbaccept") && event.getChannel().equals(cbnotify)) {
                    if (new WarManager().exists(new WarManager().getCbById(msgsplit[1]))) {
                        new WarManager().acceptCb(new WarManager().getCbById(msgsplit[1]));
                        event.getChannel().sendMessage("I accepted the CB with the Id **" + msgsplit[1] + "**");
                        instance.getFactionManager().getFactionById(new WarManager().getCbById(msgsplit[1]).getAttackerId()).sendMessageToMembers(Message.CB_ACCEPTED_FAC.getMessage().replace("%fac%", instance.getFactionManager().getFactionById(new WarManager().getCbById(msgsplit[1]).getDefenderId()).getName()).replace("%id%", new WarManager().getCbById(msgsplit[1]).getId()));
                    } else {
                        event.getChannel().sendMessage("This CB does not exist.");
                    }
                } else if (msgsplit[0].equalsIgnoreCase("$cbdeny") && event.getChannel().equals(cbnotify)) {
                    if (new WarManager().exists(new WarManager().getCbById(msgsplit[1]))) {
                        new WarManager().rejectCb(new WarManager().getCbById(msgsplit[1]));
                        event.getChannel().sendMessage("I rejected the CB with the Id **" + msgsplit[1] + "**");
                        instance.getFactionManager().getFactionById(new WarManager().getCbById(msgsplit[1]).getAttackerId()).sendMessageToMembers(Message.CB_DENIED_FAC.getMessage().replace("%fac%", instance.getFactionManager().getFactionById(new WarManager().getCbById(msgsplit[1]).getDefenderId()).getName()).replace("%id%", new WarManager().getCbById(msgsplit[1]).getId()));
                    } else {
                        event.getChannel().sendMessage("This CB does not exist.");
                    }
                }
            }
        });

    }
}
