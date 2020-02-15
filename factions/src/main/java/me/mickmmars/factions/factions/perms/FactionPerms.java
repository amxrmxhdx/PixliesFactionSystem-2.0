package me.mickmmars.factions.factions.perms;

public enum FactionPerms {

    BUILD("Build", "Build on claimed chunks."),
    RENAME("Rename", "Rename the faction."),
    CONTAINER("Container", "Open containers like chests etc."),
    INVITE("Invite", "Invite people to the faction."),
    INTERACT("Interaction", "Interact with blocks."),
    DEPOSIT("Deposit_money", "Deposit money in the faction treasury"),
    WITHDRAW("Withdraw_money", "Withdraw money from the faction treasury."),
    RELATION("edit_relations", "Ally/Enemy factions."),
    DESCRIPTION("Description", "Edit description"),
    CLAIM("Claim", "Claim territory for faction."),
    UNCLAIM("Unclaim", "Unclaim territory for faction."),
    DISCORD("Discord", "Edit discord link"),
    MODERATION("Moderation", "Kick people from the faction"),
    EDITFLAGS("Edit_flags", "Edit faction-flags."),
    EDITCAPITAL("Edit_capital", "Edit the factions capital"),
    DYNMAPCOLOUR("Dynmapcolour", "Change the factions colour on the dynmap"),
    FACTIONFLY("Factionfly", "Fly in your factions territory"),
    CHANGE_POLITY("ChangePolity", "Change the factions government-type"),
    SETWARP("SetWarp", "Set warps for your faction."),
    PUPPET("PuppetFacs", "Puppet factions."),
    SETFLAG("SetFlag", "Set the factions flag"),
    SLIMEFUN("Slimefun", "Access slimefun machines"),
    MANAGEPUPPETS("ManagePuppets", "Manage the factions puppets"),
    CB_FACTIONS("CBFactions", "Manage CB's of the faction."),
    EDITPERMS("Edit_permissions", "Edit the factions permission-system");

    private final String name;
    private final String description;

    FactionPerms(String name, String description) {
        this.description = description;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}