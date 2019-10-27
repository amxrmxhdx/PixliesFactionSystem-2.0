package me.mickmmars.factions.factions;

import com.google.gson.Gson;
import me.mickmmars.factions.Factions;
import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.chunk.location.ChunkLocation;
import me.mickmmars.factions.factions.data.FactionData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public void createNewFaction(FactionData data) throws IOException {
        factions.add(data);

        final List<String> factions = cfg.getStringList("Factions");
        factions.add(instance.getPrettyGson().toJson(new Gson().toJsonTree(data)));
        cfg.set("Factions", factions);
        cfg.save(file);
    }

    public void claimChunk(Player player, Chunk chunk, String factionId) {
        FactionData factionData = getFactionById(factionId);

        int price = (instance.getFactionManager().getFactionById(factionId).getChunks().size() >= 100 ? 5 * instance.getFactionManager().getMembersFromFaction(instance.getFactionManager().getFactionById(factionId)).size() : 5);
        if (factionData.getMoney() >= price) {
            Location minLocation = instance.getChunkManager().getMinLocation(chunk);
            Location maxLocation = instance.getChunkManager().getMaxLocation(chunk);
            ChunkLocation minChunkLocation = new ChunkLocation(minLocation);
            ChunkLocation maxChunkLocation = new ChunkLocation(maxLocation);
            String id = instance.generateKey(10);
            ChunkData chunkData = new ChunkData(id, new ArrayList<UUID>(), maxChunkLocation, minChunkLocation);
            this.addChunk(factionData, chunkData);
            instance.getChunkManager().getChunks().add(chunkData);
        } else {
            int need = (price - instance.getFactionManager().getFactionById(factionId).getMoney());
            String needString = instance.asDecimal(need);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§oYour faction needs §b§l§o" + needString + "$ §c§omore to buy this chunk."));
        }
    }

    public List<UUID> getMembersFromFaction(FactionData data) {
        final List<UUID> member = new ArrayList<>();
        for (UUID registeredPlayer : instance.getRegisteredPlayers())
            if (instance.getPlayerData(registeredPlayer).isInFaction() && instance.getPlayerData(registeredPlayer).getFactionId().equals(data.getId()))
                member.add(registeredPlayer);
        return member;
    }

    private void addChunk(FactionData factionData, ChunkData chunkData) {
        final List<ChunkData> chunks = factionData.getChunks();
        chunks.add(chunkData);
        factionData.setChunks(chunks);
        updateFactionData(factionData);
    }

    public void removeFaction(FactionData data) {
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
    }

    public void updateFactionData(FactionData factionData) {
        this.factions.remove(getFactionById(factionData.getId()));
        this.factions.add(factionData);

        final List<String> factions = new ArrayList<>();
        for (FactionData faction : this.factions)
            factions.add(instance.getPrettyGson().toJson(new Gson().toJsonTree(faction)));

        cfg.set("Factions", factions);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public FactionData getFactionByName(final String name) {
        for (FactionData faction : factions)
            if (faction.getName().equalsIgnoreCase(name))
                return faction;
        return null;
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

    public List<FactionData> getFactions() {
        return factions;
    }

    public String[] getRandomDescriptions() {
        return randomDescriptions;
    }
}
