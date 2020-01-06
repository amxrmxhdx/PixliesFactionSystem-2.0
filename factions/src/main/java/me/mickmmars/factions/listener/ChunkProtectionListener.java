package me.mickmmars.factions.listener;

import me.mickmmars.factions.factions.perms.FactionPerms;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.Factions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChunkProtectionListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void handleBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        if (instance.getChunkManager().getChunkDataByChunk(chunk) == null) {
            event.setCancelled(false);
            return;
        }
        if (!instance.getChunkManager().getFactionDataByChunk(chunk).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName()) && !instance.getStaffmode().contains(player.getUniqueId()) || !instance.getPlayerData(player).isInFaction() && !instance.getStaffmode().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.closeInventory();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NO_PERMISSION_TO_INTERACT.getMessageRaw().toString()));
            return;
        }
        if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.BUILD)) {
            event.setCancelled(true);
            player.closeInventory();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col(Message.NOPERM_INTERACT_FAC.getMessageRaw().toString())));
            return;
        }
        event.setCancelled(false);

    }

    @EventHandler
    public void handlePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        if (instance.getChunkManager().getChunkDataByChunk(chunk) == null) {
            event.setCancelled(false);
            return;
        }
        if (!instance.getChunkManager().getFactionDataByChunk(chunk).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName()) && !instance.getStaffmode().contains(player.getUniqueId()) || !instance.getPlayerData(player).isInFaction() && !instance.getStaffmode().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.closeInventory();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NO_PERMISSION_TO_INTERACT.getMessageRaw().toString()));
            return;
        }
        if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.BUILD) && instance.getPlayerData(player).getCurrentFactionData().equals(instance.getChunkManager().getFactionDataByChunk(chunk))) {
            event.setCancelled(true);
            player.closeInventory();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col(Message.NOPERM_INTERACT_FAC.getMessageRaw().toString())));
            return;
        }
        event.setCancelled(false);

    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getMaterial().isInteractable() && instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getLocation().getChunk()) == null) return;
        if (event.getClickedBlock() == null) return;
        if (!event.getMaterial().isAir()) return;
        if (event.getMaterial().isInteractable() && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.INTERACT) && (instance.getPlayerData(player).getCurrentFactionData() == instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getLocation().getChunk())) && instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getLocation().getChunk()) != null || instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getLocation().getChunk()) == null) {
            event.setCancelled(false);
            return;
        }
        if (event.isBlockInHand() && instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.BUILD) && (instance.getPlayerData(player).getCurrentFactionData() == instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getLocation().getChunk())) && instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getLocation().getChunk()) != null || instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getLocation().getChunk()) == null) {
            event.setCancelled(false);
            return;
        }
        if (instance.getChunkManager().getFactionDataByChunk(event.getClickedBlock().getLocation().getChunk()) != instance.getPlayerData(player).getCurrentFactionData() && !instance.getStaffmode().contains(player.getUniqueId())) event.setCancelled(true);
        if (!(event.getItem() instanceof Block)) return;
        if (!event.getMaterial().isAir()) {
            Chunk chunk = event.getClickedBlock().getLocation().getChunk();
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (instance.getChunkManager().getFactionDataByChunk(chunk) == null) {
                    event.setCancelled(false);
                    return;
                }
                if (instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(chunk))) {
                    event.setCancelled(false);
                    return;
                }
                if (!instance.getPlayerData(player).isInFaction() && instance.getChunkManager().getFactionDataByChunk(chunk) != null && !instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(chunk))) {
                    event.setCancelled(true);
                    player.sendMessage(Message.NO_PERMISSION_TO_INTERACT.getMessage());
                    return;
                }
                if (!instance.getChunkManager().getFactionDataByChunk(chunk).equals(instance.getPlayerData(player).getCurrentFactionData()) && !instance.getStaffmode().contains(player.getUniqueId()) || !instance.getPlayerData(player).isInFaction() && !instance.getStaffmode().contains(player.getUniqueId()) && instance.getChunkManager().getFactionDataByChunk(chunk) != null) {
                    event.setCancelled(true);
                    player.closeInventory();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NO_PERMISSION_TO_INTERACT.getMessageRaw().toString()));
                    return;
                }
                if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.INTERACT) && instance.getChunkManager().getFactionDataByChunk(chunk) == instance.getPlayerData(player).getCurrentFactionData()) {
                    event.setCancelled(true);
                    player.closeInventory();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col(Message.NOPERM_INTERACT_FAC.getMessageRaw().toString())));
                    return;
                }
                event.setCancelled(false);
            }

            if (event.getAction().equals(Action.PHYSICAL)) {
                if (instance.getChunkManager().getChunkDataByChunk(chunk) == null) {
                    event.setCancelled(false);
                    return;
                }
                if (instance.getChunkManager().getFactionDataByChunk(chunk) == null) {
                    event.setCancelled(true);
                    return;
                }
                if (instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(chunk))) {
                    event.setCancelled(false);
                    return;
                }
                if (!instance.getPlayerData(player).isInFaction() && instance.getChunkManager().getFactionDataByChunk(chunk) != null && !instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(chunk))) {
                    event.setCancelled(true);
                    player.sendMessage(Message.NO_PERMISSION_TO_INTERACT.getMessage());
                    return;
                }
                if (!instance.getChunkManager().getFactionDataByChunk(chunk).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName()) && !instance.getStaffmode().contains(player.getUniqueId()) || !instance.getPlayerData(player).isInFaction() && !instance.getStaffmode().contains(player.getUniqueId())) {
                    event.setCancelled(true);
                    player.closeInventory();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NO_PERMISSION_TO_INTERACT.getMessageRaw().toString()));
                    return;
                }
                if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.INTERACT) && instance.getPlayerData(player).getCurrentFactionData().equals(instance.getChunkManager().getFactionDataByChunk(chunk))) {
                    event.setCancelled(true);
                    player.closeInventory();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col(Message.NOPERM_INTERACT_FAC.getMessageRaw().toString())));
                    return;
                }
                event.setCancelled(false);
            }
        }
    }

}
