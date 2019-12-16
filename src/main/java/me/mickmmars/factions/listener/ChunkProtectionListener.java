package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.perms.FactionPerms;
import me.mickmmars.factions.message.Message;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

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
        if (!instance.getChunkManager().getFactionDataByChunk(chunk).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName()) && !instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.BUILD) && !instance.getStaffmode().contains(player.getUniqueId()) || !instance.getPlayerData(player).isInFaction() && !instance.getStaffmode().contains(player.getUniqueId())) {
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
    public void handleInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getMaterial().isAir()) {
            Chunk chunk = event.getClickedBlock().getLocation().getChunk();
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
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
                if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.INTERACT)) {
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
                if (event.getClickedBlock().getChunk().equals(null)) {
                    event.setCancelled(true);
                    return;
                }
                if (!instance.getChunkManager().getFactionDataByChunk(chunk).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName()) && !instance.getStaffmode().contains(player.getUniqueId()) || !instance.getPlayerData(player).isInFaction() && !instance.getStaffmode().contains(player.getUniqueId())) {
                    event.setCancelled(true);
                    player.closeInventory();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NO_PERMISSION_TO_INTERACT.getMessageRaw().toString()));
                    return;
                }
                if (!instance.getFactionManager().checkForPlayergroupPermission(player, FactionPerms.INTERACT)) {
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
