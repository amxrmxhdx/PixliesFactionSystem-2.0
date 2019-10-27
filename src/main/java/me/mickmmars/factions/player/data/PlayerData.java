package me.mickmmars.factions.player.data;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.rank.FactionRank;
import me.mickmmars.factions.home.data.HomeData;

import java.util.List;

public class PlayerData {

    private boolean isInFaction;
    private String factionId;
    private FactionRank factionRank;
    private List<String> factionInvites;
    private List<HomeData> homes;

    public PlayerData(boolean isInFaction, String factionId, FactionRank factionRank, List<String> factionInvites, List<HomeData> homes) {
        this.isInFaction = isInFaction;
        this.factionId = factionId;
        this.factionRank = factionRank;
        this.factionInvites = factionInvites;
        this.homes = homes;
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
