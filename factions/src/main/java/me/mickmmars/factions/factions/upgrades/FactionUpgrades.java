package me.mickmmars.factions.factions.upgrades;

import me.mickmmars.factions.Factions;

public enum FactionUpgrades {

    ONEPUBLICWARP("onepublicwarp", "One public-warp", 25),
    TWOPUBLICWARPS("twopublicwarps", "Two public-warps", 50),
    THREEPUBLICWARPS("threepublicwarps", "Three public-warps", 100),
    DYNMAPCOLOUR("dynmapcolour", "Change dynmap-colour", 150);

    private Factions instance = Factions.getInstance();

    private final String name;
    private final String guiname;
    private final int price;

    FactionUpgrades(String name, String guiname, int price) {
        this.name = name;
        this.guiname = guiname;
        this.price = price;
    }

    public String getName() { return name; }
    public String getGuiname() { return guiname; }
    public int getPrice() { return price; }
}
