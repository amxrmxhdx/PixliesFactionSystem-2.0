package me.mickmmars.factions.war.events;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.war.data.CasusBelli;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCappingEvent extends Event implements Cancellable {

    private Player capper;
    private CasusBelli cb;
    private FactionData nationGettingCapped;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public PlayerCappingEvent(Player capper, CasusBelli cb, FactionData nationGettingCapped) {
        this.capper = capper;
        this.cb = cb;
        this.nationGettingCapped = nationGettingCapped;
        this.isCancelled = false;
    }

    public Player getCapper() { return capper; }

    public FactionData getNationGettingCapped() { return nationGettingCapped; }

    public CasusBelli getCB() {
        return cb;
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
