package me.mickmmars.factions.war.listeners;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class WarHitListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {

            Player damager = (Player) event.getDamager();
            Player target = (Player) event.getEntity();

            if (!(instance.getPlayerData(damager).isInFaction() && instance.getPlayerData(target).isInFaction())) return;
            if (!(instance.getPlayerData(damager).getCurrentFactionData().isInWar() && instance.getPlayerData(target).getCurrentFactionData().isInWar())) return;
            if (!(instance.getPlayerData(damager).getCurrentFactionData().getOpposingFactionId().equals(instance.getPlayerData(target).getCurrentFactionData().getId()))) return;

            if (instance.getWarFactions().get(instance.getPlayerData(damager).getCurrentFactionData()).inGracePeriod()) {
                event.setCancelled(true);
                damager.sendMessage(Message.NO_HITTING_IN_GRACE.getMessage());
                return;
            }

        }

    }

}
