package me.mickmmars.minimick.listeners;

import me.mickmmars.minimick.Main;
import me.mickmmars.minimick.utils.googleSearch;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] msg = event.getMessage().getContentDisplay().split(" ");

        if (msg[0].equalsIgnoreCase(".m")) {
            // HELP
            if (msg[1].equalsIgnoreCase("help")) {
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                        .setTitle("MiniMick!")
                        .setDescription("**The i-can-do-everything-bitch-bot**\n" +
                                "```md\n" +
                                "1. .m search <keyword>\n" +
                                "2. .m rps <rock/paper/scissors>\n" +
                                "```")
                        .setColor(Color.CYAN)
                        .build()
                );
            }

            // SEARCH
            if (msg[1].equalsIgnoreCase("search")) {
                if (msg[2] == null) {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", I can't search for nothing ^^");
                    return;
                }
                if (msg[2].length() > 15) {
                    event.getChannel().sendMessage("I can't search for keywords longer than **15** characters.");
                    return;
                }
                try {
                    googleSearch.sendSearch((TextChannel) event.getChannel(), msg[2]);
                } catch (IOException e) {
                    event.getChannel().sendMessage("**Something went wrong with your request.**");
                    e.printStackTrace();
                }
            }

            // RPS
            if (msg[1].equalsIgnoreCase("rps")) {
                if (msg[2] == null) {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", you have to either choose rock, paper or scissors!");
                    return;
                }
                if (!(msg[2].equalsIgnoreCase("rock") || msg[2].equalsIgnoreCase("paper") || msg[2].equalsIgnoreCase("scissors"))) {
                    event.getChannel().sendMessage("You can only choose either rock, paper or scissors.");
                    return;
                }
                Random r = new Random();
                int result = r.nextInt(3 - 1) + 1;
                String resultEm = "";
                if (result == 1) resultEm = ":newspaper:";
                if (result == 2) resultEm = ":scissors:";
                if (result == 3) resultEm = ":black_circle:";

                // IF RESULT == PAPER
                if (result == 1 && msg[2].equalsIgnoreCase("paper"))
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("It's a tie! :newspaper: | :newspaper:")
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setColor(Color.YELLOW)
                            .setAuthor("MiniMick", null, Main.jda.getSelfUser().getAvatarUrl()).build());
                if (result == 1 && msg[2].equalsIgnoreCase("rock"))
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("You lost " + event.getAuthor().getName() + "! :black_circle: | :newspaper:")
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setColor(Color.RED)
                            .setAuthor("MiniMick", null, Main.jda.getSelfUser().getAvatarUrl()).build());
                if (result == 1 && msg[2].equalsIgnoreCase("scissors"))
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("You won " + event.getAuthor().getName() + "! :scissors: | :newspaper:")
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setColor(Color.GREEN)
                            .setAuthor("MiniMick", null, Main.jda.getSelfUser().getAvatarUrl()).build());

                // IF RESULT == SCISSORS
                if (result == 2 && msg[2].equalsIgnoreCase("paper"))
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("You lost " + event.getAuthor().getName() + "! :paper: | :scissors:")
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setColor(Color.RED)
                            .setAuthor("MiniMick", null, Main.jda.getSelfUser().getAvatarUrl()).build());
                if (result == 2 && msg[2].equalsIgnoreCase("rock"))
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("You won " + event.getAuthor().getName() + "! :black_circle: | :scissors:")
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setColor(Color.GREEN)
                            .setAuthor("MiniMick", null, Main.jda.getSelfUser().getAvatarUrl()).build());
                if (result == 2 && msg[2].equalsIgnoreCase("scissors"))
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("It's a tie! :scissors: | :scissors:")
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setColor(Color.YELLOW)
                            .setAuthor("MiniMick", null, Main.jda.getSelfUser().getAvatarUrl()).build());

                // IF RESULT == ROCK
                if (result == 3 && msg[2].equalsIgnoreCase("paper"))
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("You won " + event.getAuthor().getName() + "! :paper: | :black_circle:")
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setColor(Color.GREEN)
                            .setAuthor("MiniMick", null, Main.jda.getSelfUser().getAvatarUrl()).build());
                if (result == 3 && msg[2].equalsIgnoreCase("rock"))
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("It's a tie! :black_circle: | :black_circle:")
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setColor(Color.YELLOW)
                            .setAuthor("MiniMick", null, Main.jda.getSelfUser().getAvatarUrl()).build());
                if (result == 3 && msg[2].equalsIgnoreCase("scissors"))
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("You lost " + event.getAuthor().getName() + "! :scissors: | :black_circle:")
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setColor(Color.RED)
                            .setAuthor("MiniMick", null, Main.jda.getSelfUser().getAvatarUrl()).build());
            }
        }

        //

    }

}
