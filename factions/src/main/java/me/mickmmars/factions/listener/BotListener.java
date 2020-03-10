package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.war.data.CasusBelli;
import me.mickmmars.factions.war.events.*;
import me.mickmmars.minimick.bot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class BotListener implements Listener {

    private FileConfiguration discordconf = Factions.getInstance().discordconf.getConfiguration();
    private DiscordApi api = bot.api;

    @EventHandler
    public void onWarStart(WarStartEvent event) {
        if (discordconf.getString("notification-channel").equals("CHANNEL_HERE")) {
            System.out.println(ChatColor.RED + "Please enter the name of the war-notification channel in the config.");
            return;
        }
        TextChannel channel = null;
        channel = api.getTextChannelById(discordconf.getLong("notification-channel")).get();
        channel.sendMessage(new EmbedBuilder()
                .setAuthor("PixliesFactionSystem", null, api.getYourself().getAvatar().getUrl().toString())
                .setTitle("A war between " + event.getAttacker().getName() + " and " + event.getDefender().getName() + " has started.")
                .setDescription(event.getAttacker().getName() + " is attacking " + event.getDefender().getName() + " with the CB  `" + event.getCB().getReason() + "`.")
                .setColor(Color.RED)
                .setFooter("PixliesFactionSystem made by MickMMars"));
    }

    @EventHandler
    public void onWarEnd(WarEndEvent event) {
        if (discordconf.getString("notification-channel").equals("CHANNEL_HERE")) {
            System.out.println(ChatColor.RED + "Please enter the name of the war-notification channel in the config.");
            return;
        }
        TextChannel channel = null;
        channel = api.getTextChannelById(discordconf.getLong("notification-channel")).get();
        channel.sendMessage(new EmbedBuilder()
                .setAuthor("PixliesFactionSystem", null, api.getYourself().getAvatar().getUrl().toString())
                .setTitle(event.getWinner().getName() + " won the war against " + event.getLoser().getName())
                .setDescription("The war has ended.")
                .addField("Winner kills", "" + Factions.getInstance().getWarKills().get(event.getWinner()))
                .addField("Loser kills", "" + Factions.getInstance().getWarKills().get(event.getLoser()))
                .setColor(Color.GREEN)
                .setFooter("PixliesFactionSystem made by MickMMars"));
        Bukkit.broadcastMessage(me.mickmmars.factions.message.Message.WAR_WON.getMessage().replace("%winner%", event.getWinner().getName()).replace("%loser%", event.getLoser().getName()));
    }

    @EventHandler
    public void onPlayerCapping(PlayerCappingEvent event) {
        if (discordconf.getString("notification-channel").equals("CHANNEL_HERE")) {
            System.out.println(ChatColor.RED + "Please enter the name of the war-notification channel in the config.");
            return;
        }
        TextChannel channel = null;
        channel = api.getTextChannelById(discordconf.getLong("notification-channel")).get();
        channel.sendMessage(new EmbedBuilder()
                .setAuthor(event.getCapper().getName(), null, "https://minotar.net/avatar/" + event.getCapper().getName())
                .setTitle("Player " + event.getCapper().getName() + " is occupying the capital of " + event.getNationGettingCapped().getName())
                .setDescription("He has to stand there for 10 minutes to win the war!")
                .setColor(Color.CYAN)
                .setFooter("PixliesFactionSystem made by MickMMars"));
    }

    @EventHandler
    public void onPlayerFailCapping(PlayerCappingFailedEvent event) {
        if (discordconf.getString("notification-channel").equals("CHANNEL_HERE")) {
            System.out.println(ChatColor.RED + "Please enter the name of the war-notification channel in the config.");
            return;
        }
        TextChannel channel = null;
        channel = api.getTextChannelById(discordconf.getLong("notification-channel")).get();
        channel.sendMessage(new EmbedBuilder()
                .setAuthor(event.getCapper().getName(), null, "https://minotar.net/avatar/" + event.getCapper().getName())
                .setTitle("Player " + event.getCapper().getName() + " failed to occupy the capital of " + event.getNationGettingCapped().getName())
                .setDescription("He could not survive 10 minutes there!")
                .setColor(Color.CYAN)
                .setFooter("PixliesFactionSystem made by MickMMars"));
    }

    @EventHandler
    public void onCBRequest(CBRequestEvent event) {
        if (discordconf.getString("cbnotify-channel").equals("CHANNEL_HERE")) {
            System.out.println(ChatColor.RED + "Please enter the name of the cbnotify-channel channel in the config.");
            return;
        }
        TextChannel channel = null;
        channel = api.getTextChannelById(discordconf.getLong("cbnotify-channel")).get();
        EmbedBuilder embed = new EmbedBuilder().setAuthor(event.getSender().getName(), null, "https://minotar.net/avatar/" + event.getSender().getName())
                .setTitle("**" + Factions.getInstance().getFactionManager().getFactionById(event.getCB().getAttackerId()).getName() + "** requested a CB against **" + Factions.getInstance().getFactionManager().getFactionById(event.getCB().getDefenderId()).getName() + "**")
                .setDescription("CB-reason: " + event.getCB().getReason().toUpperCase())
                .addField("ID", event.getCB().getId())
                .setColor(Color.RED)
                .setFooter("PixliesFactionSystem made by MickMMars");
        for (String s : event.getCB().getEvidence())
            embed.addField("Evidence", s);
        channel.sendMessage(embed);
    }

    @EventHandler
    public void onCBDelete(CBDeletationEvent event) {
        if (discordconf.getString("cbnotify-channel").equals("CHANNEL_HERE")) {
            System.out.println(ChatColor.RED + "Please enter the name of the cbnotify-channel channel in the config.");
            return;
        }
        TextChannel channel = null;
        channel = api.getTextChannelById(discordconf.getLong("cbnotify-channel")).get();
        EmbedBuilder embed = new EmbedBuilder().setAuthor(bot.api.getYourself().getName(), null, bot.api.getYourself().getAvatar().getUrl().toString())
                .setTitle("The CB with the Id `" + event.getCB().getId() + "` got deleted.")
                .addField("Attacker", Factions.getInstance().getFactionManager().getFactionById(event.getCB().getAttackerId()).getName())
                .addField("Defender", Factions.getInstance().getFactionManager().getFactionById(event.getCB().getDefenderId()).getName())
                .addField("Reason", event.getCB().getReason())
                .setColor(Color.RED)
                .setFooter("PixliesFactionSystem made by MickMMars");
        channel.sendMessage(embed);
    }

    @EventHandler
    public void onStaffNeed(StaffNeededEvent event) {
        if (discordconf.getString("cbnotify-channel").equals("CHANNEL_HERE")) {
            System.out.println(ChatColor.RED + "Please enter the name of the cbnotify-channel channel in the config.");
            return;
        }
        TextChannel channel = null;
        channel = api.getTextChannelById(discordconf.getLong("cbnotify-channel")).get();
        EmbedBuilder embed = new EmbedBuilder().setAuthor(bot.api.getYourself().getName(), null, bot.api.getYourself().getAvatar().getUrl().toString())
                .setTitle("Staff is needed for a war with the CB `" + event.getCb().getId() + "`.")
                .setDescription("Join so the war can start.")
                .setColor(Color.RED)
                .setFooter("PixliesFactionSystem made by MickMMars");
        channel.sendMessage(embed);
    }

}
