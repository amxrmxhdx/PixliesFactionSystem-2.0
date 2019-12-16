package me.mickmmars.factions.factions.data;

import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.chunk.location.ChunkLocation;
import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.perms.FactionPerms;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FactionData {

    private String name;
    private String id;
    private List<String> allowedFlags;
    private List<ChunkData> chunks;
    private int maxPower;
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
    private int money;

    public FactionData(String name, String id, List<String> allowedFlags, List<ChunkData> chunks, int maxPower, String description, List<FactionPerms> perms, String discordlink, List<String> allies, List<String> allyrequests, List<String> enemies, ChunkLocation capital, List<String> adminperms, List<String> memberperms, List<String> newbieperms, List<String> allyperms, List<String> enemyperms, List<String> applications,int money) {
        this.name = name;
        this.id = id;
        this.allowedFlags = allowedFlags;
        this.chunks = chunks;
        this.maxPower = maxPower;
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
        this.money = money;
    }

    public List<String> getAdminperms() { return adminperms; }
    public List<String> getMemberperms() { return memberperms; }
    public List<String> getNewbieperms() { return newbieperms; }
    public List<String> getAllyperms() { return allyperms; }
    public List<String> getEnemyperms() { return enemyperms; }

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

    public int getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
    }

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
}
