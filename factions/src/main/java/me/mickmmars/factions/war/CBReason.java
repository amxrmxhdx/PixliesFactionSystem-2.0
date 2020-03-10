package me.mickmmars.factions.war;

import java.util.ArrayList;
import java.util.List;

public enum CBReason {

    TRESPASSING("Trespassing", "When somebody enters your faction without your agreement."),
    EXPANSION("Expansion", "If your neighbor nation is min. 3 times as big as you and you cant claim anymore land nearby, you can request this CB"),
    ROLEPLAY("Roleplay", "You can request this CB if both of your factions verify that they want to have a war together."),
    REVENGE("Revenge", "In order to get this CB, the war this CB is based of, has to be min. 10 minutes long. Active for 2 weeks, bound to the faction."),
    MURDER("Murder", "If you or one of your faction members gets murdered in wilderness, your or another faction its a murder CB. This is not valid if you were killed while caught trespassing"),
    BROKEN_TREATY("BrokenTreaty", "If you make a deal/int. treaty with someone and he doesn't fulfil his responsibility. Treaties are signed in #» Diplocmacy in discord"),
    IDEOLOGY("Ideology", "If your nation is fascist you can get a CB on communist nations, and also the other way round.");

    private String name;
    private String description;

    CBReason(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    public static List<CBReason> listCbs() {
        List<CBReason> cbs = new ArrayList<>();
        cbs.add(TRESPASSING);
        cbs.add(EXPANSION);
        cbs.add(ROLEPLAY);
        cbs.add(REVENGE);
        cbs.add(MURDER);
        cbs.add(BROKEN_TREATY);
        cbs.add(IDEOLOGY);
        return cbs;
    }

    public static CBReason getReasonByName(String name) {
        for (CBReason cb : listCbs())
            if (cb.getName().equalsIgnoreCase(name))
                return cb;
        return null;
    }


}
