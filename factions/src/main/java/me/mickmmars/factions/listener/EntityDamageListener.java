package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    Factions instance = Factions.getInstance();

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (instance.getFactionManager().getFactionByName("SafeZone").getChunks().contains(instance.getChunkManager().getChunkDataByChunk(event.getEntity().getLocation().getChunk()))) {
            event.setCancelled(false);
        }
    }

}
