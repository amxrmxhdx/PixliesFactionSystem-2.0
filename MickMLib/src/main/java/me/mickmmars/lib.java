package me.mickmmars;

import me.mickmmars.skulls.Skulls;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class lib extends JavaPlugin {

    public static lib instance;

    public void onEnable() {
        instance = this;
        Bukkit.getLogger().info("MickMLib enabled.");
    }

    private Skulls skulls;

    public Skulls getSkulls() { return skulls; }


}
