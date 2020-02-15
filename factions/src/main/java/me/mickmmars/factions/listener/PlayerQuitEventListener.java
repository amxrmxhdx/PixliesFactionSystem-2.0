package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.war.events.WarEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (instance.getPlayerData(player).isInFaction()) {
            if (instance.getPlayerData(player).getCurrentFactionData().isInWar()) {
                if (instance.getPlayerData(player).getCurrentFactionData().listOnlineMembers().size() - 1 == 0) {
                    Bukkit.getPluginManager().callEvent(new WarEndEvent(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getOpposingFactionId()), instance.getPlayerData(player).getCurrentFactionData()));
                    instance.getPlayerData(player).getCurrentFactionData().setIfIsInWar(false);
                    instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getOpposingFactionId()).setIfIsInWar(false);
                    instance.getFactionManager().updateFactionData(instance.getPlayerData(player).getCurrentFactionData());
                    instance.getFactionManager().updateFactionData(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getOpposingFactionId()));
                }
            }
        }
        instance.getPlayers().remove(instance.getChunkPlayer(player));
        instance.getStaffmode().remove(player.getUniqueId());

    }

}
