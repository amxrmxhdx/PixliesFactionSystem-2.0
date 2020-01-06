package me.mickmmars.factions.factions.upgrades;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.config.Config;

public enum FactionUpgrades {

    ONEPUBLICWARP("onepublicwarp", "One public-warp", (Double) Config.ONE_PUBLIC_WARP_PRICE.getData()),
    TWOPUBLICWARPS("twopublicwarps", "Two public-warps", (Double) Config.TWO_PUBLIC_WARPS_PRICE.getData()),
    THREEPUBLICWARPS("threepublicwarps", "Three public-warps", (Double) Config.THREE_PUBLIC_WARPS_PRICE.getData()),
    FACTION_FLY("factionfly", "Faction fly", (Double) Config.FACTION_FLY_PRICE.getData()),
    DYNMAPCOLOUR("dynmapcolour", "Change dynmap-colour", (Double) Config.DYNMAPCOLOUR_PRICE.getData());

    private Factions instance = Factions.getInstance();

    private final String name;
    private final String guiname;
    private final Double price;

    FactionUpgrades(String name, String guiname, Double price) {
        this.name = name;
        this.guiname = guiname;
        this.price = price;
    }

    public String getName() { return name; }
    public String getGuiname() { return guiname; }
    public Double getPrice() { return price; }
}
