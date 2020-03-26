package me.mickmmars.factions.war.listeners;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.war.data.CasusBelli;
import me.mickmmars.factions.war.events.PlayerCappingEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class CappingListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void onCap(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        boolean active = true;

        if (instance.getPlayerData(player).isInFaction() && instance.getPlayerData(player).getCurrentFactionData().isInWar()) {
            if (instance.getWarFactions().get(instance.getPlayerData(player).getCurrentFactionData()).isInTreaty()) {
                return;
            }
            for (UUID uuid : instance.getPlayerData(player).getCurrentFactionData().listOnlineMembers())
                if (instance.getPlayerData(uuid).isCapping())
                    active = false;
            if (!instance.getWarFactions().get(instance.getPlayerData(player).getCurrentFactionData()).inGracePeriod() && active) {
                if (instance.getPlayerData(player).getCurrentFactionData().isInWar()) {
                    CasusBelli cb = instance.getWarFactions().get(instance.getPlayerData(player).getCurrentFactionData()).getCB();
                    if (event.getFrom().getChunk() != event.getTo().getChunk() && instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getOpposingFactionId()).getCapitalLocation().toBukkitLocation().getChunk() == event.getTo().getChunk() && !instance.getPlayerData(player).isCapping()) {
                        Bukkit.getPluginManager().callEvent(new PlayerCappingEvent(player, CasusBelli.getUsed(instance.getPlayerData(player).getCurrentFactionData()), instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getOpposingFactionId())));
                        instance.getWarFactions().get(instance.getPlayerData(player).getCurrentFactionData()).showCappingBossBar(player, instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getOpposingFactionId()));
                        instance.getPlayerData(player).setIsCapping(true);
                        instance.getChunkPlayer(player).updatePlayerData(instance.getPlayerData(player));
                        return;
                    }
                }
            }
        }
    }

}
