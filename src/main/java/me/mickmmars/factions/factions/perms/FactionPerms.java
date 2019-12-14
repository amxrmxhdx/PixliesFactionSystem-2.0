package me.mickmmars.factions.factions.perms;

import me.mickmmars.factions.factions.rank.FactionRank;

public enum FactionPerms {

    BUILD("Build", "Build on claimed chunks.", FactionRank.MEMBER),
    RENAME("Rename", "Rename the faction.", FactionRank.ADMIN),
    CONTAINER("Container", "Open containers like chests etc.", FactionRank.MEMBER),
    INVITE("Invite", "Invite people to the faction.", FactionRank.ADMIN),
    INTERACT("Interaction", "Interact with blocks.", FactionRank.MEMBER),
    DEPOSIT("Deposit_money", "Deposit money in the faction treasury.", FactionRank.NONE),
    WITHDRAW("Withdraw_money", "Withdraw money from the faction treasury.", FactionRank.ADMIN),
    RELATION("edit_relations", "Ally/Enemy factions.", FactionRank.ADMIN),
    DESCRIPTION("Description", "Edit description", FactionRank.ADMIN),
    CLAIM("Claim", "Claim territory for faction.", FactionRank.MEMBER),
    UNCLAIM("Unclaim", "Unclaim territory for faction.", FactionRank.ADMIN),
    DISCORD("Discord", "Edit discord link", FactionRank.LEADER),
    MODERATION("Moderation", "Kick people from the faction", FactionRank.ADMIN),
    EDITFLAGS("Edit_flags", "Edit faction-flags.", FactionRank.ADMIN),
    EDITCAPITAL("Edit_capital", "Edit the factions capital.", FactionRank.ADMIN),
    EDITPERMS("Edit_permissions", "Edit the factions permission-system", FactionRank.ADMIN);

    private final String name;
    private final String description;
    private FactionRank startrank;

    FactionPerms(String name, String description, FactionRank startrank) {
        this.description = description;
        this.name = name;
        this.startrank = startrank;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public FactionRank getStartrank() { return startrank; }

    public void setStartrank(FactionRank startrank) {
        this.startrank = startrank;
    }

}
