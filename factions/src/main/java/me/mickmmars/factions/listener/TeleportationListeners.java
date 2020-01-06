package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportationListeners implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (Factions.getInstance().getTeleportingPlayers().contains(player.getUniqueId())) {
            if ((event.getFrom().getX() != event.getTo().getX() && event.getFrom().getY() != event.getTo().getY() && event.getFrom().getZ() != event.getTo().getZ())) {
                Factions.getInstance().getTeleportingPlayers().remove(player.getUniqueId());
                player.sendMessage("§8» §cTeleportation was canceled due to your inability to stand still.");
            }
        }
    }


}
