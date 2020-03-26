package me.mickmmars.factions.player.data;

import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.home.data.HomeData;
import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.rank.FactionRank;

import java.util.List;

public class PlayerData {

    private String lastKnownName;
    private boolean isInFaction;
    private String factionId;
    private FactionRank factionRank;
    private List<String> factionInvites;
    private List<HomeData> homes;
    private List<ChunkData> AccessableChunks;
    private Boolean capping;

    public PlayerData(boolean isInFaction, String factionId, FactionRank factionRank, List<String> factionInvites, List<HomeData> homes, List<ChunkData> AccessableChunks, String lastKnownName, Boolean capping) {
        this.isInFaction = isInFaction;
        this.factionId = factionId;
        this.factionRank = factionRank;
        this.factionInvites = factionInvites;
        this.homes = homes;
        this.AccessableChunks = AccessableChunks;
        this.lastKnownName = lastKnownName;
        this.capping = capping;
    }

    public Boolean isCapping() { return capping; }
    public void setIsCapping(Boolean isCapping) { this.capping = isCapping; }

    public String getLastKnownName() { return lastKnownName; }

    public List<ChunkData> getAccessableChunks() {
        return AccessableChunks;
    }

    public void setAccessableChunks(List<ChunkData> AccessableChunks) {
        this.AccessableChunks = AccessableChunks;
    }

    public boolean isInFaction() {
        return isInFaction;
    }

    public void setInFaction(boolean inFaction) {
        isInFaction = inFaction;
    }

    public String getFactionId() {
        return factionId;
    }

    public void setFactionId(String factionId) {
        this.factionId = factionId;
    }

    public FactionRank getFactionRank() {
        return factionRank;
    }

    public void setFactionRank(FactionRank factionRank) {
        this.factionRank = factionRank;
    }

    public List<String> getFactionInvites() {
        return factionInvites;
    }

    public void setFactionInvites(List<String> factionInvites) {
        this.factionInvites = factionInvites;
    }

    public FactionData getCurrentFactionData() {
        if (!isInFaction) return null;
        return Factions.getInstance().getFactionManager().getFactionById(factionId);
    }

    public List<HomeData> getHomes() {
        return homes;
    }

    public void setHomes(List<HomeData> homes) {
        this.homes = homes;
    }
    
}
