package me.mickmmars.factions.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mickmmars.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Placeholders extends PlaceholderExpansion {

    private Factions plugin;

    public Placeholders(Factions plugin) { this.plugin = plugin; }

	public boolean canRegister(){
		return true;
	}

	public boolean persist() { return true; }

    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    public String getIdentifier() {
        return "factions";
    }

    public String getVersion() {
        return "1.0";
    }

    public String onPlaceholderRequest(Player p, String identifier) {
        switch (identifier) {
            case "name":
                if (plugin.getPlayerData(p).isInFaction()) {
                    return plugin.getPlayerData(p).getCurrentFactionData().getName();
                } else {
                    return "§cNONE";
                }

            case "description":
                if (plugin.getPlayerData(p).isInFaction()) {
                    return plugin.getPlayerData(p).getCurrentFactionData().getDescription();
                } else {
                    return " ";
                }

            case "balance":
                if (plugin.getPlayerData(p).isInFaction()) {
                    return "§2§l$§a" + String.valueOf(plugin.getPlayerData(p).getCurrentFactionData().getMoney());
                } else {
                    return " ";
                }

            case "maxpower":
                if (plugin.getPlayerData(p).isInFaction()) {
                    return String.valueOf(plugin.getPlayerData(p).getCurrentFactionData().getMaxPower());
                }
                break;

            case "chunk_free":
                if (!plugin.getChunkManager().isFree(p.getLocation().getChunk())) {
                    return "§c§lClaimed";
                } else {
                    return "§a§lFree";
                }

            case "territory":
                if (!(plugin.getChunkManager().isFree(p.getLocation().getChunk()))) {
                    return plugin.getFactionManager().getRelColour(plugin.getPlayerData(p).getFactionId(), plugin.getChunkManager().getFactionDataByChunk(p.getLocation().getChunk()).getId()) + plugin.getChunkManager().getFactionDataByChunk(p.getLocation().getChunk()).getName();
                } else {
                    return "§cWILDERNESS";
                }

            case "power":
                if (plugin.getPlayerData(p).isInFaction()) {
                    return String.valueOf(plugin.getPlayerData(p).getCurrentFactionData().getChunks().size());
                } else {
                    return " ";
                }

            case "onlinemembers":
                List<UUID> onlinemembers = new ArrayList<UUID>();
                for (UUID uuid : plugin.getFactionManager().getMembersFromFaction(plugin.getPlayerData(p).getCurrentFactionData())) {
                    if (Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getPlayer(uuid))) {
                        onlinemembers.add(uuid);
                    }
                }
                if (plugin.getPlayerData(p).isInFaction()) {
                    return String.valueOf(onlinemembers.size());
                } else {
                    return " ";
                }

            case "rankprefix":
                return plugin.getPlayerData(p).getFactionRank().getPrefix();

        }
        return " ";
    }

}
