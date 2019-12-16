package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void handleJoin(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        instance.getPlayers().remove(instance.getChunkPlayer(player));
        instance.getStaffmode().remove(player.getUniqueId());
    }

}
