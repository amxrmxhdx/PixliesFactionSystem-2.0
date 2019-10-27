package me.mickmmars.factions.message.manager;

import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.message.Message;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageManager {

    private final File file = new File("plugins/Factions/messages/" + Config.MESSAGES.getData() + ".yml");
    private YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public void loadValues() {
        System.out.println("Load messages...");
        for (Message value : Message.values())
            value.setMessage(this.cfg.get(value.name().toLowerCase()));
        System.out.println("All messages (" + Message.values().length + ") loaded.");
    }

    public void reload() {
        this.cfg = YamlConfiguration.loadConfiguration(file);
        this.loadValues();
    }

    public void createFileIfNotExists() {
        if (!this.file.exists()) {
            for (Message value : Message.values())
                this.cfg.set(value.name().toLowerCase(), value.getDefaultMessage());
            try {
                this.cfg.save(this.file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Message value : Message.values()) {
            if (this.cfg.get(value.name().toLowerCase()) == null) {
                this.cfg.set(value.name().toLowerCase(), value.getDefaultMessage());
                try {
                    this.cfg.save(this.file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void reset() {
        for (Message value : Message.values()) {
            this.cfg.set(value.name().toLowerCase(), value.getDefaultMessage());
            value.setMessage(value.getDefaultMessage());
        }
        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getCfg() {
        return cfg;
    }


}
