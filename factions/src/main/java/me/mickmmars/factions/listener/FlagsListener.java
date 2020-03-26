package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.flags.FactionFlag;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class FlagsListener implements Listener {

    private static Factions instance = Factions.getInstance();

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent e) {
        for (FactionData factions : instance.getFactionManager().getFactions()) {
            if (factions.getChunks().containsKey(instance.getChunkManager().getChunkDataByChunk(e.getEntity().getLocation().getChunk())) && !factions.getAllowedFlags().contains(FactionFlag.EXPLOSIONS.getName())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMonsterSpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof Monster)) return;
        for (FactionData factions : instance.getFactionManager().getFactions()) {
            if (factions.getChunks().containsKey(instance.getChunkManager().getChunkDataByChunk(e.getLocation().getChunk())) && !factions.getAllowedFlags().contains(FactionFlag.MONSTER.getName())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAnimalSpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof Animals)) return;
        for (FactionData factions : instance.getFactionManager().getFactions()) {
            if (factions.getChunks().containsKey(instance.getChunkManager().getChunkDataByChunk(e.getLocation().getChunk())) && !factions.getAllowedFlags().contains(FactionFlag.ANIMALS.getName())) {
                e.setCancelled(true);
            }
        }
    }

}
