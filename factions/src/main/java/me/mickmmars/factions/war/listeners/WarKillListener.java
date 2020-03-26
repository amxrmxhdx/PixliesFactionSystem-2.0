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

        if (target instanceof Player && killer instanceof Player && instance.getPlayerData(target).isInFaction() && instance.getPlayerData(target).getCurrentFactionData().isInWar() && instance.getPlayerData(killer).isInFaction() && instance.getPlayerData(target).getCurrentFactionData().isInWar()) {
            instance.getWarFactions().get(instance.getPlayerData(killer).getCurrentFactionData()).getKills().put(instance.getPlayerData(killer).getCurrentFactionData(), instance.getWarFactions().get(instance.getPlayerData(killer).getCurrentFactionData()).getKills().get(instance.getPlayerData(killer).getCurrentFactionData()) + 1);
        }

    }

}
