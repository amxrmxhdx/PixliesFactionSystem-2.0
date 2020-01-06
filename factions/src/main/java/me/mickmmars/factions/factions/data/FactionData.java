package me.mickmmars.factions.factions.data;

import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.chunk.location.ChunkLocation;
import me.mickmmars.factions.Factions;
import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.itemstacks.BannerData;
import me.mickmmars.factions.factions.perms.FactionPerms;
import me.mickmmars.factions.publicwarps.data.WarpData;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.*;

public class FactionData {

    private String name;
    private String id;
    private List<String> allowedFlags;
    private List<ChunkData> chunks;
    private int powerboost;
    private String description;
    private List<FactionPerms> perms;
    private String discordlink;
    private List<String> allies;
    private List<String> allyrequests;
    private List<String> enemies;
    private ChunkLocation capital;
    private List<String> adminperms;
    private List<String> memberperms;
    private List<String> newbieperms;
    private List<String> allyperms;
    private List<String> enemyperms;
    private List<String> applications;
    private List<String> upgrades;
    private List<WarpData> warps;
    private int money;
    private List<String> puppets;
    private List<UUID> bannedplayer;
    private List<String> puppetrequests;
    private Boolean isInWar;

    public FactionData(String name, String id, List<String> allowedFlags, List<ChunkData> chunks, int powerboost, String description, List<FactionPerms> perms, String discordlink, List<String> allies, List<String> allyrequests, List<String> enemies, ChunkLocation capital, List<String> adminperms, List<String> memberperms, List<String> newbieperms, List<String> allyperms, List<String> enemyperms, List<String> applications, List<String> upgrades, List<WarpData> warps, int money, List<String> puppets, List<UUID> bannedplayer, List<String> puppetrequests, Boolean isInWar) {
        this.name = name;
        this.id = id;
        this.allowedFlags = allowedFlags;
        this.chunks = chunks;
        this.powerboost = powerboost;
        this.description = description;
        this.perms = perms;
        this.discordlink = discordlink;
        this.allies = allies;
        this.allyrequests = allyrequests;
        this.enemies = enemies;
        this.capital = capital;
        this.adminperms = adminperms;
        this.memberperms = memberperms;
        this.newbieperms = newbieperms;
        this.allyperms = allyperms;
        this.enemyperms = enemyperms;
        this.applications = applications;
        this.upgrades = upgrades;
        this.warps = warps;
        this.money = money;
        this.puppets = puppets;
        this.bannedplayer = bannedplayer;
        this.puppetrequests = puppetrequests;
        this.isInWar = isInWar;
    }

    public Boolean isInWar() { return isInWar; }
    public void setIfIsInWar(Boolean isInWar) { this.isInWar = isInWar; }

    public List<String> getPuppetrequests() { return puppetrequests; }
    public void setPuppetrequests(List<String> puppetrequests) { this.puppetrequests = puppetrequests; }

    public List<String> getPuppets() { return puppets; }
    public void setPuppets(List<String> puppets) { this.puppets = puppets; }

    public List<UUID> getBannedplayer() { return bannedplayer; }
    public void setBannedplayer(List<UUID> bannedplayer) { this.bannedplayer = bannedplayer; }

    public List<UUID> listMembers() {
        return Factions.getInstance().getFactionManager().getMembersFromFaction(this);
    }

    public List<UUID> listOnlineMembers() {
        List<UUID> members = new ArrayList<UUID>();
        for (UUID uuid : listMembers()) {
            if (Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getPlayer(uuid))) {
                members.add(uuid);
            }
        }
        return members;
    }

    public void broadcastMessage(String Message) {
        for (UUID uuid : listOnlineMembers())
            Bukkit.getPlayer(uuid).sendMessage(Message);
    }

    public List<WarpData> getWarps() { return warps; }
    public void setWarps(List<WarpData> warps) { this.warps = warps; }

    public List<String> getAdminperms() { return adminperms; }
    public List<String> getMemberperms() { return memberperms; }
    public List<String> getNewbieperms() { return newbieperms; }
    public List<String> getAllyperms() { return allyperms; }
    public List<String> getEnemyperms() { return enemyperms; }

    public List<String> getUpgrades() { return upgrades; }

    public void setUpgrades(List<String> upgrades) { this.upgrades = upgrades; }

    public List<String> getApplications() { return applications; }

    public void setApplications(List<String> applications) {
        this.applications = applications;
    }

    public List<UUID> getOnlineMembers() {
        List<UUID> onlinemembers = new ArrayList<UUID>();
        for (UUID uuid : Factions.getInstance().getFactionManager().getMembersFromFaction(this)) {
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid))) {
                onlinemembers.add(uuid);
            }
        }
        return onlinemembers;
    }

    public void setAdminperms(List<String> adminperms) {
        this.adminperms = adminperms;
    }

    public void setMemberperms(List<String> memberperms) {
        this.memberperms = memberperms;
    }

    public void setNewbieperms(List<String> newbieperms) {
        this.newbieperms = newbieperms;
    }

    public void setAllyperms(List<String> allyperms) {
        this.allyperms = allyperms;
    }

    public void setEnemyperms(List<String> enemyperms) {
        this.enemyperms = enemyperms;
    }

    public String getName() {
        return name;
    }

    public ChunkLocation getCapitalLocation() { return capital; }

    public void setCapital(ChunkLocation loc) { this.capital = loc; }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscordlink() { return discordlink; }

    public void setDiscordlink(String discordlink) { this.discordlink = discordlink; };

    public List<String> getAllies() {
        return allies;
    }

    public void setAllyRequests(List<String> allyrequests) {
        this.allyrequests = allyrequests;
    }

    public void setAllies(List<String> allies) {
        this.allies = allies;
    }

    public List<String> getAllyRequests() {
        return allyrequests;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAllowedFlags() {
        return allowedFlags;
    }

    public void setAllowedFlags(List<String> allowedFlags) {
        this.allowedFlags = allowedFlags;
    }

    public List<ChunkData> getChunks() {
        return chunks;
    }

    public void setChunks(List<ChunkData> chunks) {
        this.chunks = chunks;
    }

    public int getPowerboost() {
        return powerboost;
    }

    public void setPowerboost(int powerboost) {
        this.powerboost = powerboost;
    }

    public int getPower() { return (listMembers().size() * (int) Config.DEFAULT_PLAYER_POWER.getData()) + powerboost - getChunks().size(); }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FactionPerms> getPerms() { return perms; }

    public void setPerms(List<FactionPerms> perms) { this.perms = perms; }

    public int getMoney() {
        return money;
    }

    public List<String> getEnemies() { return enemies; }

    public void setEnemies(List<String> enemies) {
        this.enemies = enemies;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void sendMessageToMembers(String Message) {
        for (UUID uuid : Factions.getInstance().getFactionManager().getMembersFromFaction(this))
            Bukkit.getPlayer(uuid).sendMessage(Message);
    }

    public String getMembersString(String split, String colour) {
        StringJoiner sj = new StringJoiner(split);
        for (UUID uuid : listMembers())
            sj.add(colour + Bukkit.getPlayer(uuid).getName());
        return sj.toString();
    }

    public String getOnlineMembersString(String split, String colour) {
        StringJoiner sj = new StringJoiner(split);
        for (UUID uuid : listMembers()) {
            if (Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getPlayer(uuid))) {
                sj.add(colour + Bukkit.getPlayer(uuid).getName());
            }
        }
        return sj.toString();
    }
}
