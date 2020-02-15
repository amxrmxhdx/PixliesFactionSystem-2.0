package me.mickmmars.factions.factions.inventory;

import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.factions.flags.FactionFlag;
import me.mickmmars.factions.factions.ideologies.Ideology;
import me.mickmmars.factions.factions.upgrades.FactionUpgrades;
import me.mickmmars.factions.player.ChunkPlayer;
import me.mickmmars.factions.util.ItemBuilder;
import me.mickmmars.factions.util.ItemStackSerializer;
import me.mickmmars.factions.util.SkullBuilder;
import me.mickmmars.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

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
        inventory = Bukkit.createInventory(null, 9 * 3, "Create a new faction?");

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
        FactionData data = instance.getPlayerData(player).getCurrentFactionData();

        switch (page) {
            case LIST:
                for (FactionData faction : instance.getFactionManager().getFactions()) {
                    if (!faction.getName().equalsIgnoreCase("SafeZone")) {
                        ItemStack item = new ItemStack(instance.getFactionFlags().get(faction.getId()).getType());
                        BannerMeta meta = (BannerMeta) instance.getFactionFlags().get(faction.getId()).getItemMeta();
                        meta.setDisplayName("§c§l" + faction.getName());
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                        meta.setLore(Arrays.asList("§7Leader: §b§l" + Bukkit.getOfflinePlayer(instance.getFactionManager().getLeader(faction)).getName(), "§7Description: §b§o" + faction.getDescription(), "§7Balance: §2§l$§a" + faction.getMoney(), "§7Polity: " + faction.getIdeology(),  " ", "§8§l(§7§l!§8§l) §7§oClick to send §a§ojoin-request"));
                        item.setItemMeta(meta);
                        inventory.addItem(item);
                    }
                    for (int i = instance.getFactionManager().getFactions().size() - 1; i < inventory.getSize(); i++)
                        if (i != 40)
                            inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
                }
                break;
            case UPGRADES:
                for (FactionUpgrades upgrade : instance.getFactionManager().listUpgrades()) {
                    if (instance.getPlayerData(player).getCurrentFactionData().getUpgrades().contains(upgrade.getName().toUpperCase())) {
                        inventory.addItem(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName(upgrade.getGuiname()).addLoreLine("§7§oYou already purchased this upgrade.").build());
                    } else {
                        inventory.addItem(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setDisplayName(upgrade.getGuiname()).addLoreLine("§7§oYou need to purchase that upgrade to use it.").addLoreLine(" ").addLoreLine("§7Price: §2§l$§a" + upgrade.getPrice()).build());
                    }
                }
                for (int i = instance.getFactionManager().listUpgrades().size(); i < inventory.getSize(); i++)
                    if (i != 40)
                        inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setNoName().build());
                break;
            case MEMBERS:
                String factionName = instance.getPlayerData(player).getCurrentFactionData().getName();
                FactionData factionData = instance.getFactionManager().getFactionByName(factionName);
                for (UUID uuid : instance.getFactionManager().getMembersFromFaction(factionData)) {
                    OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);
                    inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(member.getName()).setDisplayName(member.getName()).addLoreArray(new String[]{"§3Rank: §a" + instance.getPlayerData(uuid).getFactionRank().getName(), "§8§l(§7§l!§8§l) §7§oLeftclick to promote", "§8§l(§7§l!§8§l) §7§oRightclick to demote"}).build());
                }
                break;
            case APPLICATIONS:
                for (String uuids : instance.getPlayerData(player).getCurrentFactionData().getApplications()) {
                    UUID uuid = UUID.fromString(uuids);
                    inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Bukkit.getOfflinePlayer(uuid).getName()).setDisplayName(Bukkit.getOfflinePlayer(uuid).getName()).addLoreLine("§8§l(§7§l!§8§l) §7Left-click to accept.").build());
                }
                break;
            case FINANCE:
                int money = instance.getPlayerData(player).getCurrentFactionData().getMoney();
                String moneystring = String.valueOf(money);
                inventory.addItem( new ItemBuilder(Material.EMERALD).setDisplayName("§7Balance§8: §2§l$§a" + moneystring).build());
                inventory.addItem( new ItemBuilder(Material.GRASS_BLOCK).setDisplayName("§7Landcost§8: §2§l$§a" + instance.getPlayerData(player).getCurrentFactionData().getChunks().size() * 5).build());
                break;
            case FLAGS:
                for (FactionFlag flags : instance.getFactionManager().listFlags()) {
                    if (instance.getPlayerData(player).getCurrentFactionData().getAllowedFlags().contains(flags.getName())) {
                        inventory.addItem(new ItemBuilder(Material.GREEN_BANNER).setDisplayName(flags.getName()).addLoreArray(new String[]{"§7Enabled:" + " §aYes", " ", "§8§l(§7§l!§8§l) §7Click to toggle"}).build());
                    } else {
                        inventory.addItem(new ItemBuilder(Material.RED_BANNER).setDisplayName(flags.getName()).addLoreArray(new String[]{"§7Enabled:" + " §cNo", "§8§l(§7§l!§8§l) §7Click to toggle"}).build());
                    }
                }
                break;
            case SETTINGS:
                if (instance.getPlayerData(player).getCurrentFactionData().getIdeology().equals(Ideology.DEMOCRATIC.getName())) {
                    inventory.addItem(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§7Polity: §aDemocratic").addLoreLine("§7§oLeftclick to change").build());
                } else if (instance.getPlayerData(player).getCurrentFactionData().getIdeology().equals(Ideology.THEOCRACY.getName())) {
                    inventory.addItem(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("§7Polity: §2Theocracy").addLoreLine("§7§oLeftclick to change").build());
                } else if (instance.getPlayerData(player).getCurrentFactionData().getIdeology().equals(Ideology.MONARCHY.getName())) {
                    inventory.addItem(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("§7Polity: §6Monarchy").addLoreLine("§7§oLeftclick to change").build());
                } else if (instance.getPlayerData(player).getCurrentFactionData().getIdeology().equals(Ideology.COMMUNIST.getName())) {
                    inventory.addItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§7Polity: §4Communist").addLoreLine("§7§oLeftclick to change").build());
                } else if (instance.getPlayerData(player).getCurrentFactionData().getIdeology().equals(Ideology.FASCIST.getName())) {
                    inventory.addItem(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("§7Polity: §9Fascist").addLoreLine("§7§oLeftclick to change").build());
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
        UPGRADES("§a§oFaction shop", Material.PLAYER_HEAD, "{display:{Name:\\\"Minecraft Earth Shop\\\"},SkullOwner:{Id:\\\"8df67171-1b9a-4eae-b35d-b03a56f8dacb\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2UzZGViNTdlYWEyZjRkNDAzYWQ1NzI4M2NlOGI0MTgwNWVlNWI2ZGU5MTJlZTJiNGVhNzM2YTlkMWY0NjVhNyJ9fX0=\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2UzZGViNTdlYWEyZjRkNDAzYWQ1NzI4M2NlOGI0MTgwNWVlNWI2ZGU5MTJlZTJiNGVhNzM2YTlkMWY0NjVhNyJ9fX0="),
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
