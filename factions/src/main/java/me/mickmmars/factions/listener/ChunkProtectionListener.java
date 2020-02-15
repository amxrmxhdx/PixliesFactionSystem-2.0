package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.perms.FactionPerms;
import me.mickmmars.factions.message.Message;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChunkProtectionListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // RETURN STATEMENTS
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().isEmpty()) return;
        if (event.getClickedBlock().getType().isAir()) return;
        if (event.getClickedBlock().getChunk().equals(null)) return;
        if (instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getChunk()) == null) return;
        if (instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.BUILD) && instance.getPlayerData(player).isInFaction() && instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getChunk()).equals(instance.getPlayerData(player).getCurrentFactionData())) return;
        if (!instance.getPlayerData(player).isInFaction()) return;
        if (Bukkit.getPluginManager().getPlugin("Slimefun") != null)
            if (BlockStorage.hasBlockInfo(event.getClickedBlock()) && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.SLIMEFUN) && instance.getPlayerData(player).getCurrentFactionData().equals(instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getChunk()))) return;

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
        if (instance.getPlayerData(player).getCurrentFactionData().equals(instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getChunk())) && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.INTERACT) && event.getClickedBlock().getType().isInteractable()) {
            event.setCancelled(false);
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NO_PERMISSION_TO_INTERACT.getMessage()));
        event.setCancelled(true);

    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        // RETURN STATEMENTS
        if (event.getBlockPlaced().getChunk().equals(null)) return;
        if (event.getBlockPlaced() == null) return;
        if (event.getBlockPlaced().isEmpty()) return;
        if (instance.getChunkManager().getFactionDataByChunk(event.getBlock().getChunk()) == null) return;
        if (instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.BUILD) && instance.getPlayerData(player).isInFaction() && instance.getChunkManager().getFactionDataByChunk(event.getBlock().getChunk()).equals(instance.getPlayerData(player).getCurrentFactionData())) return;
        if (Bukkit.getPluginManager().getPlugin("Slimefun") != null)
            if (BlockStorage.hasBlockInfo(event.getBlock()) && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.SLIMEFUN) && instance.getPlayerData(player).getCurrentFactionData().equals(instance.getChunkManager().getFactionDataByChunk(event.getBlock().getChunk()))) return;
        if (instance.getStaffmode().contains(player.getUniqueId())) {
            event.setCancelled(false);
            return;
        }

        // SET EVENT TRUE
        if (!instance.getPlayerData(player).isInFaction() && !instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(event.getBlockPlaced().getChunk()))) {
            event.setCancelled(false);
            return;
        }

        // SET EVENT FALSE
        if (instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(event.getBlockPlaced().getChunk()))) {
            event.setCancelled(false);
            return;
        }
        if (instance.getPlayerData(player).getCurrentFactionData().equals(instance.getChunkManager().getFactionDataByChunk(event.getBlockPlaced().getChunk())) && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.BUILD)) {
            event.setCancelled(false);
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NO_PERMISSION_TO_INTERACT.getMessage()));
        event.setCancelled(true);

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // RETURN STATEMENTS
        if (event.getBlock().getChunk().equals(null)) return;
        if (event.getBlock() == null) return;
        if (event.getBlock().isEmpty()) return;
        if (event.getBlock().getType().isAir()) return;
        if (instance.getChunkManager().getFactionDataByChunk(event.getBlock().getChunk()) == null) return;
        if (instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.BUILD) && instance.getPlayerData(player).isInFaction() && instance.getChunkManager().getFactionDataByChunk(event.getBlock().getChunk()).equals(instance.getPlayerData(player).getCurrentFactionData())) return;
        if (Bukkit.getPluginManager().getPlugin("Slimefun") != null)
            if (BlockStorage.hasBlockInfo(event.getBlock()) && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.SLIMEFUN) && instance.getPlayerData(player).getCurrentFactionData().equals(instance.getChunkManager().getFactionDataByChunk(event.getBlock().getChunk()))) return;
        if (instance.getStaffmode().contains(player.getUniqueId())) {
            event.setCancelled(false);
            return;
        }

        // SET EVENT TRUE
        if (!instance.getPlayerData(player).isInFaction() && !instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(event.getBlock().getChunk()))) {
            event.setCancelled(false);
            return;
        }

        // SET EVENT FALSE
        if (instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(event.getBlock().getChunk()))) {
            event.setCancelled(false);
            return;
        }
        if (instance.getPlayerData(player).getCurrentFactionData().equals(instance.getChunkManager().getFactionDataByChunk(event.getBlock().getChunk())) && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.BUILD)) {
            event.setCancelled(false);
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NO_PERMISSION_TO_INTERACT.getMessage()));
        event.setCancelled(true);

    }

}
