package me.mickmmars.factions.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.rank.FactionRank;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.player.ChunkPlayer;
import me.mickmmars.factions.Factions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class PlayerChatEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void handleChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();

        if (instance.getCreatefactiongui().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.performCommand("/f create " + event.getMessage());
            instance.getCreatefactiongui().remove(player.getUniqueId());
        }

        if (Config.USE_INTEGRATED_CHAT.getData().equals(true)) {
            String message = ChatColor.translateAlternateColorCodes('&', event.getMessage()).replace("%", "%%");
            if (instance.getPlayerData(player).isInFaction()) {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    event.setFormat(PlaceholderAPI.setPlaceholders(event.getPlayer(), Config.CHATFORMAT_WITHFACTION.getData().toString().replace("%prefix%", instance.getChunkPlayer(player).getRankPrefix()).replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()).replace("%rank%", "").replace("%player%", player.getName()).replace("%message%", message)));
                } else {
                    event.setFormat(Config.CHATFORMAT_WITHFACTION.getData().toString().replace("%prefix%", instance.getChunkPlayer(player).getRankPrefix()).replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()).replace("%rank%", "").replace("%player%", player.getName()).replace("%message%", message));
                }
            } else {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    event.setFormat(PlaceholderAPI.setPlaceholders(event.getPlayer(), Config.CHATFORMAT_NOFACTION.getData().toString().replace("%player%", player.getName()).replace("%rank%", "").replace("%message%", message)));
                } else {
                    event.setFormat(Config.CHATFORMAT_NOFACTION.getData().toString().replace("%player%", player.getName()).replace("%rank%", "").replace("%message%", message));
                }
            }
        }

        if (event.getMessage().equalsIgnoreCase(Config.CHAT_FCLAIM.getData().toString()) && Config.USE_CHAT_TO_CLAIM.getData().equals(true)) {
            event.setCancelled(true);
            if (!instance.getChunkManager().isFree(player.getLocation().getChunk())) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.ALREADY_CLAIMED.getMessage()));
                return;
            }
            if (FactionRank.getRankId(instance.getChunkPlayer(player).getPlayerData().getFactionRank()) < 2) {
                player.sendMessage(Message.NO_CLAIM_PERM.getMessage());
                return;
            }
            if (!instance.getPlayerData(player).isInFaction()) {
                player.sendMessage(Message.NOT_IN_A_FACTION.getMessage());
                return;
            }
            FactionData factionData = instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId());
            int price = (instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()).getChunks().size() >= 100 ? 5 * instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())).size() : 5);
            if (!(factionData.getMoney() >= price)) {
                int need = (price - instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()).getMoney());
                String needString = instance.asDecimal(need);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.NEED_MORE_TO_CLAIM.getMessage().replace("%need%", needString)));
                return;
            }
            instance.getFactionManager().claimChunk(player, player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), instance.getChunkPlayer(player).getPlayerData().getFactionId(), false);
            System.out.println("Chunk claimed at " + player.getLocation().toString() + " for player " + player.getName());
            int x = player.getLocation().getChunk().getX();
            int z = player.getLocation().getChunk().getZ();
            //Player members = (Player) instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()));
            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())))
                Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CLAIMED.getMessage().replace("%player%", player.getName()).replace("%location%", x + ", " + z));

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col("&a&oChunk claimed")));
        } else if (event.getMessage().equalsIgnoreCase(Config.CHAT_FINFO.getData().toString()) && Config.USE_CHAT_TO_CLAIM.getData().equals(true)) {
            FactionData factionData = instance.getChunkManager().getFactionDataByChunk(player.getLocation().getChunk());
            player.sendMessage(factionData.getName());
            String owner = "None";
            for (UUID registeredPlayer : instance.getRegisteredPlayers()) {
                ChunkPlayer chunkPlayer = (instance.getChunkPlayer(registeredPlayer) == null ? new ChunkPlayer(registeredPlayer) : instance.getChunkPlayer(registeredPlayer));
                if (chunkPlayer.getPlayerData().getFactionId().equals(factionData.getId()) && chunkPlayer.getPlayerData().getFactionRank().equals(FactionRank.LEADER))
                    owner = registeredPlayer.toString();
            }
            player.sendMessage("Owner: " + owner);

            event.setCancelled(true);
            if (instance.getChunkManager().isFree(player.getLocation().getChunk())) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.CHUNK_IS_FREE.getMessage()));
                TextComponent claim = new TextComponent(Message.CLAIM_QUESTIONMARK.getMessage());
                claim.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Message.CLICK_ME_TO_CLAIM.getMessage()).create()));
                claim.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "fclaim"));
                player.spigot().sendMessage(claim);
            } else if (!instance.getChunkManager().isFree(player.getLocation().getChunk())) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.ALREADY_CLAIMED.getMessage()));
            }
        }
        if (instance.getFactionChatPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())))
                Bukkit.getPlayer(uuid).sendMessage(Config.FACTIONCHAT_FORMAT.getData().toString().replace("%prefix%", instance.getChunkPlayer(uuid).getRankPrefix()).replace("%name%", player.getDisplayName()).replace("%message%", event.getMessage()));
            for (UUID uuid : instance.getStaffmode())
                Bukkit.getPlayer(uuid).sendMessage(Config.FACTIONCHAT_SPY_FORMAT.getData().toString().replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()).replace("%prefix%", instance.getChunkPlayer(uuid).getRankPrefix()).replace("%name%", player.getDisplayName()).replace("%message%", event.getMessage()));
        } else if (instance.getAllyChat().contains(player.getUniqueId())) {
            event.setCancelled(true);
            for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())))
                Bukkit.getPlayer(uuid).sendMessage(Config.ALLYCHAT_FORMAT.getData().toString().replace("%prefix%", instance.getChunkPlayer(uuid).getRankPrefix()).replace("%name%", player.getDisplayName()).replace("%message%", event.getMessage()).replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()));
            for (String s : instance.getPlayerData(player).getCurrentFactionData().getAllies()) {
                for (UUID uuid : instance.getFactionManager().getFactionById(s).getOnlineMembers())
                    Bukkit.getPlayer(uuid).sendMessage(Config.ALLYCHAT_FORMAT.getData().toString().replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()).replace("%prefix%", instance.getChunkPlayer(uuid).getRankPrefix()).replace("%name%", player.getDisplayName()).replace("%message%", event.getMessage()).replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()));
            }
            for (UUID uuid : instance.getStaffmode())
                Bukkit.getPlayer(uuid).sendMessage(Config.FACTIONCHAT_SPY_FORMAT.getData().toString().replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()).replace("%prefix%", instance.getChunkPlayer(uuid).getRankPrefix()).replace("%name%", player.getDisplayName()).replace("%message%", event.getMessage()).replace("%faction%", instance.getPlayerData(player).getCurrentFactionData().getName()));
        }
    }
}
