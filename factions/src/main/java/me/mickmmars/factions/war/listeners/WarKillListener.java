package me.mickmmars.factions.war.listeners;

import me.mickmmars.factions.Factions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WarKillListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player target = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (instance.getPlayerData(target).isInFaction() && instance.getPlayerData(target).getCurrentFactionData().isInWar() && instance.getPlayerData(killer).isInFaction() && instance.getPlayerData(target).getCurrentFactionData().isInWar()) {
            instance.getWarKills().put(instance.getPlayerData(killer).getCurrentFactionData(), instance.getWarKills().get(instance.getPlayerData(killer).getCurrentFactionData()) + 1);
        }

    }

}
