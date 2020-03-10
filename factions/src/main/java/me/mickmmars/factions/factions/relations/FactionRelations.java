package me.mickmmars.factions.factions.relations;

import me.mickmmars.factions.Factions;

public enum FactionRelations {

    SELF("Self", "§b", false),
    ALLY("Ally", "§d", false),
    TRUCE("Truce", "§5", false),
    PACT("Pact", "§3", false),
    ENEMY("Enemy", "§c", true),
    NEUTRAL("Neutral", "§f", true);

    private Factions instance = Factions.getInstance();

    private final String rel;
    private final String colour;
    private boolean pvp;

    FactionRelations(String rel, String colour, Boolean pvp) {
        this.rel = rel;
        this.colour = colour;
        this.pvp = pvp;
    }

    public String getColour() {
        return colour;
    }

    public Boolean pvpEnabled(String id1, String id2) {
        if (instance.getFactionManager().getFactionById(id1).getAllies().contains(id2)) {
            return ALLY.pvp;
        } else if (instance.getFactionManager().getFactionById(id1).getEnemies().contains(id2) || instance.getFactionManager().getFactionById(id1).getOpposingFactionId().equals(id2)) {
            return ENEMY.pvp;
        } else {
            return NEUTRAL.pvp;
        }
    }

}
