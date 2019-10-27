package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.rank.FactionRank;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void handleChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();

        if (event.getMessage().equalsIgnoreCase("claim")) {
            event.setCancelled(true);
            if (!instance.getChunkManager().isFree(player.getLocation().getChunk())) {
                player.sendMessage(Factions.col("&8» &c&oThis chunk is already claimed!"));
                return;
            }
            if (!instance.getChunkPlayer(player).getPlayerData().isInFaction() && FactionRank.getRankId(instance.getChunkPlayer(player).getPlayerData().getFactionRank()) < 2) {
                player.sendMessage(Factions.col("&8» &c&oInsuffiecent faction-permissions."));
                return;
            }
            instance.getFactionManager().claimChunk(player, player.getLocation().getChunk(), instance.getChunkPlayer(player).getPlayerData().getFactionId());
            System.out.println("Chunk claimed at " + player.getLocation().toString() + " for player " + player.getName());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col("&a&oChunk claimed")));
        } else if (event.getMessage().equalsIgnoreCase("info")) {
            event.setCancelled(true);
            if (instance.getChunkManager().isFree(player.getLocation().getChunk())) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col("&7&oChunk is free")));

            } else if (!instance.getChunkManager().isFree(player.getLocation().getChunk())) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col("&7&oChunk is already claimed")));
            }
        }
    }
}
