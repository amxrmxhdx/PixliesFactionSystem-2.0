package me.mickmmars.factions.events;

import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.factions.data.FactionData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TerritoryClaimEvent extends Event implements Cancellable {

    boolean cancelled = false;
    FactionData faction;
    ChunkData chunkData;

    public TerritoryClaimEvent(FactionData faction, ChunkData chunkData) {
        this.faction = faction;
        this.chunkData = chunkData;
    }

    public FactionData getFaction() { return faction; }

    public ChunkData getChunkData() { return chunkData; }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }

    public boolean isCancelled() { return false; }

    public void setCancelled(boolean b) { cancelled = b; }

}
