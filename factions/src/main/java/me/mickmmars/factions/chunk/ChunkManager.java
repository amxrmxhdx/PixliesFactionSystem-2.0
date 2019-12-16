package me.mickmmars.factions.chunk;

import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.chunk.location.ChunkLocation;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.Factions;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ChunkManager {

    private final List<ChunkData> chunks;

    public ChunkManager() {
        chunks = new ArrayList<>();
    }

    public void loadChunks() {
        for (FactionData factionData : Factions.getInstance().getFactionManager().getFactions())
            chunks.addAll(factionData.getChunks());
        System.out.println("§cFactions added " + chunks.size() + " chunks.");
    }

    public boolean isFree(Chunk chunk) {
        final ChunkLocation maxLocation = new ChunkLocation(this.getMaxLocation(chunk));
        final ChunkLocation minLocation = new ChunkLocation(this.getMinLocation(chunk));
        for (ChunkData chunkData : this.chunks)
            if (chunkData.getMaxLocation().getX() == maxLocation.getX() && chunkData.getMaxLocation().getZ() == maxLocation.getZ() && chunkData.getMinLocation().getX() == minLocation.getX() && chunkData.getMinLocation().getZ() == minLocation.getZ())
                return false;
        return true;
    }

    public List<ChunkData> getChunks() {
        return chunks;
    }

    public ChunkData getChunkDataByChunk(Chunk chunk) {
        for (ChunkData chunkData : chunks)
            if (chunkData.getMaxLocation().getX() == getMaxLocation(chunk).getX() && chunkData.getMaxLocation().getZ() == getMaxLocation(chunk).getZ() && chunkData.getMinLocation().getX() == getMinLocation(chunk).getX() && chunkData.getMinLocation().getZ() == getMinLocation(chunk).getZ())
                return chunkData;
        return null;
    }

    public FactionData getFactionDataByChunk(Chunk chunk) {
        for (FactionData faction : Factions.getInstance().getFactionManager().getFactions())
            for (ChunkData factionChunk : faction.getChunks())
                if (factionChunk.getMaxLocation().getX() == getMaxLocation(chunk).getX() && factionChunk.getMaxLocation().getZ() == getMaxLocation(chunk).getZ() && factionChunk.getMinLocation().getX() == getMinLocation(chunk).getX() && factionChunk.getMinLocation().getZ() == getMinLocation(chunk).getZ())
                    return faction;
        return null;
    }

    public Location getMaxLocation(final Chunk chunk) {
        final int maxX = chunk.getX() * 16;
        final int maxY = 256;
        final int maxZ = chunk.getZ() * 16;
        return new Location(chunk.getWorld(), maxX, maxY, maxZ);
    }

    public Location getMinLocation(final Chunk chunk) {
        final int maxX = (chunk.getX() * 16) + 15;
        final int maxY = 0;
        final int maxZ = (chunk.getZ() * 16) + 15;
        return new Location(chunk.getWorld(), maxX, maxY, maxZ);
    }
}
