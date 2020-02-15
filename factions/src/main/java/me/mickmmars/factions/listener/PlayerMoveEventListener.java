package me.mickmmars.factions.listener;

import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.Factions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerMoveEventListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void handleMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Chunk chunk = player.getLocation().getChunk();

        if (instance.getAutoClaim().contains(player.getUniqueId())) {
            instance.getAutoClaimChunks().putIfAbsent(player.getUniqueId(), new ArrayList<>());

            List<Chunk> chunks = instance.getAutoClaimChunks().get(player.getUniqueId());

            if (!chunks.contains(chunk)) {
                /*if (!instance.getChunkManager().isFree(chunk)) {
                    player.sendMessage("chunk already claimed");
                    return;
                }
                chunks.add(chunk);
                instance.getAutoClaimChunks().put(player.getUniqueId(), chunks);
                player.sendMessage("added chunk");*/
                if (!(event.getFrom().getChunk() == event.getTo().getChunk())) {
                    if (instance.getChunkManager().getFactionDataByChunk(player.getLocation().getChunk()) != null) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.ALREADY_CLAIMED.getMessage()));
                        return;
                    }
                    if (instance.getPlayerData(player).getCurrentFactionData().getChunks().size() + 1 > instance.getPlayerData(player).getCurrentFactionData().getPower() && !instance.getStaffmode().contains(player.getUniqueId())) {
                        player.sendMessage(Message.NO_CLAIMING_POWER.getMessage());
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
                    int x = event.getTo().getChunk().getX();
                    int z = event.getTo().getChunk().getZ();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col("&a&oChunk claimed")));

                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())))
                        Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_CLAIMED.getMessage().replace("%player%", player.getName()).replace("%location%", x + ", " + z));

                }
            }
        }

        if (instance.getAutoUnclaim().contains(player.getUniqueId())) {
            instance.getAutoUnclaimChunks().putIfAbsent(player.getUniqueId(), new ArrayList<>());

            List<Chunk> chunks = instance.getAutoUnclaimChunks().get(player.getUniqueId());

            if (!chunks.contains(chunk)) {
                /*if (!instance.getChunkManager().isFree(chunk)) {
                    player.sendMessage("chunk already claimed");
                    return;
                }
                chunks.add(chunk);
                instance.getAutoClaimChunks().put(player.getUniqueId(), chunks);
                player.sendMessage("added chunk");*/
                if (!(event.getFrom().getChunk() == event.getTo().getChunk())) {
                    if (instance.getChunkManager().getFactionDataByChunk(chunk) == null || !instance.getChunkManager().getFactionDataByChunk(chunk).equals(instance.getPlayerData(player).getCurrentFactionData()) && !instance.getChunkManager().isFree(chunk) && !instance.getStaffmode().contains(player.getUniqueId())) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.CHUNK_IS_NOT_YOURS.getMessage()));
                        return;
                    }
                    instance.getFactionManager().unclaimChunk(player, player.getLocation().getChunk(), instance.getChunkPlayer(player).getPlayerData().getFactionId());
                    System.out.println("Chunk unclaimed at " + player.getLocation().toString() + " from faction " + instance.getPlayerData(player).getCurrentFactionData().getName());
                    int x = player.getLocation().getChunk().getX();
                    int z = player.getLocation().getChunk().getZ();
                    //Player members = (Player) instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()));
                    for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId())))
                        Bukkit.getPlayer(uuid).sendMessage(Message.PLAYER_UNCLAIMED.getMessage().replace("%player%", player.getName()).replace("%location%", x + ", " + z));
                    int result = 5;
                    EconomyResponse r = Factions.econ.depositPlayer(player, result);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col("&a&oChunk unclaimed")));
                }
            }
        }

        if (instance.getFactionfly().contains(player.getUniqueId())) {
            if (!instance.getPlayerData(player).getCurrentFactionData().getChunks().contains(instance.getChunkManager().getChunkDataByChunk(event.getTo().getChunk()))) {
                player.setAllowFlight(false);
                player.setFlying(false);
            } else {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        }

        if (!instance.getAutoClaim().contains(player.getUniqueId()) || !instance.getAutoUnclaim().contains(player.getUniqueId())) {
            FactionData data = instance.getChunkManager().getFactionDataByChunk(event.getTo().getChunk());

            if (!instance.getChunkManager().isFree(player.getLocation().getChunk()) && data != null) {
                if (instance.getPlayerData(player).isInFaction()) {
                    if (!instance.getChunkPlayer(player.getUniqueId()).isInFactionChunks() || instance.getChunkPlayer(player.getUniqueId()).isInFactionChunks() && instance.getChunkManager().getFactionDataByChunk(event.getFrom().getChunk()) != instance.getChunkManager().getFactionDataByChunk(event.getTo().getChunk())) {
                        if (data.getName() == instance.getPlayerData(player).getCurrentFactionData().getName()) {
                            player.sendTitle("§b" + data.getName(), "§7" + data.getDescription(), 20, 20 * 3, 20);
                            instance.getChunkPlayer(player.getUniqueId()).setInFactionChunks(true);
                        } else if (data.getId().equals("safezone")) {
                            player.sendTitle("§aSafeZone", "§7" + data.getDescription(), 20, 20 * 3, 20);
                            instance.getChunkPlayer(player.getUniqueId()).setInFactionChunks(true);
                        } else if (data.getAllies().contains(instance.getPlayerData(player).getCurrentFactionData().getId())) {
                            player.sendTitle("§d" + data.getName(), "§7" + data.getDescription(), 20, 20 * 3, 20);
                            instance.getChunkPlayer(player.getUniqueId()).setInFactionChunks(true);
                        } else {
                            player.sendTitle("§f" + data.getName(), "§7" + data.getDescription(), 20, 20 * 3, 20);
                            instance.getChunkPlayer(player.getUniqueId()).setInFactionChunks(true);
                            if (Config.INFORM_FACTION_WHEN_FOREIGNER_ENTERS.getData().equals(true)) {
                                for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(data.getId()))) {
                                    if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)))
                                    Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.PLAYER_ENTERED_TERRITORY.getMessageRaw().toString().replace("%player%", player.getName())));
                                }
                            }
                        }
                    }
                } else if (instance.getChunkManager().getFactionDataByChunk(event.getFrom().getChunk()) != instance.getChunkManager().getFactionDataByChunk(event.getTo().getChunk()) && !instance.getPlayerData(player).isInFaction()) {
                    player.sendTitle(data.getName(), "§7" + data.getDescription(), 20, 20 * 3, 20);
                    instance.getChunkPlayer(player.getUniqueId()).setInFactionChunks(true);
                    if (Config.INFORM_FACTION_WHEN_FOREIGNER_ENTERS.getData().equals(true)) {
                        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(data.getId()))) {
                            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid)))
                                Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.PLAYER_ENTERED_TERRITORY.getMessageRaw().toString().replace("%player%", player.getName())));
                        }
                    }
                }
                if (instance.getPlayerData(player).getAccessableChunks().contains(instance.getChunkManager().getChunkDataByChunk(event.getTo().getChunk()))) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aYou have elevated access in this area."));
                }
            } else {
                if (instance.getChunkPlayer(player.getUniqueId()).isInFactionChunks()) {
                    player.sendTitle("§cWilderness", "§7It's dangerous to go out there alone!", 20, 20 * 3, 20);
                    instance.getChunkPlayer(player.getUniqueId()).setInFactionChunks(false);
                }
            }
        }
    }
}
