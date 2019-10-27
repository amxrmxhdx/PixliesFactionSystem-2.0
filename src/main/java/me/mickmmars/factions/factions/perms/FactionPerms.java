package me.mickmmars.factions.factions.perms;

public enum FactionPerms {

    BUILD("Build"),
    CONTAINER("Container"),
    INVITE("Invite"),
    DOORS("Doors"),
    DEPOSIT("Deposit money"),
    WITHDRAW("Withdraw money"),
    DISBAND("Disband"),
    DESCRIPTION("Edit description"),
    EDITPERMS("Edit permissions");

    private final String perm;

    FactionPerms(String perm) {
        this.perm = perm;
    }


    public String getPerm() {
        return perm;
    }
}
