package me.mickmmars.factions.factions.inventory;

import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.flags.FactionFlag;
import me.mickmmars.factions.player.ChunkPlayer;
import me.mickmmars.factions.util.ItemBuilder;
import me.mickmmars.factions.util.SkullBuilder;
import me.mickmmars.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FactionInventory {

    private Factions instance = Factions.getInstance();

    private final UUID uuid;
    private Inventory inventory;
    private Player player;

    private final int[] slots = {19, 21, 23, 25, 28, 30, 32, 34,49};

    public FactionInventory(UUID uuid) {
        this.uuid = uuid;
        player = Bukkit.getPlayer(uuid);
    }

    public FactionInventory setItems() {
        inventory = Bukkit.createInventory(null, 9 * 6, "§a§lYour faction");

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());

        for (int i = 0; i < slots.length; i++) {
            GUIPage page = GUIPage.values()[i];
            if (page.equals(GUIPage.MEMBERS))
                inventory.setItem(slots[i], new ItemBuilder(page.getMaterial()).setDisplayName(page.getName()).build());
            else
                inventory.setItem(slots[i], new SkullBuilder(page.getSkinData()[0], page.getSkinData()[1]).setDisplayname(page.getName()).build());
        }
        return this;
    }

    public FactionInventory CreateFaction() {
        inventory = Bukkit.createInventory(null, 9 * 1, "Create a new faction?");

        inventory.setItem(0, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(1, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(2, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(3, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(4, new ItemBuilder(Material.CRAFTING_TABLE).setDisplayName("§aCreate").build());
        inventory.setItem(5, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(6, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(7, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(8, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        player.openInventory(inventory);
        return this;
    }

    public FactionInventory setItems(GUIPage page) {
        inventory = Bukkit.createInventory(null, 9 * 5, page.getName());

        switch (page) {
            case LIST:
                for (FactionData faction : instance.getFactionManager().getFactions()) {
                    List<String> description = new ArrayList<>();
                    for (int i = 0; i < faction.getDescription().length(); i++) {

                    }
                    /*FactionData factionData = instance.getChunkManager().getFactionDataByChunk(player.getLocation().getChunk());
                    String owner = "None";
                    for (UUID registeredPlayer : instance.getRegisteredPlayers()) {
                        ChunkPlayer chunkPlayer = (instance.getChunkPlayer(registeredPlayer) == null ? new ChunkPlayer(registeredPlayer) : instance.getChunkPlayer(registeredPlayer));
                        if (chunkPlayer.getPlayerData().getFactionId().equals(factionData.getId()) && chunkPlayer.getPlayerData().getFactionRank().equals(FactionRank.LEADER))
                            owner = registeredPlayer.toString();
                    }*/
                    String owner = "None";
                    for (UUID registeredPlayer : instance.getRegisteredPlayers()) {
                        ChunkPlayer chunkPlayer = (instance.getChunkPlayer(registeredPlayer) == null ? new ChunkPlayer(registeredPlayer) : instance.getChunkPlayer(registeredPlayer));
                        if (chunkPlayer.getPlayerData().getFactionId().equals(faction.getId()))
                            owner = Bukkit.getOfflinePlayer(registeredPlayer).getName();
                    }
                    inventory.addItem(new ItemBuilder(Material.PAPER).setDisplayName("§c§l" + faction.getName()).addLoreArray(new String[]{"§7Leader: §b§l" + owner, "§7Description: §b§o" + faction.getDescription(), "§7Balance: §2§l$§a" + faction.getMoney(), " ", "§8§l(§7§l!§8§l) §7§oClick to send §a§ojoin-request"}).build());
                }
                break;
            case MEMBERS:
                String factionName = instance.getPlayerData(player).getCurrentFactionData().getName();
                FactionData factionData = instance.getFactionManager().getFactionByName(factionName);
                for (UUID uuid : instance.getFactionManager().getMembersFromFaction(factionData)) {
                    Player member = Bukkit.getPlayer(uuid);
                    inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(member.getName()).setDisplayName(member.getName()).addLoreArray(new String[]{"§3Rank: §a" + instance.getPlayerData(member).getFactionRank().getName(), "§8§l(§7§l!§8§l) §7§oLeftclick to promote", "§8§l(§7§l!§8§l) §7§oRightclick to demote"}).build());
                }
                break;
            case APPLICATIONS:
                for (String uuids : instance.getPlayerData(player).getCurrentFactionData().getApplications()) {
                    UUID uuid = UUID.fromString(uuids);
                    inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName()).setDisplayName(Bukkit.getPlayer(uuid).getName()).addLoreLine("§8§l(§7§l!§8§l) §7Left-click to accept.").build());
                }
                break;
            case FINANCE:
                int money = instance.getPlayerData(player).getCurrentFactionData().getMoney();
                String moneystring = String.valueOf(money);
                inventory.addItem( new ItemBuilder(Material.EMERALD).setDisplayName("§7Balance§8: §2§l$§a" + moneystring).build());
                inventory.addItem( new ItemBuilder(Material.GRASS_BLOCK).setDisplayName("§7Landcost§8: §2§l$§a" + instance.getPlayerData(player).getCurrentFactionData().getChunks().size() * 5).build());
                break;
            case FLAGS:
                for (FactionFlag flags : FactionFlag.values()) {
                    if (flags.getValue().equals(true)) {
                        inventory.addItem(new ItemBuilder(Material.GREEN_BANNER).setDisplayName(flags.getName()).addLoreArray(new String[]{"§7Enabled:" + " §aYes", " ", "§8§l(§7§l!§8§l) §7Click to toggle"}).build());
                    } else {
                        inventory.addItem(new ItemBuilder(Material.RED_BANNER).setDisplayName(flags.getName()).addLoreArray(new String[]{"§7Enabled:" + " §cNo", "§8§l(§7§l!§8§l) §7Click to toggle"}).build());
                    }
                }
                break;
        }
        inventory.setItem(36, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(37, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(38, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(39, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(41, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(42, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(43, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(44, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(40, new SkullBuilder(Factions.Skulls.BACK_ITEM[1], Factions.Skulls.BACK_ITEM[0]).setDisplayname("§cBack").build());

        return this;
    }

    public FactionInventory removeBackItem() {
        inventory.setItem(36, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(37, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(38, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(39, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(40, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(41, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(42, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(43, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        inventory.setItem(44, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
        return this;
    }

    public void load() {
        player.openInventory(inventory);
    }

    public enum GUIPage {
        LIST("§a§oList", Material.PLAYER_HEAD, "{display:{Name:\\\"Papers\\\"},SkullOwner:{Id:\\\"08a21c84-663b-4638-a31e-2f0423a3853f\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OTJmOTljYzZkNzgyNDIzMDQxMTA1NTM1ODk0ODQyOThiMmU0YTAyMzNiNzY3NTNmODg4ZTIwN2VmNSJ9fX0=\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OTJmOTljYzZkNzgyNDIzMDQxMTA1NTM1ODk0ODQyOThiMmU0YTAyMzNiNzY3NTNmODg4ZTIwN2VmNSJ9fX0="),
        MEMBERS("§a§oMembers", Material.PLAYER_HEAD),
        FMAP("§a§oFMap", Material.PLAYER_HEAD, "{display:{Name:\\\"Grass Block (alpha)\\\"},SkullOwner:{Id:\\\"e2e44b3b-e45b-45ae-82ce-2ed4208184a2\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzk1ZDM3OTkzZTU5NDA4MjY3ODQ3MmJmOWQ4NjgyMzQxM2MyNTBkNDMzMmEyYzdkOGM1MmRlNDk3NmIzNjIifX19\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzk1ZDM3OTkzZTU5NDA4MjY3ODQ3MmJmOWQ4NjgyMzQxM2MyNTBkNDMzMmEyYzdkOGM1MmRlNDk3NmIzNjIifX19"),
        SETTINGS("§a§oSettings", Material.PLAYER_HEAD, "{display:{Name:\\\"Command Block (impulse)\\\"},SkullOwner:{Id:\\\"a36e6694-fa87-48d2-abdb-62eaf2b6711d\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY0YzIxZDE3YWQ2MzYzODdlYTNjNzM2YmZmNmFkZTg5NzMxN2UxMzc0Y2Q1ZDliMWMxNWU2ZTg5NTM0MzIifX19\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY0YzIxZDE3YWQ2MzYzODdlYTNjNzM2YmZmNmFkZTg5NzMxN2UxMzc0Y2Q1ZDliMWMxNWU2ZTg5NTM0MzIifX19"),
        APPLICATIONS("§a§oApplications", Material.PLAYER_HEAD, "{display:{Name:\\\"Old Manuscript\\\"},SkullOwner:{Id:\\\"82929274-58b4-4a75-8e00-46f4e1aa85f9\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTgxOGQxY2M1M2MyNzVjMjk0ZjVkZmI1NTkxNzRkZDkzMWZjNTE2YTg1YWY2MWExZGUyNTZhZWQ4YmNhNWU3In19fQ==\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTgxOGQxY2M1M2MyNzVjMjk0ZjVkZmI1NTkxNzRkZDkzMWZjNTE2YTg1YWY2MWExZGUyNTZhZWQ4YmNhNWU3In19fQ=="),
        UPGRADES("§a§oFaction upgrades", Material.PLAYER_HEAD, "{display:{Name:\\\"Quartz Arrow Up\\\"},SkullOwner:{Id:\\\"96f198b9-1e67-4b68-bbd1-c5213797e58a\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk5YWFmMjQ1NmE2MTIyZGU4ZjZiNjI2ODNmMmJjMmVlZDlhYmI4MWZkNWJlYTFiNGMyM2E1ODE1NmI2NjkifX19\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk5YWFmMjQ1NmE2MTIyZGU4ZjZiNjI2ODNmMmJjMmVlZDlhYmI4MWZkNWJlYTFiNGMyM2E1ODE1NmI2NjkifX19"),
        FINANCE("§a§oFinance", Material.PLAYER_HEAD, "{display:{Name:\\\"Monitor\\\"},SkullOwner:{Id:\\\"bbc26eae-6689-4c28-846a-6908baf83d12\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE5MjljNzE2NTQ0MmJmYTcwNGVlYTBmNTM2YTk3YzI3MDE5NzY3NzAyNDY5ZjA2YmY2MGJiYTkwMjBjZDIyNCJ9fX0=\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE5MjljNzE2NTQ0MmJmYTcwNGVlYTBmNTM2YTk3YzI3MDE5NzY3NzAyNDY5ZjA2YmY2MGJiYTkwMjBjZDIyNCJ9fX0="),
        FLAGS("§a§oFlags", Material.PLAYER_HEAD, "{display:{Name:\\\"Icon (Flag)\\\"},SkullOwner:{Id:\\\"5ec8b668-ed18-40ed-8aaa-b93f169ee0b4\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhmZDcxMjZjZDY3MGM3OTcxYTI4NTczNGVkZmRkODAyNTcyYTcyYTNmMDVlYTQxY2NkYTQ5NDNiYTM3MzQ3MSJ9fX0=\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhmZDcxMjZjZDY3MGM3OTcxYTI4NTczNGVkZmRkODAyNTcyYTcyYTNmMDVlYTQxY2NkYTQ5NDNiYTM3MzQ3MSJ9fX0="),
        DISCORD("§d§oFaction Discord", Material.PLAYER_HEAD, "{display:{Name:\\\"Discord\\\"},SkullOwner:{Id:\\\"df774cf8-eb17-48b5-b990-1304d4fc43ea\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ0MjMzN2JlMGJkY2EyMTI4MDk3ZjFjNWJiMTEwOWU1YzYzM2MxNzkyNmFmNWZiNmZjMjAwMDAwMTFhZWI1MyJ9fX0=\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ0MjMzN2JlMGJkY2EyMTI4MDk3ZjFjNWJiMTEwOWU1YzYzM2MxNzkyNmFmNWZiNmZjMjAwMDAwMTFhZWI1MyJ9fX0=");

        private final String name;
        private final Material material;
        private final String[] skinData;

        GUIPage(String name, Material material, String... value) {
            this.name = name;
            this.material = material;
            this.skinData = value;
        }

        public String getName() {
            return name;
        }

        public Material getMaterial() {
            return material;
        }

        public String[] getSkinData() {
            return skinData;
        }

        public static GUIPage getPageByMaterial(Material material) {
            for (GUIPage value : values())
                if (value.getMaterial().equals(material))
                    return value;
            return null;
        }

        public static GUIPage getPageByName(String name) {
            for (GUIPage value : values())
                if (value.getName().equals(name))
                    return value;
            return null;
        }
    }

}
