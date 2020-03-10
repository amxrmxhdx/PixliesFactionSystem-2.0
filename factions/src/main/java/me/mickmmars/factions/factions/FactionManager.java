package me.mickmmars.factions.factions;

import com.google.gson.Gson;
import io.papermc.lib.PaperLib;
import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.chunk.location.ChunkLocation;
import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.perms.ForeignFactionData;
import me.mickmmars.factions.factions.upgrades.FactionUpgrades;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.player.data.PlayerData;
import me.mickmmars.factions.publicwarps.data.WarpData;
import me.mickmmars.factions.util.ItemBuilder;
import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.flags.FactionFlag;
import me.mickmmars.factions.factions.perms.FactionPerms;
import me.mickmmars.factions.factions.rank.FactionRank;
import me.mickmmars.factions.factions.relations.FactionRelations;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.factions.DynmapFactionsPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FactionManager {

    private Factions instance = Factions.getInstance();

    private final File file;
    private final YamlConfiguration cfg;

    private final String[] randomDescriptions = {
            "you nice!",
            "im the best",
            "you not cool",
            "No description :("
    };

    private final List<FactionData> factions;

    public FactionManager() {
        file = new File("plugins/Factions/factions.yml");
        cfg = YamlConfiguration.loadConfiguration(file);

        factions = new ArrayList<>();
    }

    public void loadFactions() {
        for (String s : cfg.getStringList("Factions")) {
            final FactionData data = new Gson().fromJson(s, FactionData.class);
            if (getFactionByName(data.getName()) == null)
                factions.add(data);
        }
    }

    public UUID getLeader(FactionData faction) {
        UUID owner = null;
        for (UUID uuid : getMembersFromFaction(faction)) {
            if (instance.getPlayerData(uuid).getFactionRank().equals(FactionRank.LEADER)) {
                owner = uuid;
            }
        }
        return owner;
    }

    public void addUpgradeToFaction(FactionData faction, FactionUpgrades upgrade) {
        if (faction.getUpgrades().contains(upgrade.getName().toUpperCase())) {
            return;
        }
        List<String> upgrades = new ArrayList<String>(faction.getUpgrades());
        upgrades.add(upgrade.getName().toUpperCase());
        faction.setUpgrades(upgrades);
        updateFactionData(faction);
    }

    public void addPlayerApplication(Player player, FactionData data) {
        if (data.getApplications().contains(player.getUniqueId().toString())) {
            return;
        }
        List<String> applications = new ArrayList<String>(data.getApplications());
        applications.add(player.getUniqueId().toString());
        data.setApplications(applications);
        updateFactionData(data);
    }

    public Boolean hasPurchasedUpgrade(FactionData faction, FactionUpgrades upgrade) {
        if (faction.getUpgrades().contains(upgrade.getName().toUpperCase())) {
            return true;
        } else {
            return false;
        }
    }

    public void setPublicWarp(Player player, String name) {
        WarpData location = new WarpData(name, new ChunkLocation(player.getLocation()));
        List<WarpData> warps = new ArrayList<WarpData>(instance.getPlayerData(player).getCurrentFactionData().getWarps());
        if (instance.getPlayerData(player).getCurrentFactionData().getWarps().contains(location)) {
            return;
        }
        warps.add(location);
        instance.getPlayerData(player).getCurrentFactionData().setWarps(warps);
        updateFactionData(instance.getPlayerData(player).getCurrentFactionData());
    }

    public void removePublicWarp(Player player, String name) {
        WarpData location = getWarpByName(name, instance.getPlayerData(player).getCurrentFactionData());
        List<WarpData> warps = new ArrayList<WarpData>(instance.getPlayerData(player).getCurrentFactionData().getWarps());
        if (instance.getPlayerData(player).getCurrentFactionData().getWarps().contains(location)) {
            return;
        }
        warps.remove(location);
        instance.getPlayerData(player).getCurrentFactionData().setWarps(warps);
        updateFactionData(instance.getPlayerData(player).getCurrentFactionData());
    }

    public WarpData getWarpByName(String name, FactionData data) {
        for (WarpData warp : data.getWarps())
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        return null;
    }

    public void removePlayerApplication(Player player, FactionData data) {
        if (!data.getApplications().contains(player.getUniqueId().toString())) {
            return;
        }
        List<String> applications = new ArrayList<String>(data.getApplications());
        applications.remove(player.getUniqueId().toString());
        data.setApplications(applications);
        updateFactionData(data);
    }

    public String getRelColour(String id1, String id2) {
        if (id1.equals(id2)){
            return FactionRelations.SELF.getColour();
        } else if (instance.getFactionManager().getFactionById(id1).getAllies().contains(id2)) {
            return FactionRelations.ALLY.getColour();
        } else if (instance.getFactionManager().getFactionById(id1).getEnemies().contains(id2)) {
            return FactionRelations.ENEMY.getColour();
        } else {
            return FactionRelations.NEUTRAL.getColour();
        }
    }

    private List<FactionPerms> perms;

    private List<FactionData> allies;
    private List<FactionData> allyrequests;

    public void createNewFaction(FactionData data) throws IOException {
        factions.add(data);

        final List<String> factions = cfg.getStringList("Factions");
        factions.add(instance.getPrettyGson().toJson(new Gson().toJsonTree(data)));
        cfg.set("Factions", factions);
        cfg.save(file);
    }

    public void addAllyRequest(String factionId, FactionData factionData) {
        FactionData factiondata = factionData;
        List<String> allyrequests = factionData.getAllyRequests();
        allyrequests.add(factionId);
        factiondata.setAllyRequests(allyrequests);
        updateFactionData(factionData);
    }

    public void removeAllyRequest(String FactionId, FactionData factionData ) {
        FactionData factiondata = factionData;
        List<String> allyrequests = factionData.getAllyRequests();
        allyrequests.remove(FactionId);
        factionData.setAllyRequests(allyrequests);
        updateFactionData(factionData);

    }

    public void Ally(String faction1Id, String faction2Id, FactionData faction1Data, FactionData faction2Data) {
        List<String> alliesreq2 = faction2Data.getAllyRequests();
        List<String> alliesreq1 = faction2Data.getAllyRequests();
        List<String> allies2 = faction2Data.getAllies();
        List<String> allies1 = faction1Data.getAllies();
        alliesreq1.remove(faction2Id);
        alliesreq2.remove(faction1Id);
        allies2.add(faction1Id);
        allies1.add(faction2Id);
        faction1Data.setAllyRequests(alliesreq1);
        faction2Data.setAllyRequests(alliesreq2);
        faction1Data.setAllies(allies1);
        faction2Data.setAllies(allies2);
        updateFactionData(faction1Data);
        updateFactionData(faction2Data);
    }

    public void fillClaimPlayerRadius(Player player) {
        Location ploc = player.getLocation();
        List<Chunk> lines = new ArrayList<Chunk>();
        Chunk line1chunk9 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 4, ploc.getChunk().getZ() - 2);
        Chunk line1chunk8 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 3, ploc.getChunk().getZ() - 2);
        Chunk line1chunk7 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 2, ploc.getChunk().getZ() - 2);
        Chunk line1chunk6 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ() - 2);
        Chunk line1chunk5 = ploc.getWorld().getChunkAt(ploc.getChunk().getX(), ploc.getChunk().getZ() - 2);
        Chunk line1chunk4 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ() - 2);
        Chunk line1chunk3 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 2, ploc.getChunk().getZ() - 2);
        Chunk line1chunk2 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 3, ploc.getChunk().getZ() - 2);
        Chunk line1chunk1 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 4, ploc.getChunk().getZ() - 2);
        lines.add(line1chunk1);
        lines.add(line1chunk2);
        lines.add(line1chunk3);
        lines.add(line1chunk4);
        lines.add(line1chunk5);
        lines.add(line1chunk6);
        lines.add(line1chunk7);
        lines.add(line1chunk8);
        lines.add(line1chunk9);
        Chunk line2chunk9 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 4, ploc.getChunk().getZ() - 1);
        Chunk line2chunk8 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 3, ploc.getChunk().getZ() - 1);
        Chunk line2chunk7 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 2, ploc.getChunk().getZ() - 1);
        Chunk line2chunk6 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ() - 1);
        Chunk line2chunk5 = ploc.getWorld().getChunkAt(ploc.getChunk().getX(), ploc.getChunk().getZ() - 1);
        Chunk line2chunk4 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ() - 1);
        Chunk line2chunk3 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 2, ploc.getChunk().getZ() - 1);
        Chunk line2chunk2 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 3, ploc.getChunk().getZ() - 1);
        Chunk line2chunk1 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 4, ploc.getChunk().getZ() - 1);
        lines.add(line2chunk1);
        lines.add(line2chunk2);
        lines.add(line2chunk3);
        lines.add(line2chunk4);
        lines.add(line2chunk5);
        lines.add(line2chunk6);
        lines.add(line2chunk7);
        lines.add(line2chunk8);
        lines.add(line2chunk9);
        Chunk line3chunk9 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 4, ploc.getChunk().getZ());
        Chunk line3chunk8 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 3, ploc.getChunk().getZ());
        Chunk line3chunk7 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 2, ploc.getChunk().getZ());
        Chunk line3chunk6 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ());
        Chunk playerchunk = ploc.getBlock().getChunk();
        Chunk line3chunk4 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ());
        Chunk line3chunk3 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 2, ploc.getChunk().getZ());
        Chunk line3chunk2 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 3, ploc.getChunk().getZ());
        Chunk line3chunk1 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 4, ploc.getChunk().getZ());
        lines.add(line3chunk1);
        lines.add(line3chunk2);
        lines.add(line3chunk3);
        lines.add(line3chunk4);
        lines.add(playerchunk);
        lines.add(line3chunk6);
        lines.add(line3chunk7);
        lines.add(line3chunk8);
        lines.add(line3chunk9);
        Chunk line4chunk9 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 4, ploc.getChunk().getZ() + 1);
        Chunk line4chunk8 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 3, ploc.getChunk().getZ() + 1);
        Chunk line4chunk7 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 2, ploc.getChunk().getZ() + 1);
        Chunk line4chunk6 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ() + 1);
        Chunk line4chunk5 = ploc.getWorld().getChunkAt(ploc.getChunk().getX(), ploc.getChunk().getZ() + 1);
        Chunk line4chunk4 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ() + 1);
        Chunk line4chunk3 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 2, ploc.getChunk().getZ() + 1);
        Chunk line4chunk2 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 3, ploc.getChunk().getZ() + 1);
        Chunk line4chunk1 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 4, ploc.getChunk().getZ() + 1);
        lines.add(line4chunk1);
        lines.add(line4chunk2);
        lines.add(line4chunk3);
        lines.add(line4chunk4);
        lines.add(line4chunk5);
        lines.add(line4chunk6);
        lines.add(line4chunk7);
        lines.add(line4chunk8);
        lines.add(line4chunk9);
        Chunk line5chunk9 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 4, ploc.getChunk().getZ() + 2);
        Chunk line5chunk8 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 3, ploc.getChunk().getZ() + 2);
        Chunk line5chunk7 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 2, ploc.getChunk().getZ() + 2);
        Chunk line5chunk6 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ() + 2);
        Chunk line5chunk5 = ploc.getWorld().getChunkAt(ploc.getChunk().getX(), ploc.getChunk().getZ() + 2);
        Chunk line5chunk4 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ() + 2);
        Chunk line5chunk3 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 2, ploc.getChunk().getZ() + 2);
        Chunk line5chunk2 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 3, ploc.getChunk().getZ() + 2);
        Chunk line5chunk1 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 4, ploc.getChunk().getZ() + 2);
        lines.add(line5chunk1);
        lines.add(line5chunk2);
        lines.add(line5chunk3);
        lines.add(line5chunk4);
        lines.add(line5chunk5);
        lines.add(line5chunk6);
        lines.add(line5chunk7);
        lines.add(line5chunk8);
        lines.add(line5chunk9);
        boolean unconnected = true;
        for (Chunk chunks : lines) {
            if (checkIfClaimIsConnected(chunks, instance.getPlayerData(player).getCurrentFactionData()))
                unconnected = false;
        }
        if (unconnected) {
            player.sendMessage(Message.CLAIMS_MUST_BE_CONNECTED.getMessage());
            return;
        }
        List<Chunk> claimable = new ArrayList<Chunk>();
        for (Chunk chunks : lines) {
            if (instance.getChunkManager().isFree(chunks)) {
                claimable.add(chunks);
            }
        }
        for (Chunk chunk : claimable) {
            if (!instance.getChunkManager().isFree(chunk)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.ALREADY_CLAIMED.getMessage()));
                return;
            }
            if (!this.checkForPlayergroupPermission(player, FactionPerms.CLAIM)) {
                player.sendMessage(Message.NO_CLAIM_PERM.getMessage());
                return;
            }
            if (instance.getPlayerData(player).getCurrentFactionData().getChunks().size() + 1 > instance.getPlayerData(player).getCurrentFactionData().getPower() && !instance.getStaffmode().contains(player.getUniqueId())) {
                player.sendMessage(Message.NO_CLAIMING_POWER.getMessage());
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
            instance.getFactionManager().claimChunk(player, chunk.getX(), chunk.getZ(), instance.getChunkPlayer(player).getPlayerData().getFactionId(), true);
            System.out.println("Chunk claimed at " + player.getLocation().toString() + " for player " + player.getName());
            //Player members = (Player) instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Factions.col("&a&oChunk claimed")));
        }
        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()))) {
            if (claimable.size() > 0)
                if (Bukkit.getOnlinePlayers().contains(uuid))
                Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(Message.PLAYER_FILLED.getMessage().replace("%player%", player.getName()).replace("%chunks%", String.valueOf(claimable.size()))));
        }
        lines.remove(lines);
        claimable.remove(claimable);
    }

    public void Neutralize(Player player, String faction1Id, String faction2Id, FactionData faction1Data, FactionData faction2Data) {
        if (faction1Data.getAllies().contains(faction2Id) ) {
            List<String> allies1 = faction1Data.getAllies();
            List<String> allies2 = faction2Data.getAllies();
            allies1.remove(faction2Id);
            allies2.remove(faction1Id);
            faction1Data.setAllies(allies1);
            faction2Data.setAllies(allies2);
            updateFactionData(faction1Data);
            updateFactionData(faction2Data);
        } else if (faction1Data.getEnemies().contains(faction2Id) || faction2Data.getEnemies().contains(faction1Id)) {
            List<String> enemies1 = faction1Data.getEnemies();
            List<String> enemies2 = faction2Data.getEnemies();
            enemies1.remove(faction2Id);
            enemies2.remove(faction1Id);
            faction1Data.setEnemies(enemies1);
            faction2Data.setEnemies(enemies2);
            updateFactionData(faction1Data);
            updateFactionData(faction2Data);
        } else {
            player.sendMessage(Message.NO_RELATION.getMessage());
        }
    }

    public void Enemy(String faction1Id, String faction2Id, FactionData faction1Data, FactionData faction2Data) {
        List<String> enemies1 = faction1Data.getEnemies();
        List<String> enemies2 = faction2Data.getEnemies();
        enemies1.add(faction2Id);
        enemies2.add(faction1Id);
        faction1Data.setEnemies(enemies1);
        faction2Data.setEnemies(enemies2);
        updateFactionData(faction1Data);
        updateFactionData(faction2Data);
    }

    public void claimChunk(Player player, int x, int z, String factionId, Boolean ignoreUnconnected) {
        FactionData factionData = getFactionById(factionId);
        if (!instance.getFactionManager().checkIfClaimIsConnected(player.getLocation().getChunk(), instance.getFactionManager().getFactionById(instance.getPlayerData(player).getCurrentFactionData().getId())) && Config.ALLOW_UNCONNECTED_CLAIMS.getData().equals(false) && !instance.getStaffmode().contains(player.getUniqueId()) && !instance.getFillClaimPlayers().contains(player.getUniqueId()) && !ignoreUnconnected && !getFactionById(factionId).getName().equals("SafeZone")) {
            player.sendMessage(Message.CLAIMS_MUST_BE_CONNECTED.getMessage());
            return;
        }
        if (getFactionById(factionId).getPower() + 1 > (int) Config.MAX_FACTION_POWER.getData() && !getFactionById(factionId).getName().equals("SafeZone")) {
            player.sendMessage(Message.MAX_CLAIMING_REACHED.getMessage());
            return;
        }
        int price = (instance.getFactionManager().getFactionById(factionId).getChunks().size() >= 100 ? 5 * instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(factionId)).size() : 5);
        if (factionData.getMoney() >= price) {
            String id = instance.generateKey(10);
            ChunkData chunkData = new ChunkData(id, new ArrayList<UUID>(), x, z);
            this.addChunk(factionData, chunkData);
            instance.getFactionManager().getFactionById(factionId).setMoney(instance.getFactionManager().getMoneyFromFaction(factionData) - price);
            instance.getChunkManager().getChunks().add(chunkData);
            updateFactionData(getFactionById(factionId));
        } else {
            int need = (price - instance.getFactionManager().getFactionById(factionId).getMoney());
            String needString = instance.asDecimal(need);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§oYour faction needs §b§l§o" + needString + "$ §c§omore to buy this chunk."));
        }
    }

    public void unclaimChunk(Player player, Chunk chunk, String factionid) {
        int price = (instance.getFactionManager().getFactionById(factionid).getChunks().size() >= 100 ? 5 * instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(factionid)).size() : 5);
        FactionData factionData = getFactionById(factionid);
        String id = instance.getChunkManager().getChunkDataByChunk(chunk).getId();
        ChunkData chunkData = new ChunkData(id, new ArrayList<UUID>(), chunk.getX(), chunk.getZ());
        this.removeChunk(factionData, chunkData);
        instance.getFactionManager().getFactionById(factionid).setMoney(instance.getFactionManager().getMoneyFromFaction(factionData) + price);
        instance.getChunkManager().getChunks().remove(chunkData);
        updateFactionData(factionData);
    }

    public List<UUID> getMembersFromFaction(FactionData data) {
        final List<UUID> member = new ArrayList<>();
        for (UUID registeredPlayer : instance.getRegisteredPlayers())
            if (instance.getPlayerData(registeredPlayer).isInFaction() && instance.getPlayerData(registeredPlayer).getFactionId().equals(data.getId()))
                member.add(registeredPlayer);
        return member;
    }

    public FactionFlag getFlagByName(String name) {
        for (FactionFlag flags : this.listFlags()) {
            if (flags.getName().equals(name)) {
                return flags;
            }
        }
        return null;
    }

    public void addFlag(FactionData fac1, FactionFlag flag) {
        if (fac1.getAllowedFlags().contains(flag.getName())) {
            return;
        }
        List<String> flags = new ArrayList<String>(fac1.getAllowedFlags());
        flags.add(flag.getName());
        fac1.setAllowedFlags(flags);
        updateFactionData(fac1);
    }

    public void removeFlag(FactionData fac1, FactionFlag flag) {
        if (!fac1.getAllowedFlags().contains(flag.getName())) {
            return;
        }
        List<String> flags = new ArrayList<String>(fac1.getAllowedFlags());
        flags.remove(flag.getName());
        fac1.setAllowedFlags(flags);
        updateFactionData(fac1);
    }

    public void promotePlayer(Player player) {
        if (instance.getPlayerData(player).getFactionRank().equals(FactionRank.NEWBIE)) {
            instance.getPlayerData(player).setFactionRank(FactionRank.MEMBER);
            instance.getChunkPlayer(player).updatePlayerData(instance.getPlayerData(player));
        } else if (instance.getPlayerData(player).getFactionRank().equals(FactionRank.MEMBER)) {
            instance.getPlayerData(player).setFactionRank(FactionRank.ADMIN);
            instance.getChunkPlayer(player).updatePlayerData(instance.getPlayerData(player));
        }
        return;
    }

    public void demotePlayer(Player player) {
        if (instance.getPlayerData(player).getFactionRank().equals(FactionRank.ADMIN)) {
            instance.getPlayerData(player).setFactionRank(FactionRank.MEMBER);
            instance.getChunkPlayer(player).updatePlayerData(instance.getPlayerData(player));
        } else if (instance.getPlayerData(player).getFactionRank().equals(FactionRank.MEMBER)) {
            instance.getPlayerData(player).setFactionRank(FactionRank.NEWBIE);
            instance.getChunkPlayer(player).updatePlayerData(instance.getPlayerData(player));
        }
        return;
    }

    public void setPlayerRank(Player player, FactionRank rank) {
        instance.getPlayerData(player).setFactionRank(rank);
        instance.getChunkPlayer(player).updatePlayerData(instance.getPlayerData(player));
    }

    private void addChunk(FactionData factionData, ChunkData chunkData) {
        final List<ChunkData> chunks = factionData.getChunks();
        chunks.add(chunkData);
        factionData.setChunks(chunks);
        updateFactionData(factionData);
    }

    private void removeChunk(FactionData factionData, ChunkData chunkData) {
        final List<String> factionDatas = new ArrayList<>();
        final List<ChunkData> chunks = factionData.getChunks();
        for (ChunkData chunk : chunks)
            factionDatas.add(new Gson().toJson(chunk));
        factionDatas.remove(new Gson().toJson(chunkData));
        chunks.clear();
        for (String data : factionDatas)
            chunks.add(new Gson().fromJson(data, ChunkData.class));
        factionData.setChunks(chunks);
        updateFactionData(factionData);
        instance.getChunkManager().reloadChunks();
    }

    public void removeFaction(FactionData data) {
        for (UUID uuid : getMembersFromFaction(data))
            instance.getChunkPlayer(uuid).removeFromFaction();
        this.factions.remove(getFactionById(data.getId()));

        final List<String> factions = new ArrayList<>();
        for (FactionData faction : this.factions)
            factions.add(instance.getPrettyGson().toJson(new Gson().toJsonTree(faction)));

        cfg.set("Factions", factions);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance.getChunkManager().reloadChunks();
    }

    public void updateFactionData(FactionData factionData) {
        this.factions.remove(getFactionById(factionData.getId()));
        this.factions.add(factionData);

        final List<String> factions = new ArrayList<>();
        for (FactionData faction : this.factions)
            factions.add(instance.getPrettyGson().toJson(new Gson().toJsonTree(faction)));

        instance.getFactionFlags().put(factionData.getId(), instance.flags.getConfiguration().getItemStack(factionData.getId()));

        cfg.set("Factions", factions);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createRelInv(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 5, "§8-= §bYour factions relations §8=-");
        for (String ids : instance.getPlayerData(player).getCurrentFactionData().getAllies())
            inventory.addItem(new ItemBuilder(Material.MAGENTA_CONCRETE).addEnchantment(Enchantment.LOYALTY, 1).setDisplayName("§d§l" + instance.getFactionManager().getFactionById(ids).getName()).addLoreLine("§7Description: §f" + instance.getFactionManager().getFactionById(ids).getDescription()).build());
        for (String ids : instance.getPlayerData(player).getCurrentFactionData().getEnemies())
            inventory.addItem(new ItemBuilder(Material.RED_CONCRETE).addEnchantment(Enchantment.SWEEPING_EDGE, 1).setDisplayName("§4§l" + instance.getFactionManager().getFactionById(ids).getName()).addLoreLine("§7Description: §f" + instance.getFactionManager().getFactionById(ids).getDescription()).build());
        player.openInventory(inventory);
    }

    public void setCapitalLocation(FactionData fac1, ChunkLocation location) {
        fac1.setCapital(location);
        updateFactionData(fac1);
    }

    public void deleteCapital(FactionData fac1) {
        fac1.setCapital(null);
        updateFactionData(fac1);
    }

    public FactionData getFactionByName(final String name) {
        for (FactionData faction : factions)
            if (faction.getName().equalsIgnoreCase(name))
                return faction;
        return null;
    }

    public List<FactionPerms> listPerms() {
        List<FactionPerms> perms = new ArrayList<FactionPerms>();
        perms.add(FactionPerms.INVITE);
        perms.add(FactionPerms.BUILD);
        perms.add(FactionPerms.CONTAINER);
        perms.add(FactionPerms.CLAIM);
        perms.add(FactionPerms.RENAME);
        perms.add(FactionPerms.UNCLAIM);
        perms.add(FactionPerms.INTERACT);
        perms.add(FactionPerms.DEPOSIT);
        perms.add(FactionPerms.CHANGE_POLITY);
        perms.add(FactionPerms.WITHDRAW);
        perms.add(FactionPerms.DESCRIPTION);
        perms.add(FactionPerms.DISCORD);
        perms.add(FactionPerms.MODERATION);
        perms.add(FactionPerms.DYNMAPCOLOUR);
        perms.add(FactionPerms.EDITFLAGS);
        perms.add(FactionPerms.EDITCAPITAL);
        perms.add(FactionPerms.MANAGEPUPPETS);
        perms.add(FactionPerms.RELATION);
        perms.add(FactionPerms.CB_FACTIONS);
        perms.add(FactionPerms.EDITPERMS);
        perms.add(FactionPerms.SETWARP);
        perms.add(FactionPerms.PUPPET);
        perms.add(FactionPerms.SETFLAG);
        perms.add(FactionPerms.SLIMEFUN);
        perms.add(FactionPerms.FACTIONFLY);
        return perms;
    }

    public Boolean checkIfAlreadyPuppeted(FactionData data1) {
        for (FactionData facs : getFactions()) {
            if (facs.getPuppets().contains(data1.getId())) {
                return true;
            }
        }
        return false;
    }

    public List<String> getDefaultNewbiePerms() {
        List<String> list = new ArrayList<String>();
        list.add(FactionPerms.FACTIONFLY.getName().toUpperCase());
        return list;
    }

    public List<String> getDefaultMemberPerms() {
        List<String> list = new ArrayList<String>(getDefaultNewbiePerms());
        list.add(FactionPerms.CLAIM.getName().toUpperCase());
        list.add(FactionPerms.INTERACT.getName().toUpperCase());
        list.add(FactionPerms.BUILD.getName().toUpperCase());
        return list;
    }

    public void setFactionFlag(FactionData fac1, ItemStack flag) {
        if (!flag.getType().name().contains("BANNER")) return;
        instance.flags.getConfiguration().set(fac1.getId(), flag);
        instance.flags.save();
        instance.flags.reload();
        updateFactionData(fac1);
    }

    public Boolean checkIfClaimIsConnected(Chunk start, FactionData fac1) {
        Chunk north = Bukkit.getWorld(Config.FACTION_WORLD.getData().toString()).getChunkAt(start.getX(), start.getZ() + 1);
        Chunk south = Bukkit.getWorld(Config.FACTION_WORLD.getData().toString()).getChunkAt(start.getX(), start.getZ() - 1);
        Chunk west = Bukkit.getWorld(Config.FACTION_WORLD.getData().toString()).getChunkAt(start.getX() - 1, start.getZ());
        Chunk east = Bukkit.getWorld(Config.FACTION_WORLD.getData().toString()).getChunkAt(start.getX() + 1, start.getZ() + 1);
        if (fac1.getChunks().size() == 0) {
            return true;
        }
        return (fac1.getChunks().contains(instance.getChunkManager().getChunkDataByChunk(north)) && instance.getChunkManager().getChunkDataByChunk(north) != null) || (fac1.getChunks().contains(instance.getChunkManager().getChunkDataByChunk(south)) && instance.getChunkManager().getChunkDataByChunk(south) != null) || (fac1.getChunks().contains(instance.getChunkManager().getChunkDataByChunk(west)) && instance.getChunkManager().getChunkDataByChunk(west) != null) || (fac1.getChunks().contains(instance.getChunkManager().getChunkDataByChunk(east)) && instance.getChunkManager().getChunkDataByChunk(east) != null);
    }

    public void banPlayerFromFac(FactionData fac1, Player player) {
        if (fac1.getBannedplayer().contains(player.getUniqueId())) return;
        List<UUID> bannedplayers = new ArrayList<UUID>(fac1.getBannedplayer());
        bannedplayers.add(player.getUniqueId());
        fac1.setBannedplayer(bannedplayers);
        updateFactionData(fac1);
    }

    public void unBanPlayerFromFac(FactionData fac1, Player player) {
        if (!fac1.getBannedplayer().contains(player.getUniqueId())) return;
        List<UUID> bannedplayers = new ArrayList<UUID>(fac1.getBannedplayer());
        bannedplayers.remove(player.getUniqueId());
        fac1.setBannedplayer(bannedplayers);
        updateFactionData(fac1);
    }

    public List<String> getDefaultAdminPerms() {
        List<String> list = new ArrayList<String>(getDefaultMemberPerms());
        for (FactionPerms perms : listPerms())
            if (!list.contains(perms.getName().toUpperCase()))
                list.add(perms.getName().toUpperCase());
        return list;
    }

    public List<FactionUpgrades> listUpgrades() {
        List<FactionUpgrades> upgrades = new ArrayList<FactionUpgrades>();
        upgrades.add(FactionUpgrades.ONEPUBLICWARP);
        upgrades.add(FactionUpgrades.TWOPUBLICWARPS);
        upgrades.add(FactionUpgrades.THREEPUBLICWARPS);
        upgrades.add(FactionUpgrades.FACTION_FLY);
        upgrades.add(FactionUpgrades.DYNMAPCOLOUR);
        upgrades.add(FactionUpgrades.ONEPUPPET);
        upgrades.add(FactionUpgrades.TWOPUPPETS);
        upgrades.add(FactionUpgrades.THREEPUPPETS);
        upgrades.add(FactionUpgrades.BIGGER_FCHEST);
        upgrades.add(FactionUpgrades.COMMUNISM);
        upgrades.add(FactionUpgrades.FASCISM);
        return upgrades;
    }

    public FactionUpgrades getUpgradeByGuiName(String guiname) {
        FactionUpgrades result = null;
        for (FactionUpgrades upgrade : this.listUpgrades()) {
            if (upgrade.getGuiname().equalsIgnoreCase(guiname)) {
                result = upgrade;
            }
        }
        return result;
    }

    public Boolean checkIfFacHasFlagEnabled(FactionData faction, FactionFlag flag) {
        return faction.getAllowedFlags().contains(flag.getName());
    }

    public List<FactionFlag> listFlags() {
        List<FactionFlag> flags = new ArrayList<FactionFlag>();
        flags.add(FactionFlag.PVP);
        flags.add(FactionFlag.MONSTER);
        flags.add(FactionFlag.FRIENDLYFIRE);
        flags.add(FactionFlag.EXPLOSIONS);
        flags.add(FactionFlag.OPEN);
        flags.add(FactionFlag.ANIMALS);
        return flags;
    }

    public void addPermToGroup(Player player, String groupname, String permission) {
        if (groupname.equalsIgnoreCase("admin")) {
            List<String> perms = new ArrayList<String>(instance.getPlayerData(player).getCurrentFactionData().getAdminperms());
            perms.add(permission.toUpperCase());
            instance.getPlayerData(player).getCurrentFactionData().setAdminperms(perms);
        } else if (groupname.equalsIgnoreCase("member")) {
            List<String> perms = new ArrayList<String>(instance.getPlayerData(player).getCurrentFactionData().getMemberperms());
            perms.add(permission.toUpperCase());
            instance.getPlayerData(player).getCurrentFactionData().setMemberperms(perms);
        } else if (groupname.equalsIgnoreCase("newbie")) {
            List<String> perms = new ArrayList<String>(instance.getPlayerData(player).getCurrentFactionData().getNewbieperms());
            perms.add(permission.toUpperCase());
            instance.getPlayerData(player).getCurrentFactionData().setNewbieperms(perms);
        } else if (groupname.equalsIgnoreCase("ally")) {
            List<String> perms = new ArrayList<String>(instance.getPlayerData(player).getCurrentFactionData().getAllyperms());
            perms.add(permission.toUpperCase());
            instance.getPlayerData(player).getCurrentFactionData().setAllyperms(perms);
        } else if (groupname.equalsIgnoreCase("enemy")) {
            List<String> perms = new ArrayList<String>(instance.getPlayerData(player).getCurrentFactionData().getEnemyperms());
            perms.add(permission.toUpperCase());
            instance.getPlayerData(player).getCurrentFactionData().setEnemyperms(perms);
        }
        updateFactionData(instance.getPlayerData(player).getCurrentFactionData());
    }

    public void removePermFromGroup(Player player, String groupname, String permission) {
        if (groupname.equalsIgnoreCase("admin")) {
            List<String> perms = new ArrayList<String>(instance.getPlayerData(player).getCurrentFactionData().getAdminperms());
            perms.remove(permission.toUpperCase());
            instance.getPlayerData(player).getCurrentFactionData().setAdminperms(perms);
        } else if (groupname.equalsIgnoreCase("member")) {
            List<String> perms = new ArrayList<String>(instance.getPlayerData(player).getCurrentFactionData().getMemberperms());
            perms.remove(permission.toUpperCase());
            instance.getPlayerData(player).getCurrentFactionData().setMemberperms(perms);
        } else if (groupname.equalsIgnoreCase("newbie")) {
            List<String> perms = new ArrayList<String>(instance.getPlayerData(player).getCurrentFactionData().getNewbieperms());
            perms.remove(permission.toUpperCase());
            instance.getPlayerData(player).getCurrentFactionData().setNewbieperms(perms);
        } else if (groupname.equalsIgnoreCase("ally")) {
            List<String> perms = new ArrayList<String>(instance.getPlayerData(player).getCurrentFactionData().getAllyperms());
            perms.remove(permission.toUpperCase());
            instance.getPlayerData(player).getCurrentFactionData().setAllyperms(perms);
        } else if (groupname.equalsIgnoreCase("enemy")) {
            List<String> perms = new ArrayList<String>(instance.getPlayerData(player).getCurrentFactionData().getEnemyperms());
            perms.remove(permission.toUpperCase());
            instance.getPlayerData(player).getCurrentFactionData().setEnemyperms(perms);
        }
        updateFactionData(instance.getPlayerData(player).getCurrentFactionData());
    }

    public boolean checkForPlayergroupPermission(Player player, FactionPerms permission) {
        FactionData data = instance.getPlayerData(player).getCurrentFactionData();
        PlayerData pdata = instance.getPlayerData(player);
        if (instance.getPlayerData(player).getFactionRank().equals(FactionRank.LEADER)) {
            return true;
        } else if (instance.getPlayerData(player).getFactionRank().equals(FactionRank.ADMIN)) {
            if (instance.getPlayerData(player).getCurrentFactionData().getAdminperms().contains(permission.getName().toUpperCase())) {
                return true;
            } else {
                return false;
            }
        } else if (pdata.getFactionRank().equals(FactionRank.MEMBER)) {
            if (data.getMemberperms().contains(permission.getName().toUpperCase())) {
                return true;
            } else {
                return false;
            }
        } else if (pdata.getFactionRank().equals(FactionRank.NEWBIE)) {
            if (data.getNewbieperms().contains(permission.getName().toUpperCase())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void addChunkaccessToPlayer(Player reciever, Player requester, Chunk chunk) {
        String id = instance.generateKey(10);
        ChunkData chunkData = instance.getChunkManager().getChunkDataByChunk(chunk);
        if (instance.getPlayerData(reciever).getAccessableChunks().contains(chunkData)) {
            List<ChunkData> accessableChunks = new ArrayList<ChunkData>(instance.getPlayerData(reciever).getAccessableChunks());
            accessableChunks.add(chunkData);
            instance.getPlayerData(reciever).setAccessableChunks(accessableChunks);
            requester.sendMessage(Message.GAVE_CHUNKACCESS_TO_PLAYER.getMessage().replace("%player%", reciever.getName()));
            reciever.sendMessage(Message.PLAYER_GAINED_CHUNK_ACCESS.getMessage().replace("%loc%", requester.getLocation().getChunk().getX() + "§7, §a" + requester.getLocation().getChunk().getZ()));
            instance.getChunkPlayer(reciever.getUniqueId()).updatePlayerData(instance.getPlayerData(reciever.getUniqueId()));
            return;
        }
        if (!instance.getChunkManager().getFactionDataByChunk(chunk.getX(), chunk.getZ()).equals(instance.getPlayerData(requester).getCurrentFactionData())) {
            requester.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.CANT_ACCESS_WHATS_NOT_YOURS.getMessage()));
            return;
        }
        List<ChunkData> accessableChunks = new ArrayList<ChunkData>(instance.getPlayerData(reciever).getAccessableChunks());
        accessableChunks.add(chunkData);
        instance.getPlayerData(reciever).setAccessableChunks(accessableChunks);
        requester.sendMessage(Message.GAVE_CHUNKACCESS_TO_PLAYER.getMessage().replace("%player%", reciever.getName()));
        reciever.sendMessage(Message.PLAYER_GAINED_CHUNK_ACCESS.getMessage().replace("%loc%", requester.getLocation().getChunk().getX() + "§7, §a" + requester.getLocation().getChunk().getZ()));
        instance.getChunkPlayer(reciever.getUniqueId()).updatePlayerData(instance.getPlayerData(reciever.getUniqueId()));
    }

    public FactionPerms getPermissionByName(String name) {
        FactionPerms permission = null;
        for (FactionPerms perm : this.listPerms()) {
            if (perm.getName().equalsIgnoreCase(name)) {
                permission = perm;
            }
        }
        return permission;
    }

    public boolean checkIfRankHasPerm(Player player, String rankname, String permname) {
        if (rankname.equalsIgnoreCase("leader")) {
            return true;
        } else if (rankname.equalsIgnoreCase("admin")) {
            if (instance.getPlayerData(player).getCurrentFactionData().getAdminperms().contains(permname.toUpperCase())) {
                return true;
            } else {
                return false;
            }
        } else if (rankname.equalsIgnoreCase("member")) {
            if (instance.getPlayerData(player).getCurrentFactionData().getMemberperms().contains(permname.toUpperCase())) {
                return true;
            } else {
                return false;
            }
        } else if (rankname.equalsIgnoreCase("newbie")) {
            if (instance.getPlayerData(player).getCurrentFactionData().getNewbieperms().contains(permname.toUpperCase())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public FactionRank getRankByName(String name) {
        if (name.equalsIgnoreCase("leader")) {
            return FactionRank.LEADER;
        } else if (name.equalsIgnoreCase("admin")) {
            return FactionRank.ADMIN;
        } else if (name.equalsIgnoreCase("member")) {
            return FactionRank.MEMBER;
        } else if (name.equalsIgnoreCase("newbie")) {
            return FactionRank.NEWBIE;
        } else if (name.equalsIgnoreCase("none")){
            return FactionRank.NONE;
        } else {
            return null;
        }
    }

    public int getMoneyFromFaction(FactionData data) {
        return data.getMoney();
    }

    public FactionData getFactionById(final String id) {
        for (FactionData faction : factions)
            if (faction.getId().equals(id))
                return faction;
        return null;
    }

    public Boolean canClaimThatAmount(int amount, FactionData faction) {
        int price = amount * 5;
        if (price >= faction.getMoney() && amount <= faction.getChunks().size()) {
            return true;
        } else {
            return false;
        }
    }

    public void claimFill(Set<ChunkData> toClaim, Player player, FactionData faction) {
            for (ChunkData chunks : toClaim) {
                claimChunk(player, chunks.getMinecraftX(), chunks.getMinecraftZ(), faction.getId(), true);
            }
            player.sendMessage(Message.CLAIM_FILLED_X_CHUNKS.getMessage().replace("%x%", Integer.toString(toClaim.size())));
            Bukkit.getLogger().info(player.getName() + " claim filled " + toClaim.size() + " chunks.");
    }

    public Boolean warpWithNameAlreadyExists(FactionData data, String name) {
        for (WarpData warp : data.getWarps())
            if (warp.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    public void sendPlayerFactionInfo(Player player, FactionData faction) {
        String factionName = faction.getName();
        FactionData factionData = instance.getFactionManager().getFactionByName(factionName);
        StringJoiner sj = new StringJoiner("§8, ");
        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(factionData)) {
            OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);
            if (Bukkit.getServer().getOnlinePlayers().contains(member)) {
                sj.add(instance.getChunkPlayer(uuid).getRankPrefix() + member.getName());
            }
        }
        int chunks = faction.getChunks().size();
        StringJoiner sj1 = new StringJoiner("§8, ");
        for (UUID uuid : instance.getFactionManager().getMembersFromFaction(factionData)) {
            OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);
            sj1.add(instance.getChunkPlayer(uuid).getRankPrefix() + member.getName());
        }
        player.sendMessage("§8-§b+§8---------------------------------------------§b+§8-");
        player.sendMessage("§7Faction: §b" + faction.getName());
        player.sendMessage("§7Land: §b" + chunks + "§7/§b" + faction.getPower());
        player.sendMessage("§7Factionbank: §2§l$§a" + faction.getMoney());
        player.sendMessage("§7Age (Days): §a" + faction.getAgeInDays());
        player.sendMessage("§7Polity: " + faction.getIdeology());
        player.sendMessage("§7Total landcost: §2§l$§a" + chunks * 5);
        player.sendMessage("§7Online members: " + sj.toString());
        player.sendMessage("§7All-members: " + sj1.toString());
        player.sendMessage("§8-§b+§8---------------------------------------------§b+§8-");
    }

    public void addForeignPermission(FactionData hostfaction, ForeignFactionData foreignFactionData) {
        if (hostfaction.getForeignPerms().contains(foreignFactionData)) {
            return;
        }
        ArrayList<ForeignFactionData> foreignFactionData1 = new ArrayList<>(hostfaction.getForeignPerms());
        foreignFactionData1.add(foreignFactionData);
        hostfaction.setForeignPerms(foreignFactionData1);
        updateFactionData(hostfaction);
    }

    public void removeForeignPermission(FactionData hostfaction, ForeignFactionData foreignFactionData) {
        if (!hostfaction.getForeignPerms().contains(foreignFactionData)) {
            return;
        }
        ArrayList<ForeignFactionData> foreignFactionData1 = new ArrayList<>(hostfaction.getForeignPerms());
        foreignFactionData1.remove(foreignFactionData);
        hostfaction.setForeignPerms(foreignFactionData1);
        updateFactionData(hostfaction);
    }

    public Boolean checkForeignPermission(FactionData hostFaction, FactionData userFaction, FactionPerms permission) {
        if (hostFaction.getForeignPerms().contains(new ForeignFactionData(userFaction.getId(), hostFaction.getId(), permission.getName()))) {
            return true;
        }
        return false;
    }


    // THIS IS A 8-Neighbour FLOODFILL ALGORYTHM THAT SEARCHES FOR WHAT CHUNKS TO CLAIM.
    public void floodSearch(int x, int z, Set<ChunkData> toClaim, Set<Chunk> alreadyChecked) throws ExecutionException, InterruptedException {

                ChunkData chunk = new ChunkData(null, null, x, z);

                if (toClaim.size() >= (int) Config.MAX_FILL_SIZE.getData()) return;

                // ALREADY CONTAINS CHUNK?
                if (toClaim.contains(chunk))
                    return;

                // ALREADY CLAIMED?
                if (!(instance.getChunkManager().getFactionDataByChunk(x, z) == null))
                    return;

                toClaim.add(chunk);

                // RECOURSE
                floodSearch(x, z + 1, toClaim, alreadyChecked); // north
                floodSearch(x, z - 1, toClaim, alreadyChecked); // south
                floodSearch(x - 1, z, toClaim, alreadyChecked); // west
                floodSearch(x + 1, z, toClaim, alreadyChecked); // east
                floodSearch(x + 1, z + 1, toClaim, alreadyChecked); // north-east
                floodSearch(x - 1, z - 1, toClaim, alreadyChecked); // south-west
                floodSearch(x - 1, z + 1, toClaim, alreadyChecked); // north-west
                floodSearch(x + 1, z - 1, toClaim, alreadyChecked); // south-east

    }

    public void TopBalance(Player player) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        ValueComparator bvc = new ValueComparator(map);
        TreeMap<String, Integer> sorted_map = new TreeMap<String,Integer>(bvc);
        for (FactionData facs : getFactions())
            map.put(facs.getName(), facs.getMoney() + facs.getChunks().size() + facs.listMembers().size());
        sorted_map.putAll(map);
        List<String> ten = new ArrayList<String>();
        player.sendMessage("§8---§b+§8-§7[ §aFaction-Top §7]§8-§b+§8---");
        for (String s : sorted_map.keySet()) {
            if (10 >= ten.size() && !s.equalsIgnoreCase("safezone")) {
                ten.add(instance.generateKey(5));
                player.sendMessage("§7" + ten.size() + "§8. §b" + s + "§7: §6" + map.get(s) + " score");
            }
        }
    }

    class ValueComparator implements Comparator<String> {

        Map<String, Integer> base;
        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }


    public List<FactionData> getFactions() {
        return factions;
    }

    public String[] getRandomDescriptions() {
        return randomDescriptions;
    }

}
