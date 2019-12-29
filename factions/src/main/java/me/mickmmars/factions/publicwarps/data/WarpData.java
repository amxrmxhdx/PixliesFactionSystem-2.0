package me.mickmmars.factions.publicwarps.data;

import me.mickmmars.factions.chunk.location.ChunkLocation;

public class WarpData {

    private final String name;
    private final ChunkLocation location;

    public WarpData(String name, ChunkLocation location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public ChunkLocation getLocation() {
        return location;
    }

}
