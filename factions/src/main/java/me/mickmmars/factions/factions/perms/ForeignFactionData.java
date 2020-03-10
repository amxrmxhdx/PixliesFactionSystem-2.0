package me.mickmmars.factions.factions.perms;

public class ForeignFactionData {

    public String userFaction;
    public String hostingFaction;
    public String perm;

    public ForeignFactionData(String userFaction, String hostingFaction, String perm) {
        this.userFaction = userFaction;
        this.hostingFaction = hostingFaction;
        this.perm = perm;
    }

}
