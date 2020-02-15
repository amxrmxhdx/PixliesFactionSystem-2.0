package me.mickmmars.factions.chunk;

import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

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

    public void reloadChunks() {
        chunks.clear();
        for (FactionData factioNData : Factions.getInstance().getFactionManager().getFactions())
            chunks.addAll(factioNData.getChunks());
    }

    public void removeChunkFromColl(ChunkData chunk) {
        chunks.remove(chunk);
    }

    @Deprecated
    public boolean isFree(Chunk chunk) {
        if (getFactionDataByChunk(chunk.getX(), chunk.getZ()) != null)
            return false;
        return true;
    }

    public List<ChunkData> getChunks() {
        return chunks;
    }

    public ChunkData getChunkDataByChunk(Chunk chunk) {
        for (ChunkData chunkData : chunks)
            if (chunkData.getMinecraftX() == chunk.getX() && chunkData.getMinecraftZ() == chunk.getZ())
                return chunkData;
        return null;
    }

    public FactionData getFactionDataByChunk(int x, int z) {
        for (FactionData faction : Factions.getInstance().getFactionManager().getFactions())
            for (ChunkData factionChunk : faction.getChunks())
                if (factionChunk.getMinecraftX() == x && factionChunk.getMinecraftZ() == z)
                    return faction;
        return null;
    }

    public FactionData getFactionDataByChunk(Chunk chunk) {
        for (FactionData faction : Factions.getInstance().getFactionManager().getFactions())
            for (ChunkData factionChunk : faction.getChunks())
                if (factionChunk.getMinecraftX() == chunk.getX() && factionChunk.getMinecraftZ() == chunk.getZ())
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

    public Chunk getChunkFromXZ(int x, int z) {
        return Bukkit.getWorld(Config.FACTION_WORLD.getData().toString()).getChunkAt(x, z);
    }
}
