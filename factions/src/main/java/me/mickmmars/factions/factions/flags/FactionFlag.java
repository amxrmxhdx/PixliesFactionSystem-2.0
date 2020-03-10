package me.mickmmars.factions.factions.flags;

import java.util.ArrayList;
import java.util.List;

public enum FactionFlag {

    FRIENDLYFIRE("§cFriendly-fire", false),
    PVP("§cPvP", false),
    EXPLOSIONS("§cExplosions", true),
    OPEN("§cOpen/No-invitation", false),
    ANIMALS("§cAnimals", true),
    PERMANENT("§cPermanent", false),
    MONSTER("§cMonsters", false);

    private final String name;
    private Boolean value;
    private static List<FactionFlag> flags = new ArrayList<FactionFlag>();

    FactionFlag(String name, Boolean value) {
        this.name = name;
        this.value = value;
    }

    public Boolean getValue() { return value; }

    public void setValue(Boolean value) { this.value = value; }

    public String getName() {
        return name;
    }
}
