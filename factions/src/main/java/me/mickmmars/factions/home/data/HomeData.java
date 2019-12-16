package me.mickmmars.factions.home.data;

import me.mickmmars.factions.chunk.location.ChunkLocation;

public class HomeData {

    private final String name;
    private final ChunkLocation location;

    public HomeData(String name, ChunkLocation location) {
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
