package me.mickmmars.minimick;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    String rootPath;
    String configPath;
    Properties props;

    public Config() {
        rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        configPath = rootPath + "config.properties";
        props = new Properties();
        try {
            props.load(new FileInputStream(configPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return props.getProperty(key);
    }

    public void set(String key, String value) {
        props.setProperty(key, value);
    }

    public void createConfig() {
        File cfg = new File("config.properties");
        try {
            cfg.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        set("token", "TOKEN_HERE");
    }

}
