package me.mickmmars.minimick;

import me.mickmmars.minimick.listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

public class Main {

    public static Config config;
    public static JDA jda;

    public static void main(String[] args) {
        config = new Config();
        String token = "";
        if (config.get("token") == null) {
            config.createConfig();
        }
        if (config.get("token").equals("TOKEN_HERE")) {
            System.out.println("§7Invalid token. Enter token: ");
            Scanner s = new Scanner(System.in);
            token = s.nextLine();
        }
        if (token.equals("")) {
            token = config.get("token");
        }
        try {
            jda = new JDABuilder(token).setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "What the hell is oatmeal?")).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        jda.addEventListener(new MessageListener());

    }

}
