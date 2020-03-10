package me.mickmmars.factions.war.events;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.war.data.CasusBelli;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarEndEvent extends Event implements Cancellable {

    private FactionData winner;
    private FactionData loser;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public WarEndEvent(FactionData winner, FactionData loser) {
        this.winner = winner;
        this.loser = loser;
        this.isCancelled = false;
    }

    public FactionData getWinner() {
        return winner;
    }

    public FactionData getLoser() {
        return loser;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
