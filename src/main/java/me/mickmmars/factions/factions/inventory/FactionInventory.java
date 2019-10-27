package me.mickmmars.factions.factions.inventory;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.ItemBuilder;
import me.mickmmars.factions.SkullBuilder;
import me.mickmmars.factions.factions.data.FactionData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FactionInventory {

    private Factions instance = Factions.getInstance();

    private final UUID uuid;
    private Inventory inventory;
    private Player player;

    private final int[] slots = {19, 21, 23, 25};

    public FactionInventory(UUID uuid) {
        this.uuid = uuid;
        player = Bukkit.getPlayer(uuid);
    }

    public FactionInventory setItems() {
        inventory = Bukkit.createInventory(null, 9 * 5, "§a§lFactions menu" + (instance.getPlayerData(player).isInFaction() ? " §8» §b§o" + instance.getFactionManager().getFactionById(instance.getPlayerData(player).getFactionId()).getName() : ""));

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setNoName().build());

        for (int i = 0; i < slots.length; i++) {
            GUIPage page = GUIPage.values()[i];
            if (page.equals(GUIPage.MEMBERS))
                inventory.setItem(slots[i], new ItemBuilder(page.getMaterial()).setDisplayName(page.getName()).build());
            else
                inventory.setItem(slots[i], new SkullBuilder(page.getSkinData()[0], page.getSkinData()[1]).setDisplayname(page.getName()).build());
        }
        return this;
    }

    public FactionInventory setItems(GUIPage page) {
        inventory = Bukkit.createInventory(null, 9 * 5, "§a§l" + page.getName());

        switch (page) {
            case LIST:
                for (FactionData faction : instance.getFactionManager().getFactions()) {
                    List<String> description = new ArrayList<>();
                    for (int i = 0; i < faction.getDescription().length(); i++) {

                    }
                    inventory.addItem(new ItemBuilder(Material.DIAMOND).setDisplayName("§b§o" + faction.getName()).build());
                }
                break;
        }

        inventory.setItem(40, new SkullBuilder(Factions.Skulls.BACK_ITEM[1], Factions.Skulls.BACK_ITEM[0]).setDisplayname("Back").build());

        return this;
    }

    public FactionInventory removeBackItem() {
        inventory.setItem(40, null);
        return this;
    }

    public void load() {
        player.openInventory(inventory);
    }

    public enum GUIPage {
        LIST("§b§oList", Material.PLAYER_HEAD, "{display:{Name:\\\"Papers\\\"},SkullOwner:{Id:\\\"08a21c84-663b-4638-a31e-2f0423a3853f\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OTJmOTljYzZkNzgyNDIzMDQxMTA1NTM1ODk0ODQyOThiMmU0YTAyMzNiNzY3NTNmODg4ZTIwN2VmNSJ9fX0=\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OTJmOTljYzZkNzgyNDIzMDQxMTA1NTM1ODk0ODQyOThiMmU0YTAyMzNiNzY3NTNmODg4ZTIwN2VmNSJ9fX0="),
        MEMBERS("§b§oMember", Material.PLAYER_HEAD),
        PERMISSIONS("§b§oPermissions", Material.PLAYER_HEAD, "{display:{Name:\\\"Winrar Books\\\"},SkullOwner:{Id:\\\"5fdf61b2-71bf-456e-864c-45f7dfeadf38\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBlYWJhZTc2Mjk0ZGFiNTM2NjI3ZGIyMTk3ZWE0YzkyNWFjNmJmN2VhMDNkOTVkMmYxNGUxNzI1NTFlY2I0YiJ9fX0=\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBlYWJhZTc2Mjk0ZGFiNTM2NjI3ZGIyMTk3ZWE0YzkyNWFjNmJmN2VhMDNkOTVkMmYxNGUxNzI1NTFlY2I0YiJ9fX0="),
        SETTINGS("§b§oSettings", Material.PLAYER_HEAD, "{display:{Name:\\\"Command Block (impulse)\\\"},SkullOwner:{Id:\\\"a36e6694-fa87-48d2-abdb-62eaf2b6711d\\\",Properties:{textures:[{Value:\\\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY0YzIxZDE3YWQ2MzYzODdlYTNjNzM2YmZmNmFkZTg5NzMxN2UxMzc0Y2Q1ZDliMWMxNWU2ZTg5NTM0MzIifX19\\\"}]}}}", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY0YzIxZDE3YWQ2MzYzODdlYTNjNzM2YmZmNmFkZTg5NzMxN2UxMzc0Y2Q1ZDliMWMxNWU2ZTg5NTM0MzIifX19");

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
    }

}
