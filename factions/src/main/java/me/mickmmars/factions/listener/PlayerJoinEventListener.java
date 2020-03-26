package me.mickmmars.factions.listener;

import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.player.ChunkPlayer;
import me.mickmmars.factions.Factions;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.DecimalFormat;
import java.util.StringJoiner;
import java.util.UUID;

public class PlayerJoinEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    private Economy economy;

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        instance.getPlayers().add(new ChunkPlayer(player.getUniqueId()));

        if (player.hasPermission("factions.host")) {
            if (Config.AUTO_UPDATE.getData().equals(true)) {
                player.sendMessage("§7§oThank you for using §b§lPixlies§fFaction§cSystem§7! \n §7No updates available...");
            }
        }

        if(!instance.getChunkPlayer(player).getPlayerData().getFactionInvites().isEmpty()) {
            String amount = new DecimalFormat().format(instance.getChunkPlayer(player).getPlayerData().getFactionInvites().size()).replace(",", ".");

            player.sendMessage("§aYou have §b" + amount + " §ainvites open");
        }

        if (instance.getPlayerData(player).isInFaction()) {
            instance.getFactionManager().sendPlayerFactionInfo(player, instance.getPlayerData(player).getCurrentFactionData());
        }
    }

}
