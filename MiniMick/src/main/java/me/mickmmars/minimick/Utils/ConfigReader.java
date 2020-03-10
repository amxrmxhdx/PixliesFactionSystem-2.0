package me.mickmmars.minimick.Utils;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigReader {

    public static String getString(File file, String key) {
        String value = "";
        try {
            for (String keys : Files.readAllLines(file.toPath())) {
                String[] splitted = keys.split("=");
                if (splitted[0].equalsIgnoreCase(key))
                    value = splitted[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static int getInt(File file, String key) {
        int value = 0;
        try {
            for (String keys : Files.readAllLines(file.toPath())) {
                String[] splitted = keys.split("=");
                if (splitted[0].equalsIgnoreCase(key))
                    value = Integer.parseInt(splitted[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Double getDouble(File file, String key) {
        Double value = 0D;
        try {
            for (String keys : Files.readAllLines(file.toPath())) {
                String[] splitted = keys.split("=");
                if (splitted[0].equalsIgnoreCase(key))
                    value = Double.parseDouble(splitted[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean getBoolean(File file, String key) {
        boolean value = true;
        try {
            for (String keys : Files.readAllLines(file.toPath())) {
                String[] splitted = keys.split("=");
                if (splitted[0].equalsIgnoreCase(key))
                    value = Boolean.parseBoolean(splitted[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Long getLong(File file, String key) {
        Long value = null;
        try {
            for (String keys : Files.readAllLines(file.toPath())) {
                String[] splitted = keys.split("=");
                if (splitted[0].equalsIgnoreCase(key))
                    value = Long.parseLong(splitted[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void set(File file, String key, String value) {
        boolean exists = false;
        StringBuilder builder = new StringBuilder();
        File newFile = new File(file.getAbsolutePath());
        String keyFormatted = key.replace(".", "\n");
        try {
            for (String lines : Files.readAllLines(file.toPath())) {
                String[] splitted = lines.split("=");
                if (splitted[0] != key)
                    builder.append(lines + "\n");
            }
            file.delete();
            newFile.createNewFile();
            PrintWriter writer = new PrintWriter(newFile);
            writer.write(builder + keyFormatted + "=" + value + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void set(File file, String key, Double value) {
        boolean exists = false;
        StringBuilder builder = new StringBuilder();
        File newFile = new File(file.getAbsolutePath());
        String keyFormatted = key.replace(".", "\n");
        try {
            for (String lines : Files.readAllLines(file.toPath())) {
                String[] splitted = lines.split("=");
                if (splitted[0] != key)
                    builder.append(lines + "\n");
            }
            file.delete();
            newFile.createNewFile();
            PrintWriter writer = new PrintWriter(newFile);
            writer.write(builder + keyFormatted + "=" + value + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDefaultServerConfig(Server server) {
        File cfg = new File("Servers/" + server.getId() + ".cfg");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(cfg));
            writer.write("join-msg=false");
            writer.write("join-channel=null");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDefaultUserConfig(User user) {
        File cfg = new File("Users/" + user.getIdAsString() + ".cfg");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(cfg));
            writer.write("balance=0");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDefaultUserConfig(MessageAuthor user) {
        File cfg = new File("Users/" + user.getIdAsString() + ".cfg");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(cfg));
            writer.write("balance=0");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
