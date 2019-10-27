package me.mickmmars.factions.factions.relations;

public enum FactionRelations {

    SELF("Self"),
    ALLY("Ally"),
    TRUCE("Truce"),
    PACT("Pact"),
    ENEMY("Enemy"),
    NEUTRAL("Neutral");

    private final String rel;

    FactionRelations(String rel) {
        this.rel = rel;
    }

    public String getRel() {
        return rel;
    }

}
