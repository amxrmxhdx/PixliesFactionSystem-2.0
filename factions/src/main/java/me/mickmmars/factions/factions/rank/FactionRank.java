package me.mickmmars.factions.factions.rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum FactionRank {

    LEADER("§b§lLeader", "§b+"),
    ADMIN("§c§lAdmin", "§c***"),
    MEMBER("§aMember", "§a**"),
    NEWBIE("§3Newbie", "§3*"),
    NONE("", "");

    private final String name;
    private final String defaultPrefix;

    FactionRank(String name, String defaultPrefix) {
        this.name = name;
        this.defaultPrefix = defaultPrefix;
    }

    public String getName() {
        return name;
    }
    public String getDefaultPrefix() { return defaultPrefix; }

    public static int getRankId(FactionRank rank) {
        final List<FactionRank> ranks = new ArrayList<>(Arrays.asList(values()));
        for (int i = 0; i < ranks.size(); i++)
            if (ranks.get(i).equals(rank))
                return (FactionRank.values().length - i) - 1;
        return 0;
    }

    public List<FactionRank> getRanks() {
        List<FactionRank> ranks = new ArrayList<FactionRank>();
        ranks.add(LEADER);
        ranks.add(ADMIN);
        ranks.add(MEMBER);
        ranks.add(NEWBIE);
        ranks.add(NONE);
        return ranks;
    }

    public FactionRank getRankByName(String name) {
        for (FactionRank rank : this.getRanks()) {
            if (rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }
        return null;
    }

}
