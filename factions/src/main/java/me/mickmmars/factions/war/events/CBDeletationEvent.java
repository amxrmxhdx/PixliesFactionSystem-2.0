package me.mickmmars.factions.war.events;

import me.mickmmars.factions.war.data.CasusBelli;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CBDeletationEvent extends Event implements Cancellable {
    private CasusBelli cb;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public CBDeletationEvent(CasusBelli cb) {
        this.cb = cb;
        this.isCancelled = false;
    }

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
