package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.player.ChunkPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.DecimalFormat;

public class PlayerJoinEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        instance.getPlayers().add(new ChunkPlayer(player.getUniqueId()));

        boolean exists = instance.getChunkPlayer(player).createIfNotExists();

        if(!instance.getChunkPlayer(player).getPlayerData().getFactionInvites().isEmpty()) {
            String amount = new DecimalFormat().format(instance.getChunkPlayer(player).getPlayerData().getFactionInvites().size()).replace(",", ".");

            player.sendMessage("§ayou have §b" + amount + " §ainvites open");
        }
    }

}
