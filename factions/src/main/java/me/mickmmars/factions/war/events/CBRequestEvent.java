package me.mickmmars.factions.war.events;

import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.war.data.CasusBelli;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CBRequestEvent extends Event implements Cancellable {
    private CasusBelli cb;
    private Player sender;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public CBRequestEvent(CasusBelli cb, Player sender) {
        this.sender = sender;
        this.cb = cb;
        this.isCancelled = false;
    }

    public CasusBelli getCB() {
        return cb;
    }

    public Player getSender() { return sender; }

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
