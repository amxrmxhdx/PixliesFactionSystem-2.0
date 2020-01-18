package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.perms.FactionPerms;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChunkProtectionListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // RETURN STATEMENTS
        if (event.getClickedBlock().getChunk().equals(null)) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().isEmpty()) return;
        if (event.getClickedBlock().getType().isAir()) return;
        if (instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getChunk()).equals(null)) return;

        // STAFFMODE EXCEPTION
        if (instance.getStaffmode().contains(player.getUniqueId())) {
            event.setCancelled(false);
            return;
        }

        // SET EVENT FALSE STATEMENTS
        if (instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(event.getClickedBlock().getChunk()))) {
            event.setCancelled(false);
            return;
        }
        if (instance.getPlayerData(player).getCurrentFactionData().equals(instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getChunk())) && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.INTERACT) && event.getClickedBlock().getType().name().contains("door")) {
            event.setCancelled(false);
            return;
        }
        if (BlockStorage.hasBlockInfo(event.getClickedBlock()) && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.SLIMEFUN)) {
            event.setCancelled(false);
            return;
        }

    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        // RETURN STATEMENTS
        if (event.getBlockPlaced().getChunk().equals(null)) return;
        if (event.getBlockPlaced() == null) return;
        if (event.getBlockPlaced().isEmpty()) return;
        if (event.getBlockPlaced().getType().isAir()) return;
        if (instance.getChunkManager().getFactionDataByChunk(event.getBlockPlaced().getChunk()).equals(null)) return;
        if (instance.getStaffmode().contains(player.getUniqueId())) event.setCancelled(false);

        // SET EVENT TRUE
        if (!instance.getPlayerData(player).isInFaction() && !instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(event.getBlockPlaced().getChunk()))) event.setCancelled(true);

        // SET EVENT FALSE
        if (instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(event.getBlockPlaced().getChunk()))) event.setCancelled(false);
        if (instance.getPlayerData(player).getCurrentFactionData().equals(instance.getChunkManager().getFactionDataByChunk(event.getBlockPlaced().getChunk())) && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.BUILD)) event.setCancelled(false);

    }

}
