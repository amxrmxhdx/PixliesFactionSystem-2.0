package me.mickmmars.factions.player;

import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.home.HomeObject;
import me.mickmmars.factions.home.data.HomeData;
import me.mickmmars.factions.Factions;
import me.mickmmars.factions.config.JsonConfig;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.rank.FactionRank;
import me.mickmmars.factions.player.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkPlayer {

    private Factions instance = Factions.getInstance();

    private final UUID uuid;

    private final File file;
    private final YamlConfiguration cfg;

    private int money = 1000;
    private boolean isInFactionChunks = false;
    private boolean title = false;

    private PlayerData playerData;
    private HomeObject homeObject;

    public ChunkPlayer(UUID uuid) {
        this.uuid = uuid;

        this.file = new File("plugins/Factions/players/" + uuid.toString() + ".yml");
        this.cfg = YamlConfiguration.loadConfiguration(file);

        if (exists()) {
            final JsonConfig jsonConfig = new JsonConfig(this.file);
            playerData = jsonConfig.get("userData", PlayerData.class);

            homeObject = new HomeObject(uuid);
        }
    }

    public static List<UUID> getAllRegisteredPlayers() {
        List<UUID> returnable = new ArrayList<>();
        for (File files : new File("plugins/Factions/players/").listFiles()) {
            returnable.add(UUID.fromString(files.getName().replace(".yml", "")));
        }
        return returnable;
    }

    public static PlayerData getRegisteredPlayerFromName(String name) {
        for (UUID uuid : getAllRegisteredPlayers())
            if (Bukkit.getPlayer(uuid).getName().equalsIgnoreCase(name)) {
                JsonConfig jsonConfig = new JsonConfig(new File("plugins/Factions/players/" + uuid.toString() + ".yml"));
                return jsonConfig.get("userData", PlayerData.class);
            }
        return null;
    }

    private boolean exists() {
        return file.exists();
    }

    public boolean createIfNotExists() {
        if (exists()) return true;
        new File("plugins/Factions/players").mkdirs();
        final PlayerData playerData = new PlayerData(false, "a", FactionRank.NONE, new ArrayList<String>(), new ArrayList<HomeData>(), new ArrayList<ChunkData>(), Bukkit.getPlayer(uuid).getName(), false);

        final JsonConfig jsonConfig = new JsonConfig(this.file);
        jsonConfig.set("userData", playerData);
        jsonConfig.saveConfig();

        homeObject = new HomeObject(uuid);

        Factions.getInstance().getRegisteredPlayers().add(uuid);

        this.playerData = jsonConfig.get("userData", PlayerData.class);

        return false;
    }

    public void addToFaction(FactionData data, FactionRank rank) {
        final PlayerData playerData = this.playerData;
        playerData.setFactionRank(rank);
        playerData.setFactionId(data.getId());
        playerData.setInFaction(true);
        updatePlayerData(playerData);
    }

    public void addFactionInvite(String factionId) {
        PlayerData playerData = this.playerData;
        List<String> invites = playerData.getFactionInvites();
        invites.add(factionId);
        playerData.setFactionInvites(invites);
        updatePlayerData(playerData);
    }

    public void removeFactionInvite(String factionId) {
        PlayerData playerData = this.playerData;
        List<String> invites = playerData.getFactionInvites();
        invites.remove(factionId);
        playerData.setFactionInvites(invites);
        updatePlayerData(playerData);
    }

    public void removeFromFaction() {
        final PlayerData playerData = this.playerData;
        playerData.setFactionRank(FactionRank.NONE);
        playerData.setFactionId("a");
        playerData.setInFaction(false);
        updatePlayerData(playerData);
    }

    public void updatePlayerData(PlayerData playerData) {
        final JsonConfig jsonConfig = new JsonConfig(this.file);
        jsonConfig.set("userData", playerData);
        jsonConfig.saveConfig();

        this.playerData = playerData;
    }

    public String getRankPrefix() {
        if (playerData.getFactionRank() == FactionRank.NEWBIE) {
            return playerData.getCurrentFactionData().getNewbiePrefix();
        } else if (playerData.getFactionRank().equals(FactionRank.MEMBER)) {
            return playerData.getCurrentFactionData().getMemberPrefix();
        } else if (playerData.getFactionRank().equals(FactionRank.ADMIN)) {
            return playerData.getCurrentFactionData().getAdminPrefix();
        } else if (playerData.getFactionRank() == FactionRank.LEADER) {
            return playerData.getCurrentFactionData().getLeaderPrefix();
        }
        return null;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public UUID getUuid() {
        return uuid;
    }

    public HomeObject getHomeObject() {
        return homeObject;
    }

    public boolean isInFactionChunks() {
        return isInFactionChunks;
    }

    public void setInFactionChunks(boolean inFactionChunks) {
        isInFactionChunks = inFactionChunks;
    }

    public boolean isTitle() {
        return title;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }
}
