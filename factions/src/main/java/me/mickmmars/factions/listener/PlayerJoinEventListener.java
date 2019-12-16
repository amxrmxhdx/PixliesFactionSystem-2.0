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

        boolean exists = instance.getChunkPlayer(player).createIfNotExists();

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
            String factionName = instance.getPlayerData(player).getCurrentFactionData().getName();
            FactionData factionData = instance.getFactionManager().getFactionByName(factionName);
            StringJoiner sj = new StringJoiner("§8, ");
            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(factionData)) {
                Player member = Bukkit.getPlayer(uuid);
                if (Bukkit.getServer().getOnlinePlayers().contains(member)) {
                    sj.add(instance.getPlayerData(member).getFactionRank().getPrefix() + member.getName());
                }
            }
            int chunks = instance.getPlayerData(player).getCurrentFactionData().getChunks().size();
            player.sendMessage("§8-§b+§8---------------------------------------------§b+§8-");
            player.sendMessage("§7Faction: §b" + instance.getPlayerData(player).getCurrentFactionData().getName());
            player.sendMessage("§7Land: §b" + chunks + "§7/§b" + instance.getPlayerData(player).getCurrentFactionData().getMaxPower());
            player.sendMessage("§7Factionbank: §2§l$§a" + instance.getPlayerData(player).getCurrentFactionData().getMoney());
            player.sendMessage("§7Total landcost: §2§l$§a" + chunks * 5);
            player.sendMessage("§7Online members: " + sj.toString());
            player.sendMessage("§8-§b+§8---------------------------------------------§b+§8-");
        }
    }

}
