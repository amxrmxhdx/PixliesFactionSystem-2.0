package me.mickmmars.factions.factions.inventory;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.util.ItemBuilder;
import me.mickmmars.factions.util.SkullBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FMapInventory {
    private Factions instance = Factions.getInstance();
    private Inventory inventory;
    private final UUID uuid;
    private Player player;

    public FMapInventory(UUID uuid) {
        this.uuid = uuid;
        player = Bukkit.getPlayer(uuid);
    }

    public FMapInventory setChunks() {
        inventory = Bukkit.createInventory(null, 9 * 6, "§8Chunk-map §0| §8Facing: §c" + player.getFacing().toString());
        Location ploc = player.getLocation();
        //LINE1
        Chunk line1chunk9 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 4, ploc.getChunk().getZ() - 2);
        Chunk line1chunk8 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 3, ploc.getChunk().getZ() - 2);
        Chunk line1chunk7 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 2, ploc.getChunk().getZ() - 2);
        Chunk line1chunk6 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ() - 2);
        Chunk line1chunk5 = ploc.getWorld().getChunkAt(ploc.getChunk().getX(), ploc.getChunk().getZ() - 2);
        Chunk line1chunk4 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ() - 2);
        Chunk line1chunk3 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 2, ploc.getChunk().getZ() - 2);
        Chunk line1chunk2 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 3, ploc.getChunk().getZ() - 2);
        Chunk line1chunk1 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 4, ploc.getChunk().getZ() - 2);
        List<Chunk> line1 = new ArrayList<Chunk>();
        line1.add(line1chunk1);
        line1.add(line1chunk2);
        line1.add(line1chunk3);
        line1.add(line1chunk4);
        line1.add(line1chunk5);
        line1.add(line1chunk6);
        line1.add(line1chunk7);
        line1.add(line1chunk8);
        line1.add(line1chunk9);
        for (Chunk line1chunks : line1) {
                if (instance.getChunkManager().isFree(line1chunks)) {
                    inventory.addItem(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§6" + line1chunks.getX() + "§7, §6" + line1chunks.getZ()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line1chunks).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName())) {
                    inventory.addItem(new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).setDisplayName("§6" + line1chunks.getX() + "§7, §6" + line1chunks.getZ()).addLoreLine("§7Claimed by: §b" + instance.getChunkManager().getFactionDataByChunk(line1chunks).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line1chunks).getAllies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.addItem(new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE).setDisplayName("§6" + line1chunks.getX() + "§7, §6" + line1chunks.getZ()).addLoreLine("§7Claimed by: §5" + instance.getChunkManager().getFactionDataByChunk(line1chunks).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line1chunks).getEnemies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.addItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§6" + line1chunks.getX() + "§7, §6" + line1chunks.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(line1chunks).getName()).build());
                } else {
                    inventory.addItem(new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§6" + line1chunks.getX() + "§7, §6" + line1chunks.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(line1chunks).getName()).build());
                }
        }
        //LINE 2
        Chunk line2chunk9 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 4, ploc.getChunk().getZ() - 1);
        Chunk line2chunk8 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 3, ploc.getChunk().getZ() - 1);
        Chunk line2chunk7 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 2, ploc.getChunk().getZ() - 1);
        Chunk line2chunk6 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ() - 1);
        Chunk line2chunk5 = ploc.getWorld().getChunkAt(ploc.getChunk().getX(), ploc.getChunk().getZ() - 1);
        Chunk line2chunk4 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ() - 1);
        Chunk line2chunk3 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 2, ploc.getChunk().getZ() - 1);
        Chunk line2chunk2 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 3, ploc.getChunk().getZ() - 1);
        Chunk line2chunk1 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 4, ploc.getChunk().getZ() - 1);
        List<Chunk> line2 = new ArrayList<Chunk>();
        line2.add(line2chunk1);
        line2.add(line2chunk2);
        line2.add(line2chunk3);
        line2.add(line2chunk4);
        line2.add(line2chunk5);
        line2.add(line2chunk6);
        line2.add(line2chunk7);
        line2.add(line2chunk8);
        line2.add(line2chunk9);
        for (Chunk line2chunks : line2) {
                if (instance.getChunkManager().isFree(line2chunks)) {
                    inventory.addItem(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§6" + line2chunks.getX() + "§7, §6" + line2chunks.getZ()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line2chunks).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName())) {
                    inventory.addItem(new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).setDisplayName("§6" + line2chunks.getX() + "§7, §6" + line2chunks.getZ()).addLoreLine("§7Claimed by: §b" + instance.getChunkManager().getFactionDataByChunk(line2chunks).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line2chunks).getAllies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.addItem(new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE).setDisplayName("§6" + line2chunks.getX() + "§7, §6" + line2chunks.getZ()).addLoreLine("§7Claimed by: §5" + instance.getChunkManager().getFactionDataByChunk(line2chunks).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line2chunks).getEnemies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.addItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§6" + line2chunks.getX() + "§7, §6" + line2chunks.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(line2chunks).getName()).build());
                } else {
                    inventory.addItem(new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§6" + line2chunks.getX() + "§7, §6" + line2chunks.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(line2chunks).getName()).build());
                }
        }
        //LINE 3
        Chunk line3chunk9 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 4, ploc.getChunk().getZ());
        Chunk line3chunk8 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 3, ploc.getChunk().getZ());
        Chunk line3chunk7 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 2, ploc.getChunk().getZ());
        Chunk line3chunk6 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ());
        Chunk playerchunk = ploc.getBlock().getChunk();
        Chunk line3chunk4 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ());
        Chunk line3chunk3 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 2, ploc.getChunk().getZ());
        Chunk line3chunk2 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 3, ploc.getChunk().getZ());
        Chunk line3chunk1 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 4, ploc.getChunk().getZ());
        List<Chunk> line3 = new ArrayList<Chunk>();
        line3.add(line3chunk1);
        line3.add(line3chunk2);
        line3.add(line3chunk3);
        line3.add(line3chunk4);
        line3.add(line3chunk6);
        line3.add(line3chunk7);
        line3.add(line3chunk8);
        line3.add(line3chunk9);
        for (Chunk line3chunks : line3) {
                if (instance.getChunkManager().isFree(line3chunks)) {
                    inventory.addItem(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§6" + line3chunks.getX() + "§7, §6" + line3chunks.getZ()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line3chunks).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName())) {
                    inventory.addItem(new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).setDisplayName("§6" + line3chunks.getX() + "§7, §6" + line3chunks.getZ()).addLoreLine("§7Claimed by: §b" + instance.getChunkManager().getFactionDataByChunk(line3chunks).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line3chunks).getAllies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.addItem(new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE).setDisplayName("§6" + line3chunks.getX() + "§7, §6" + line3chunks.getZ()).addLoreLine("§7Claimed by: §5" + instance.getChunkManager().getFactionDataByChunk(line3chunks).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line3chunks).getEnemies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.addItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§6" + line3chunks.getX() + "§7, §6" + line3chunks.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(line3chunks).getName()).build());
                } else {
                    inventory.addItem(new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§6" + line3chunks.getX() + "§7, §6" + line3chunks.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(line3chunks).getName()).build());
                }
            if (instance.getChunkManager().isFree(playerchunk)) {
                inventory.setItem(22, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player.getName()).setDisplayName("§6" + playerchunk.getX() + "§7, §6" + playerchunk.getZ()).build());
            } else {
                if (instance.getChunkManager().getFactionDataByChunk(playerchunk).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName())){
                    inventory.setItem(22, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player.getName()).setDisplayName("§6" + playerchunk.getX() + "§7, §6" + playerchunk.getZ()).addLoreLine("§7Claimed by: §b" + instance.getChunkManager().getFactionDataByChunk(playerchunk).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(playerchunk).getAllies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.setItem(22, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player.getName()).setDisplayName("§6" + playerchunk.getX() + "§7, §6" + playerchunk.getZ()).addLoreLine("§7Claimed by: §5" + instance.getChunkManager().getFactionDataByChunk(playerchunk).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(playerchunk).getEnemies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.setItem(22, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player.getName()).setDisplayName("§6" + playerchunk.getX() + "§7, §6" + playerchunk.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(playerchunk).getName()).build());
                } else {
                    inventory.setItem(22, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player.getName()).setDisplayName("§6" + playerchunk.getX() + "§7, §6" + playerchunk.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(playerchunk).getName()).build());
                }
            }
        }
        //LINE4
        Chunk line4chunk9 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 4, ploc.getChunk().getZ() + 1);
        Chunk line4chunk8 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 3, ploc.getChunk().getZ() + 1);
        Chunk line4chunk7 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 2, ploc.getChunk().getZ() + 1);
        Chunk line4chunk6 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ() + 1);
        Chunk line4chunk5 = ploc.getWorld().getChunkAt(ploc.getChunk().getX(), ploc.getChunk().getZ() + 1);
        Chunk line4chunk4 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ() + 1);
        Chunk line4chunk3 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 2, ploc.getChunk().getZ() + 1);
        Chunk line4chunk2 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 3, ploc.getChunk().getZ() + 1);
        Chunk line4chunk1 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 4, ploc.getChunk().getZ() + 1);
        List<Chunk> line4 = new ArrayList<Chunk>();
        line4.add(line4chunk1);
        line4.add(line4chunk2);
        line4.add(line4chunk3);
        line4.add(line4chunk4);
        line4.add(line4chunk5);
        line4.add(line4chunk6);
        line4.add(line4chunk7);
        line4.add(line4chunk8);
        line4.add(line4chunk9);
        for (Chunk line4chunks : line4) {
                if (instance.getChunkManager().isFree(line4chunks)) {
                    inventory.addItem(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§6" + line4chunks.getX() + "§7, §6" + line4chunks.getZ()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line4chunks).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName())) {
                    inventory.addItem(new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).setDisplayName("§6" + line4chunks.getX() + "§7, §6" + line4chunks.getZ()).addLoreLine("§7Claimed by: §b" + instance.getChunkManager().getFactionDataByChunk(line4chunks).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line4chunks).getAllies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.addItem(new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE).setDisplayName("§6" + line4chunks.getX() + "§7, §6" + line4chunks.getZ()).addLoreLine("§7Claimed by: §5" + instance.getChunkManager().getFactionDataByChunk(line4chunks).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line4chunks).getEnemies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.addItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§6" + line4chunks.getX() + "§7, §6" + line4chunks.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(line4chunks).getName()).build());
                } else {
                    inventory.addItem(new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§6" + line4chunks.getX() + "§7, §6" + line4chunks.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(line4chunks).getName()).build());
                }
            }
        //LINE5
        Chunk line5chunk9 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 4, ploc.getChunk().getZ() + 2);
        Chunk line5chunk8 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 3, ploc.getChunk().getZ() + 2);
        Chunk line5chunk7 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 2, ploc.getChunk().getZ() + 2);
        Chunk line5chunk6 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() + 1, ploc.getChunk().getZ() + 2);
        Chunk line5chunk5 = ploc.getWorld().getChunkAt(ploc.getChunk().getX(), ploc.getChunk().getZ() + 2);
        Chunk line5chunk4 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 1, ploc.getChunk().getZ() + 2);
        Chunk line5chunk3 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 2, ploc.getChunk().getZ() + 2);
        Chunk line5chunk2 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 3, ploc.getChunk().getZ() + 2);
        Chunk line5chunk1 = ploc.getWorld().getChunkAt(ploc.getChunk().getX() - 4, ploc.getChunk().getZ() + 2);
        List<Chunk> line5 = new ArrayList<Chunk>();
        line5.add(line5chunk1);
        line5.add(line5chunk2);
        line5.add(line5chunk3);
        line5.add(line5chunk4);
        line5.add(line5chunk5);
        line5.add(line5chunk6);
        line5.add(line5chunk7);
        line5.add(line5chunk8);
        line5.add(line5chunk9);
        for (Chunk line5chunks : line5) {
                if (instance.getChunkManager().isFree(line5chunks)) {
                    inventory.addItem(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§6" + line5chunks.getX() + "§7, §6" + line5chunks.getZ()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line5chunks).getName().equals(instance.getPlayerData(player).getCurrentFactionData().getName())) {
                    inventory.addItem(new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).setDisplayName("§6" + line5chunks.getX() + "§7, §6" + line5chunks.getZ()).addLoreLine("§7Claimed by: §b" + instance.getChunkManager().getFactionDataByChunk(line5chunks).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line5chunks).getAllies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.addItem(new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE).setDisplayName("§6" + line5chunks.getX() + "§7, §6" + line5chunks.getZ()).addLoreLine("§7Claimed by: §5" + instance.getChunkManager().getFactionDataByChunk(line5chunks).getName()).build());
                } else if (instance.getChunkManager().getFactionDataByChunk(line5chunks).getEnemies().contains(instance.getPlayerData(player).getFactionId())) {
                    inventory.addItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§6" + line5chunks.getX() + "§7, §6" + line5chunks.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(line5chunks).getName()).build());
                } else {
                    inventory.addItem(new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§6" + line5chunks.getX() + "§7, §6" + line5chunks.getZ()).addLoreLine("§7Claimed by: §4" + instance.getChunkManager().getFactionDataByChunk(line5chunks).getName()).build());
                }
        }
        inventory.setItem(45, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(46, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(47, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(48, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(49, new SkullBuilder(Factions.Skulls.FILL[1], Factions.Skulls.FILL[0]).setDisplayname("§aClaim-fill map").build());
        inventory.setItem(50, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(51, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(52, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(53, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());

        line1.removeAll(line1);
        line2.removeAll(line2);
        line3.removeAll(line3);
        line4.removeAll(line4);
        line5.removeAll(line5);
        return this;
    }

    public void load() {
        player.openInventory(inventory);
    }

}
