package me.mickmmars.factions.chunk.data;

import me.mickmmars.factions.chunk.location.ChunkLocation;

import java.util.List;
import java.util.UUID;

public class ChunkData {

    private final String id;
    private final List<UUID> member;
    private final ChunkLocation maxLocation;
    private final ChunkLocation minLocation;

    public ChunkData(String id, List<UUID> member, ChunkLocation maxLocation, ChunkLocation minLocation) {
        this.id = id;
        this.member = member;
        this.maxLocation = maxLocation;
        this.minLocation = minLocation;
    }

    public String getId() {
        return id;
    }

    public List<UUID> getMemeber() {
        return member;
    }

    public ChunkLocation getMaxLocation() {
        return maxLocation;
    }

    public ChunkLocation getMinLocation() {
        return minLocation;
    }
}
