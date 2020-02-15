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
            if (factions.getChunks().contains(instance.getChunkManager().getChunkDataByChunk(e.getEntity().getLocation().getChunk())) && !factions.getAllowedFlags().contains(FactionFlag.EXPLOSIONS.getName())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        for (FactionData factions : instance.getFactionManager().getFactions()) {
            if (!(e.getDamager() instanceof Player)) return;
            if (!(e.getEntity() instanceof Player)) return;
            if (factions.getChunks().contains(instance.getChunkManager().getChunkDataByChunk(e.getEntity().getLocation().getChunk())) && !factions.getAllowedFlags().contains(FactionFlag.PVP.getName())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFriendlyFire(EntityDamageByEntityEvent e) {
        for (FactionData factions : instance.getFactionManager().getFactions()) {
            if (e.getEntityType().equals(EntityType.PLAYER) && e.getDamager().getType().equals(EntityType.PLAYER)) {
                if (instance.getFactionManager().getMembersFromFaction(factions).contains(e.getDamager().getUniqueId()) && instance.getFactionManager().getMembersFromFaction(factions).contains(e.getEntity().getUniqueId()) && !factions.getAllowedFlags().contains(FactionFlag.FRIENDLYFIRE.getName())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onMonsterSpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof Monster)) return;
        for (FactionData factions : instance.getFactionManager().getFactions()) {
            if (factions.getChunks().contains(instance.getChunkManager().getChunkDataByChunk(e.getLocation().getChunk())) && !factions.getAllowedFlags().contains(FactionFlag.MONSTER.getName())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAnimalSpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof Animals)) return;
        for (FactionData factions : instance.getFactionManager().getFactions()) {
            if (factions.getChunks().contains(instance.getChunkManager().getChunkDataByChunk(e.getLocation().getChunk())) && !factions.getAllowedFlags().contains(FactionFlag.ANIMALS.getName())) {
                e.setCancelled(true);
            }
        }
    }

}
