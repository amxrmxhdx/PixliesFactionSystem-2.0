package me.mickmmars.factions.war.events;

import me.mickmmars.factions.war.data.CasusBelli;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StaffNeededEvent extends Event implements Cancellable {

    private CasusBelli cb;
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    public StaffNeededEvent(CasusBelli cb) {
        this.cb = cb;
        this.cancelled = false;
    }

    public CasusBelli getCb() { return cb; }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
