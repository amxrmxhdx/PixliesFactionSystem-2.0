package me.mickmmars.factions.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class ExplosionListener implements Listener {

    @EventHandler
    public void onExplosion(EntitySpawnEvent event) {
        if (event.getEntityType().equals(EntityType.CREEPER)) {
            event.setCancelled(true);
        }
    }

}
