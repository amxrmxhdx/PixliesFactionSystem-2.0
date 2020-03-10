package me.mickmmars.factions.war.listeners;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.war.WarManager;
import me.mickmmars.factions.war.data.CasusBelli;
import me.mickmmars.factions.war.events.PlayerCappingEvent;
import me.mickmmars.factions.war.events.PlayerCappingFailedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CappingListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void onCap(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        boolean active = true;

        if (instance.getPlayerData(player).isInFaction() && instance.getPlayerData(player).getCurrentFactionData().isInWar()) {
            for (UUID uuid : instance.getPlayerData(player).getCurrentFactionData().listOnlineMembers())
                if (instance.getPlayerData(Bukkit.getPlayer(uuid)).getIsCappingisCapping())
                    active = false;
            if (!instance.inGracePeriod().get(instance.getPlayerData(player).getCurrentFactionData()) && active) {
                if (instance.getPlayerData(player).getCurrentFactionData().isInWar()) {
                    CasusBelli cb = instance.getWarCB().get(instance.getPlayerData(player).getCurrentFactionData());
                    if (event.getFrom().getChunk() != event.getTo().getChunk() && instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getOpposingFactionId()).getCapitalLocation().toBukkitLocation().getChunk() == event.getTo().getChunk() && !instance.getPlayerData(player).getIsCappingisCapping()) {
                        Bukkit.getPluginManager().callEvent(new PlayerCappingEvent(player, new WarManager().getUsed(instance.getPlayerData(player).getCurrentFactionData()), instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getOpposingFactionId())));
                        new WarManager().showCappingBossBar(cb, instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getOpposingFactionId()), player);
                        instance.getPlayerData(player).setIsCapping(true);
                        instance.getChunkPlayer(player).updatePlayerData(instance.getPlayerData(player));
                        return;
                    }
                }
            }
        }
    }

}
