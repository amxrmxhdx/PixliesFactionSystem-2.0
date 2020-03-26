package me.mickmmars.factions.chunk;

import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ChunkManager {

    private final Map<ChunkData, FactionData> chunks;

    public ChunkManager() {
        chunks = new HashMap<>();
    }

    public void loadChunks() {
        for (FactionData factionData : Factions.getInstance().getFactionManager().getFactions())
            for (ChunkData chunk : factionData.getDBChunks())
                chunks.put(chunk, factionData);
        System.out.println("§cFactions added " + chunks.size() + " chunks.");
    }

    public void removeChunkFromColl(ChunkData chunk) {
        chunks.remove(chunk);
    }

    public Map<ChunkData, String> getChunkMap(FactionData data) {
        Map<ChunkData, String> map = new HashMap<>();
        for (ChunkData chDat : chunks.keySet())
            if (chunks.get(chDat) == data)
                map.put(chDat, "");
        return map;
    }

    public boolean isFree(Chunk chunk) {
        return getFactionDataByChunk(chunk.getX(), chunk.getZ()) == null;
    }

    public Map<ChunkData, FactionData> getChunks() {
        return chunks;
    }

    public ChunkData getChunkDataByChunk(Chunk chunk) {
        for (ChunkData chunkData : chunks.keySet())
            if (chunkData.getMinecraftX() == chunk.getX() && chunkData.getMinecraftZ() == chunk.getZ())
                return chunkData;
        return null;
    }

    public ChunkData getChunkDataByChunk(int x, int z) {
        for (ChunkData chunkData : chunks.keySet())
            if (chunkData.getMinecraftX() == x && chunkData.getMinecraftZ() == z)
                return chunkData;
        return null;
    }

    public FactionData getFactionDataByChunk(int x, int z) {
        FactionData data = null;
                for (ChunkData chunk : chunks.keySet())
                    if (chunk.getMinecraftX() == x)
                        if (chunk.getMinecraftZ() == z)
                            data = chunks.get(chunk);
        return data;
    }

    public FactionData getFactionDataByChunk(Chunk chunk) {
        FactionData data = null;
                for (ChunkData chunk1 : chunks.keySet())
                    if (chunk1.getMinecraftX() == chunk.getX())
                        if (chunk1.getMinecraftZ() == chunk.getZ())
                            data = chunks.get(chunk1);
        return data;
    }

    public void addChunk(ChunkData chunk, FactionData fac) {
        chunks.put(chunk, fac);
    }

    public void removeChunk(ChunkData chunk) {
        chunks.remove(getChunkDataByChunk(chunk.getMinecraftX(), chunk.getMinecraftZ()));
    }

    public void removeFactionChunks(FactionData fac) {

        Set<Map.Entry<ChunkData, FactionData>> entrySet = chunks.entrySet();

        Iterator<Map.Entry<ChunkData, FactionData>> itr = entrySet.iterator();

        while(itr.hasNext()) {
            Map.Entry<ChunkData, FactionData> entry = itr.next();
            FactionData val = entry.getValue();

            if (val == fac) {
                itr.remove();
            }

        }

    }

    public Set<ChunkData> getFactionsChunks(FactionData fac) {
        Set<ChunkData> chunkDatas = new HashSet<>();
        for (ChunkData chunk : chunks.keySet())
            if (chunks.get(chunk) == fac)
                chunkDatas.add(chunk);
        return chunkDatas;
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
