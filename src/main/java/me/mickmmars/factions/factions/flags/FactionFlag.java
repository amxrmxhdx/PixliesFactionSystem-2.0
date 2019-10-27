package me.mickmmars.factions.factions.flags;

public enum FactionFlag {

    TNT("TNT"),
    PVP("PvP"),
    PERMANENT("Permanent"),
    FIRESPREAD("Firespread"),
    MONSTERDMG("Monster damage"),
    MONSTER("Monster");

    private final String name;

    FactionFlag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
