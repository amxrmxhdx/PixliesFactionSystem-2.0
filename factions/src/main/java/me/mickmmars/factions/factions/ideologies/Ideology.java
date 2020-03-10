package me.mickmmars.factions.factions.ideologies;

import java.util.ArrayList;
import java.util.List;

public enum Ideology {

    THEOCRACY("§2Theocracy"),
    MONARCHY("§6Monarchy"),
    DEMOCRATIC("§bDemocratic"),
    COMMUNIST("§4Communist"),
    FASCIST("§9Fascist");

    private String name;

    Ideology(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public static List<Ideology> listIdeologies() {
        List<Ideology> ideologies = new ArrayList<Ideology>();
        ideologies.add(THEOCRACY);
        ideologies.add(MONARCHY);
        ideologies.add(DEMOCRATIC);
        ideologies.add(COMMUNIST);
        ideologies.add(FASCIST);
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
