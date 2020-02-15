package me.mickmmars.factions.chunk.data;

import me.mickmmars.factions.chunk.location.ChunkLocation;

import java.util.List;
import java.util.UUID;

public class ChunkData {

    private final String id;
    private final int mcx;
    private final int mcz;

    public ChunkData(String id, List<UUID> member, int mcx, int mcz) {
        this.id = id;
        this.mcx = mcx;
        this.mcz = mcz;
    }

    public String getId() {
        return id;
    }

    public int getMinecraftX() { return mcx; }

    public int getMinecraftZ() { return mcz; }


}
