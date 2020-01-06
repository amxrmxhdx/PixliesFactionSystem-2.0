package me.mickmmars.factions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import me.mickmmars.factions.chunk.ChunkManager;
import me.mickmmars.factions.commands.ConstructTabCompleter;
import me.mickmmars.factions.commands.FactionCommand;
import me.mickmmars.factions.commands.HomeCommand;
import me.mickmmars.factions.config.ConfigManager;
import me.mickmmars.factions.factions.FactionManager;
import me.mickmmars.factions.listener.*;
import me.mickmmars.factions.message.manager.MessageManager;
import me.mickmmars.factions.placeholders.Placeholders;
import me.mickmmars.factions.player.ChunkPlayer;
import me.mickmmars.factions.player.data.PlayerData;
import me.mickmmars.factions.util.FileManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;

import static java.lang.reflect.Modifier.TRANSIENT;
import static me.mickmmars.factions.util.StaticTypeAdapterFactory.getStaticTypeAdapterFactory;

public class Factions extends JavaPlugin {

    public static Economy econ = null;

    private static Factions instance;

    private final List<UUID> registeredPlayers = new ArrayList<>();
    private final List<ChunkPlayer> players = new ArrayList<>();
    private final List<UUID> autoClaim = new ArrayList<>();
    private final List<UUID> autoUnclaim = new ArrayList<>();
    private final Map<UUID, List<Chunk>> autoClaimChunks = new HashMap<>();
    private final Map<UUID, List<Chunk>> autoUnclaimChunks = new HashMap<>();
    private final List<UUID> staffmode = new ArrayList<>();
    private final List<UUID> factionchat = new ArrayList<>();
    private final List<UUID> createfactiongui = new ArrayList<>();
    private final List<UUID> factionfly = new ArrayList<>();
    private final List<UUID> teleportingPlayers = new ArrayList<>();

    public List<UUID> getTeleportingPlayers() { return teleportingPlayers; }

    private final Gson gson = new Gson(),
            prettyGson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .excludeFieldsWithModifiers(TRANSIENT)
                    .registerTypeAdapterFactory(getStaticTypeAdapterFactory())
                    .setPrettyPrinting()
                    .create();
    private final JsonParser parser = new JsonParser();

    public FileManager flags;

    private ChunkManager chunkManager;
    private FactionManager factionManager;
    private ConfigManager configManager;
    private MessageManager messageManager;

    private static final Logger log = Logger.getLogger("Minecraft");

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) {
            return false;
        }
        this.econ = (Economy)registeredServiceProvider.getProvider();
        return (this.econ != null);
    }

    private static final Logger logger = Logger.getLogger("[PixliesFactionSystem]");

    @Override
    public void onEnable() {

        if (!setupEconomy()) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", new Object[] { getDescription().getName() }));
            getServer().getPluginManager().disablePlugin(this);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            System.out.print("Could not find PlaceholderAPI! PlaceholderAPI functions are disabled!");
        } else {
            (new Placeholders(this)).register();
        }

        instance = this;

        this.init();

        flags = new FileManager(this, "flags", getDataFolder().getAbsolutePath());
        flags.save();
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
        this.getCommand("factions").setTabCompleter(new ConstructTabCompleter());
        this.getCommand("home").setExecutor(new HomeCommand());
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerClickEventListener(), this);
        pluginManager.registerEvents(new PlayerJoinEventListener(), this);
        pluginManager.registerEvents(new PlayerQuitEventListener(), this);
        pluginManager.registerEvents(new PlayerChatEventListener(), this);
        pluginManager.registerEvents(new ChunkProtectionListener(), this);
        pluginManager.registerEvents(new PlayerMoveEventListener(), this);
        pluginManager.registerEvents(new PlayerHitListener(), this);
        pluginManager.registerEvents(new FlagsListener(), this);
        pluginManager.registerEvents(new TeleportationListeners(), this);
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

    public List<UUID> getAutoClaim() {
        return autoClaim;
    }

    public List<UUID> getAutoUnclaim() {
        return autoUnclaim;
    }

    public List<UUID> getStaffmode() { return staffmode; }

    public List<UUID> getFactionChatPlayers() { return factionchat; }

    public Map<UUID, List<Chunk>> getAutoClaimChunks() {
        return autoClaimChunks;
    }

    public Map<UUID, List<Chunk>> getAutoUnclaimChunks() { return autoUnclaimChunks; }

    public List<UUID> getCreatefactiongui() { return createfactiongui; }

    public List<UUID> getFactionfly() { return factionfly; }

    public static class Skulls {
        public final static String[] BACK_ITEM = new String[] {"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0=", "{display:{Name:\\\"Black Backward\\\"},SkullOwner:{Id:\\\"66d19f8d-b159-4034-9b86-3f75b6064629\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0=\\\"}]}}}"};
        public final static String[] FILL = new String[] {"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19", "{display:{Name:\\\"Chest\\\"},SkullOwner:{Id:\\\"33a84c61-263c-4689-a62c-3b8044e1ff4d\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19\\\"}]}}}"};
    }
}
