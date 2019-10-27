package me.mickmmars.factions.factions.rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum FactionRank {

    LEADER("Leader"),
    ADMIN("Admin"),
    MEMBER("Member"),
    NEWBIE("Newbie"),
    NONE("");

    private final String name;

    FactionRank(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static int getRankId(FactionRank rank) {
        final List<FactionRank> ranks = new ArrayList<>(Arrays.asList(values()));
        for (int i = 0; i < ranks.size(); i++)
            if (ranks.get(i).equals(rank))
                return (FactionRank.values().length - i) - 1;
        return 0;
    }

}
