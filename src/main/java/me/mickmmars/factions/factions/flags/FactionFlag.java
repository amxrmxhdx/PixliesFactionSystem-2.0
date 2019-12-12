package me.mickmmars.factions.factions.flags;

import java.util.ArrayList;
import java.util.List;

public enum FactionFlag {

    TNT("§cTNT", true),
    PVP("§cPvP", false),
    PERMANENT("§cPermanent", false),
    FIRESPREAD("§cFirespread", true),
    MONSTERDMG("§cMonster-damage", true),
    FLY("§cFaction-Fly", false),
    MONSTER("§cMonster", false);

    private final String name;
    private Boolean value;
    private static List<FactionFlag> flags = new ArrayList<FactionFlag>();

    FactionFlag(String name, Boolean value) {
        this.name = name;
        this.value = value;
    }

    public Boolean getValue() { return value; }

    public void setValue(Boolean value) { this.value = value; }

    public static List<FactionFlag> getFlags() {
        flags.add(TNT);
        flags.add(PVP);
        flags.add(PERMANENT);
        flags.add(FIRESPREAD);
        flags.add(MONSTERDMG);
        flags.add(FLY);
        flags.add(MONSTER);
        return flags;
    }

    public String getName() {
        return name;
    }
}
