package me.mickmmars.factions.war.events;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.war.data.CasusBelli;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarStartEvent extends Event implements Cancellable {

    private CasusBelli cb;
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    public WarStartEvent(CasusBelli cb) {
        this.cb = cb;
        this.cancelled = false;
    }

    public FactionData getAttacker() {
        return Factions.getInstance().getFactionManager().getFactionById(cb.getAttackerId());
    }

    public FactionData getDefender() {
        return Factions.getInstance().getFactionManager().getFactionById(cb.getDefenderId());
    }

    public CasusBelli getCB() {
        return cb;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
