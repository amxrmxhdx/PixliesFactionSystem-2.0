package me.mickmmars.factions.factions.data;

import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.factions.flags.FactionFlag;

import java.util.List;

public class FactionData {

    private String name;
    private String id;
    private List<FactionFlag> allowedFlags;
    private List<ChunkData> chunks;
    private String maxPower;
    private String description;
    private int money;

    public FactionData(String name, String id, List<FactionFlag> allowedFlags, List<ChunkData> chunks, String maxPower, String description, int money) {
        this.name = name;
        this.id = id;
        this.allowedFlags = allowedFlags;
        this.chunks = chunks;
        this.maxPower = maxPower;
        this.description = description;
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FactionFlag> getAllowedFlags() {
        return allowedFlags;
    }

    public void setAllowedFlags(List<FactionFlag> allowedFlags) {
        this.allowedFlags = allowedFlags;
    }

    public List<ChunkData> getChunks() {
        return chunks;
    }

    public void setChunks(List<ChunkData> chunks) {
        this.chunks = chunks;
    }

    public String getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(String maxPower) {
        this.maxPower = maxPower;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
