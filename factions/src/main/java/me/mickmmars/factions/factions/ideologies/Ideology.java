package me.mickmmars.factions.factions.ideologies;

import me.mickmmars.factions.factions.upgrades.FactionUpgrades;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum Ideology {

    THEOCRACY("§2Theocracy", 0.0, DyeColor.GREEN, null),
    MONARCHY("§6Monarchy", 0.0, DyeColor.YELLOW, null),
    DEMOCRATIC("§bDemocratic", 0.0, DyeColor.CYAN, null),
    COMMUNISM("§4Communist", 30000.0, DyeColor.RED, "communism"),
    FASCISM("§9Fascist", 30000.0, DyeColor.BLUE, "fascism"),
    ANARCHOCAPITALISM("§eAnarcho-Capitalism", 5000.0, DyeColor.LIGHT_BLUE, "anarchocapitalism"),
    ENVIRONMENTALISM("§aEnvironmentalism", 3000.0, DyeColor.LIME, "environmentalism"),
    FEMINISM("§cFeminism", 1000.0, DyeColor.PINK, "feminism"),
    PROGRESSIVISM("§bProgressive", 10000.0, DyeColor.LIGHT_BLUE, "progressive"),
    CONSERVATIVISM("§cConservative", 10000.0, DyeColor.RED, "conservative"),
    SOCIALISM("§cSocialism", 15000.0, DyeColor.RED, "socialism"),
    OLIGARCHY("§6Oligarchy", 5000.0, DyeColor.YELLOW, "oligarchy");

    private String name;
    private double price;
    private DyeColor color;
    private String upgrade;

    Ideology(String name, double price, DyeColor color, String upgrade) {
        this.name = name;
        this.price = price;
        this.color = color;
        this.upgrade = upgrade;
    }

    public String getName() { return name; }

    public double getPrice() { return price; }

    public DyeColor getColor() { return color; }

    public String getUpgrade() { return upgrade; }

    public static List<Ideology> listIdeologies() {
        List<Ideology> ideologies = new ArrayList<Ideology>();
        ideologies.add(THEOCRACY);
        ideologies.add(MONARCHY);
        ideologies.add(DEMOCRATIC);
        ideologies.add(COMMUNISM);
        ideologies.add(FASCISM);
        ideologies.add(ANARCHOCAPITALISM);
        ideologies.add(ENVIRONMENTALISM);
        ideologies.add(FEMINISM);
        ideologies.add(PROGRESSIVISM);
        ideologies.add(CONSERVATIVISM);
        ideologies.add(SOCIALISM);
        ideologies.add(OLIGARCHY);
        return ideologies;
    }

    public static List<Ideology> listFreeIdeologies() {
        List<Ideology> ideologies = new ArrayList<Ideology>();
        ideologies.add(THEOCRACY);
        ideologies.add(MONARCHY);
        ideologies.add(DEMOCRATIC);
        return ideologies;
    }

    public static Ideology getIdeologyByName(String name) {
        for (Ideology ideology : listIdeologies())
            if (ideology.getName().equalsIgnoreCase(name))
                return ideology;
        return null;
    }

}
