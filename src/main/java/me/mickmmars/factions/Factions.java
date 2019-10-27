package me.mickmmars.factions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import me.mickmmars.factions.chunk.ChunkManager;
import me.mickmmars.factions.commands.FactionCommand;
import me.mickmmars.factions.config.ConfigManager;
import me.mickmmars.factions.factions.FactionManager;
import me.mickmmars.factions.listener.PlayerChatEventListener;
import me.mickmmars.factions.listener.PlayerClickEventListener;
import me.mickmmars.factions.listener.PlayerJoinEventListener;
import me.mickmmars.factions.message.manager.MessageManager;
import me.mickmmars.factions.player.ChunkPlayer;
import me.mickmmars.factions.player.data.PlayerData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

public class Factions extends JavaPlugin {

    public static Economy econ = null;

    private static Factions instance;

    private final List<UUID> registeredPlayers = new ArrayList<>();
    private final List<ChunkPlayer> players = new ArrayList<>();

    private final Gson gson = new Gson(),
            prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private final JsonParser parser = new JsonParser();

    private ChunkManager chunkManager;
    private FactionManager factionManager;
    private ConfigManager configManager;
    private MessageManager messageManager;

    private static final Logger log = Logger.getLogger("Minecraft");

    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        return econ != null;
    }

    @Override
    public void onEnable() {

        instance = this;

        this.init();
    }

    @Override
    public void onDisable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            onlinePlayer.kickPlayer("Reload");
    }

    private void init() {
        this.loadPlayers();

        configManager = new ConfigManager();
        configManager.createFileIfNotExists();
        configManager.loadValues();

        messageManager = new MessageManager();
        messageManager.createFileIfNotExists();
        messageManager.loadValues();

        factionManager = new FactionManager();
        factionManager.loadFactions();

        chunkManager = new ChunkManager();
        chunkManager.loadChunks();

        this.registerListener(Bukkit.getPluginManager());
        this.registerCommands();
    }

    public void reloadPlugin() {
        registeredPlayers.clear();
        loadPlayers();
    }

    private void registerCommands() {
        this.getCommand("factions").setExecutor(new FactionCommand());
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerClickEventListener(), this);
        pluginManager.registerEvents(new PlayerJoinEventListener(), this);
        pluginManager.registerEvents(new PlayerChatEventListener(), this);
    }

    private void loadPlayers() {
        if (new File("plugins/Factions/players").listFiles() != null)
            for (File file : new File("plugins/Factions/players").listFiles())
                registeredPlayers.add(UUID.fromString(file.getName().replace(".yml", "")));
    }

    public String generateKey(int letters) {
        final StringBuilder builder = new StringBuilder();
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        for (int i = 0; i < letters; i++)
            builder.append(alphabet.charAt(new Random().nextInt(alphabet.length())));
        return builder.toString();
    }

    public ChunkPlayer getChunkPlayer(final UUID uuid) {
        boolean online = Bukkit.getPlayer(uuid) != null;
        if (!online)
            return new ChunkPlayer(uuid);
        for (ChunkPlayer player : players)
            if (player.getUuid().equals(uuid))
                return player;
        return null;
    }

    public ChunkPlayer getChunkPlayer(final Player player) {
        boolean online = player != null;
        if (!online)
            return null;
        for (ChunkPlayer chunkPlayer : players)
            if (chunkPlayer.getUuid().equals(player.getUniqueId()))
                return chunkPlayer;
        return null;
    }

    public String asDecimal(int i) {
        return new DecimalFormat().format(i).replace(",", ".");
    }

    public PlayerData getPlayerData(final UUID uuid) {
        return getChunkPlayer(uuid).getPlayerData();
    }

    public PlayerData getPlayerData(final Player player) {
        return getChunkPlayer(player).getPlayerData();
    }

    public static Factions getInstance() {
        return instance;
    }

    public Gson getGson() {
        return gson;
    }

    public Gson getPrettyGson() {
        return prettyGson;
    }

    public JsonParser getParser() {
        return parser;
    }

    public List<UUID> getRegisteredPlayers() {
        return registeredPlayers;
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public List<ChunkPlayer> getPlayers() {
        return players;
    }

    public FactionManager getFactionManager() {
        return factionManager;
    }

    public static String col(String msg) {
        String coloredMSG = "";
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == '&')
                coloredMSG += '§';
            else
                coloredMSG += msg.charAt(i);
        }
        return coloredMSG;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public static class Skulls {
        public final static String[] BACK_ITEM = new String[] {"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTJmMDQyNWQ2NGZkYzg5OTI5MjhkNjA4MTA5ODEwYzEyNTFmZTI0M2Q2MGQxNzViZWQ0MjdjNjUxY2JlIn19fQ==", "{display:{Name:\\\"Birch Arrow Left\\\"},SkullOwner:{Id:\\\"18ca284c-c03d-4324-8a5f-cc8a5eccf91f\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTJmMDQyNWQ2NGZkYzg5OTI5MjhkNjA4MTA5ODEwYzEyNTFmZTI0M2Q2MGQxNzViZWQ0MjdjNjUxY2JlIn19fQ==\\\"}]}}}"};
    }
}
