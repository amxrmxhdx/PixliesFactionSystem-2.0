package me.mickmmars.minimick;


import java.awt.Color;

import me.mickmmars.minimick.Utils.ConfigReader;
import me.mickmmars.minimick.Utils.TextColor;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.server.ServerJoinListener;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import java.io.*;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Main {

    public static DiscordApi api;
    public static File config;
    public static boolean invalidToken = false;
    public static boolean running = false;

    public static void main(String[] args) {
        running = true;
        config = new File("config.cfg");
        if (!config.exists()) {
            try {
                System.out.println("Config does not exist.");
                config.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(config));
                writer.write("token=TOKEN_HERE");
                writer.close();
                System.out.println("Config file created.");
                File serverDir = new File("Servers");
                serverDir.mkdir();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ConfigReader.getString(config, "token").equals("TOKEN_HERE"))
            invalidToken = true;
        while (invalidToken) {
            if (ConfigReader.getString(config, "token").equals("TOKEN_HERE")) {
                System.out.println("Please enter your bot token.");
                Scanner scanner = new Scanner(System.in);
                ConfigReader.set(config, "token", scanner.nextLine());
                System.out.println();
            } else {
                invalidToken = false;
            }
        }
        String token = ConfigReader.getString(config, "token");
        api = new DiscordApiBuilder().setToken(token).login().join();

        api.updateActivity(ActivityType.LISTENING, "your heart <3");

        System.out.println("Servers using MiniMick");
        for (Server servers : api.getServers())
            System.out.println(TextColor.Cyan() + "L " + TextColor.Reset() + servers.getName());

        api.addServerJoinListener(new ServerJoinListener() {
            @Override
            public void onServerJoin(ServerJoinEvent event) {
                File serverCfg = new File("Servers/" + event.getServer().getId() + ".cfg");
                if (!serverCfg.exists()) {
                    ConfigReader.createDefaultServerConfig(event.getServer());
                }
                System.out.println("Server config created.");
            }
        });

        api.addMessageCreateListener(new MessageCreateListener() {
            @Override
            public void onMessageCreate(MessageCreateEvent event) {
                File serverCfg = new File("Servers/" + event.getServer().get().getId() + ".cfg");
                String[] splitted = event.getMessageContent().split(" ");
                if (splitted[0].equalsIgnoreCase("meek!")) {
                    if (splitted[1].equalsIgnoreCase("setJoinMsg")) {
                        if (!event.getMessageAuthor().isServerAdmin()) return;
                        if (splitted[2].equalsIgnoreCase("true")) {
                            ConfigReader.set(serverCfg, "join-msg", "true");
                            event.getChannel().sendMessage("**successfully** set the setting `join-msg` to `true`.");
                        } else if (splitted[2].equalsIgnoreCase("false")) {
                            ConfigReader.set(serverCfg, "join-msg", "false");
                            event.getChannel().sendMessage("**successfully** set the setting `join-msg` to `false`.");
                        }
                    }
                    if (splitted[1].equalsIgnoreCase("setJoinChannel")) {
                        if (!event.getMessageAuthor().isServerAdmin()) return;
                        ConfigReader.set(serverCfg, "join-channel", event.getChannel().getIdAsString());
                        event.getChannel().sendMessage("**successfully** set the Join-message channel to this channel.");
                    }
                    if (splitted[1].equalsIgnoreCase("serverinfo")) {
                        List<User> bots = new ArrayList<>();
                        for (User users : event.getServer().get().getMembers())
                            if (users.isBot())
                                bots.add(users);
                        event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle(event.getServer().get().getName())
                        .setDescription("Server information")
                        .addField("All members", String.valueOf(event.getServer().get().getMemberCount()), true)
                        .addField("Real members", String.valueOf(event.getServer().get().getMemberCount() - bots.size()), true)
                        .addField("Bots", String.valueOf(bots.size()), true)
                        .addField("Created at", DateTimeFormatter.ofPattern("yyyy/MM/dd").format(event.getServer().get().getCreationTimestamp().atZone(ZoneId.systemDefault())))
                        .addField("Owner", event.getServer().get().getOwner().getName())
                        .addField("Roles", String.valueOf(event.getServer().get().getRoles().size()))
                        .setFooter("MiniMick | Powered by PixliesEarth!™")
                        .setColor(Color.CYAN));
                    }
                    if (splitted[1].equalsIgnoreCase("invite")) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(Color.CYAN)
                        .setTitle("Invite MiniMick to your server!")
                        .setDescription("https://discordapp.com/api/oauth2/authorize?client_id=605416814511521800&permissions=0&scope=bot")
                        .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString())
                        .setFooter("MiniMick | Powered by PixliesEarth!™"));
                    }
                    if (splitted[1].equalsIgnoreCase("rps")) {
                        if (splitted[2] == null) {
                            event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("You have to provide a valid item: Rock / Paper / Scissors")
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setColor(Color.CYAN)
                            .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));
                            return;
                        }
                        if (!(splitted[2].equalsIgnoreCase("rock") || splitted[2].equalsIgnoreCase("paper") || splitted[2].equalsIgnoreCase("scissors"))) {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("You have to provide a valid item: Rock / Paper / Scissors")
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setColor(Color.CYAN)
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));
                            return;
                        }
                        Random r = new Random();
                        int result = r.nextInt(3 - 1) + 1;
                        String resultEm = "";
                        if (result == 1) resultEm = ":newspaper:";
                        if (result == 2) resultEm = ":scissors:";
                        if (result == 3) resultEm = ":black_circle:";

                        // IF RESULT == PAPER
                        if (result == 1 && splitted[2].equalsIgnoreCase("paper"))
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("It's a tie! :newspaper: | :newspaper:")
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setColor(Color.YELLOW)
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));
                        if (result == 1 && splitted[2].equalsIgnoreCase("rock"))
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("You lost " + event.getMessageAuthor().getDisplayName() + "! :black_circle: | :newspaper:")
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setColor(Color.RED)
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));
                        if (result == 1 && splitted[2].equalsIgnoreCase("scissors"))
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("You won " + event.getMessageAuthor().getDisplayName() + "! :scissors: | :newspaper:")
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setColor(Color.GREEN)
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));

                        // IF RESULT == SCISSORS
                        if (result == 2 && splitted[2].equalsIgnoreCase("paper"))
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("You lost " + event.getMessageAuthor().getDisplayName() + "! :paper: | :scissors:")
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setColor(Color.RED)
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));
                        if (result == 2 && splitted[2].equalsIgnoreCase("rock"))
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("You won " + event.getMessageAuthor().getDisplayName() + "! :black_circle: | :scissors:")
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setColor(Color.GREEN)
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));
                        if (result == 2 && splitted[2].equalsIgnoreCase("scissors"))
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("It's a tie! :scissors: | :scissors:")
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setColor(Color.YELLOW)
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));

                        // IF RESULT == ROCK
                        if (result == 3 && splitted[2].equalsIgnoreCase("paper"))
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("You won " + event.getMessageAuthor().getDisplayName() + "! :paper: | :black_circle:")
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setColor(Color.GREEN)
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));
                        if (result == 3 && splitted[2].equalsIgnoreCase("rock"))
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("It's a tie! :black_circle: | :black_circle:")
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setColor(Color.YELLOW)
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));
                        if (result == 3 && splitted[2].equalsIgnoreCase("scissors"))
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("You lost " + event.getMessageAuthor().getDisplayName() + "! :scissors: | :black_circle:")
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setColor(Color.RED)
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString()));
                    }
                    if (splitted[1].equalsIgnoreCase("fu")) {
                        if (!event.getMessageAuthor().isServerAdmin()) {
                            return;
                        }
                        event.getMessage().delete();
                        event.getChannel().sendMessage("Frick you " + splitted[2] + ".");
                    }
                    if (splitted[1].equalsIgnoreCase("balance")) {
                        DecimalFormat df = new DecimalFormat("####0.0");
                        File plcfg = new File("Users/" + event.getMessageAuthor().getIdAsString() + ".cfg");
                        if (!plcfg.exists())
                            ConfigReader.createDefaultUserConfig(event.getMessageAuthor());
                        event.getChannel().sendMessage(new EmbedBuilder()
                        .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString())
                        .setFooter("MiniMick | Powered by PixliesEarth!™")
                        .setColor(Color.GREEN)
                        .setTitle("Your balance is: " + df.format(ConfigReader.getDouble(plcfg, "balance")) + " MeekCoins")
                        .setDescription(event.getMessageAuthor().getDisplayName()));
                    }
                    if (splitted[1].equalsIgnoreCase("work")) {
                        Random r = new Random();
                        DecimalFormat df = new DecimalFormat("####0.0");
                        File plcfg = new File("Users/" + event.getMessageAuthor().getIdAsString() + ".cfg");
                        if (!plcfg.exists())
                            ConfigReader.createDefaultUserConfig(event.getMessageAuthor());
                        int result = r.nextInt(16 - 1) + 1;
                        if (result == 3 || result == 6 || result == 7) {
                            Double balance = ConfigReader.getDouble(plcfg, "balance");
                            ConfigReader.set(new File("Users/" + event.getMessageAuthor().getIdAsString() + ".cfg"), "balance",  balance + 0.3 * result);
                            event.getChannel().sendMessage(new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setFooter("MiniMick | Powered by PixliesEarth!™")
                            .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString())
                            .setTitle("You just earned " + df.format(0.3 * result) + " MeekCoin(s)!")
                            .setDescription("Hard work pays out my dude."));
                        } else if (result == 2 || result == 15) {
                            Double balance = ConfigReader.getDouble(new File("Users/" + event.getMessageAuthor().getIdAsString() + ".cfg"), "balance");
                            ConfigReader.set(new File("Users/" + event.getMessageAuthor().getIdAsString() + ".cfg"), "balance",  balance - 0.2 * result);
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString())
                                    .setTitle("Uh oh, you just got robbed!")
                                    .setDescription("You lost " + 0.2 * result));
                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setFooter("MiniMick | Powered by PixliesEarth!™")
                                    .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString())
                                    .setTitle("Looks like today's not your lucky day fella")
                                    .setDescription("You didn't earn anything today. Go back to the gulag."));
                        }
                    }
                    if (splitted[1].equalsIgnoreCase("help")) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(Color.CYAN)
                        .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString())
                        .setTitle("MiniMick help")
                        .setDescription("```md\n" +
                                "1. meek! balance - shows your balance\n" +
                                "2. meek! work - work or get robbed\n" +
                                "3. meek! ip - show PixliesEarth's IP\n" +
                                "4. meek! rps <rock/paper/scissors> - RockPaperScissors\n" +
                                "5. meek! help - this command\n" +
                                "6. meek! invite - invite me to your server!\n" +
                                "```")
                        .setFooter("MiniMick | Powered by PixliesEarth!™"));
                    }
                    if (splitted[1].equalsIgnoreCase("ip")) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                        .setAuthor("MiniMick", null, api.getYourself().getAvatar().getUrl().toString())
                        .setFooter("MiniMick | Powered by PixliesEarth!™")
                        .setTitle("pixliesearth.eu")
                        .setDescription("Version: 1.15.2 Java-edition"));
                    }

                }

                if (event.getMessage().getMentionedUsers().contains(api.getYourself())) {
                    List<String> answers = Arrays.asList("What",
                            "I don't like getting pinged.",
                            "You're annoying!");
                    Random r = new Random();
                    int result = r.nextInt( answers.size() - 1) + 1;
                    event.getChannel().sendMessage(answers.get(result));
                }

            }
        });

        api.addServerMemberJoinListener(new ServerMemberJoinListener() {
            @Override
            public void onServerMemberJoin(ServerMemberJoinEvent event) {
                File serverCfg = new File("Servers/" + event.getServer().getId() + ".cfg");
                if (ConfigReader.getBoolean(serverCfg, "join-msg")) {
                    event.getServer().getTextChannelById(ConfigReader.getLong(serverCfg, "join-channel")).get().sendMessage(
                            new EmbedBuilder()
                    .setTitle("Everyone, please welcome " + event.getUser().getName() + "!")
                    .setDescription("He's new in our community :heart:!")
                    .setAuthor(event.getServer().getName(), null, event.getServer().getIcon().get().getUrl().toString())
                    .setFooter("MiniMick | Powered by PixliesEarth!™", api.getYourself().getAvatar().getUrl().toString())
                    .setColor(Color.CYAN));
                }
            }
        });

        api.addServerVoiceChannelMemberLeaveListener(event -> {
            event.getServer().getAudioConnection().ifPresent(connection -> {
                if (connection.getChannel() == event.getChannel()) {
                    if (event.getChannel().getConnectedUsers().size() <= 1) {
                        connection.close();
                    }
                }
            });
        });

        while (running) {
            Scanner scan = new Scanner(System.in);
            if (scan.nextLine().equalsIgnoreCase("servers")) {
                System.out.println("Servers using MiniMick");
                for (Server servers : api.getServers())
                    System.out.println(TextColor.Cyan() + "L " + TextColor.Reset() + servers.getName());
            }
        }
    }
}