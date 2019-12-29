package me.mickmmars.factions.message;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import me.mickmmars.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public enum Message {

    PREFIX("§8» ", ""),
    NO_PERMISSIONS("§cInsufficient permissions!", ""),
    HOME_MUST_BE_WITHIN_FAC_TERRITORY("§cYour faction home must be within your faction territory.", ""),
    INVITE_REVOKED("§7You §crevoked §7the invitation you sent to §6%player%§7.", ""),
    FAC_HOME_SUCCESSFULLY_SET("§7You have §asuccessfully §7set your factions home at §c%loc%", ""),
    PLAYER_SET_FAC_HOME("§7Player §6%player% §7has set your factionhome at §c%loc%", ""),
    ALREADY_HAS_FAC("§cYou already have a faction.", ""),
    ALREADY_IN_FAC("&7You have to leave your current faction to join a new one. &c/f leave", ""),
    NO_PERMISSION_TO_INTERACT("§cThis faction does not allow you to interact with their territory.", ""),
    NAME_ALREADY_TAKEN("&cYou can't use that name because it's already taken.", ""),
    UNKNOWN_ERROR("§cUnknown error. Please contact your server administrator.", ""),
    FACTION_CREATED("&7You successfully created &b%faction_name%&7!", ""),
    FACTION_CREATION_ERROR("&cError while creating new faction.", ""),
    NO_INVITE_PERM("&cYour faction does not allow you to invite members.", ""),
    PLAYER_ALREADY_INVITED("&cThis player has already been invited to your faction. &e/f invite revoke &b%target%", ""),
    SUCCESSFULLY_INVITED("&7You successfully invited &a&l%target% &7to your faction!", ""),
    PLAYER_GOT_INVITED("&b&l%faction% &7invited you to join their faction.", ""),
    BANK_BALANCE("&7Your faction has §2§l$&a%balance% &7in their bank.", ""),
    PLAYER_ALREADY_HAS_FAC("§cThis player already has a faction.", ""),
    CANT_HIT_FAC_MEMBER("§cYou may not hit people from your faction.", ""),
    CANT_INVITE_YOURSELF("§cYou can't invite yourself.", ""),
    FEATURE_NOT_FINISHED("§cWe are still working on this feature.", ""),
    NEED_MORE_TO_CLAIM("§7Your faction needs §2§l$§a%need% §7more to claim this chunk.", ""),
    PLAYER_NOT_IN_YOUR_FAC("§cThis player is not in your faction.", ""),
    NOPERM_KICK("&cYour faction does not allow you to kick players.", ""),
    NOT_IN_A_FACTION("§cYou are not in a faction.", ""),
    NO_DEMOTION_PERM("§cYour faction does not allow you to demote people.", ""),
    PROMOTION_NOT_POSSIBLE("§cYou can't promote a player to a rank higher than Admin. §e/f handover (player)", ""),
    PLAYER_GOT_DEMOTED("§a%player% §7got demoted to %rank% §7in your faction.", ""),
    PLAYER_NOT_FOUND("§a§l$player$ §7does not exist!", ""),
    PLAYER_NOT_IN_YOUR_FACTION("§a§l$player$ §7is not in your faction!", ""),
    PLAYER_GOT_PROMOTED("§a%player% §7got promoted to %rank% §7in your faction!", ""),
    YOU_GOT_PROMOTED("§7You got promoted to %rank% §7in your faction.", ""),
    YOU_GOT_DEMOTED("§7You got demoted to %rank% §7in your faction.", ""),
    DEMOTION_NOT_POSSIBLE("§cThis player cannot be demoted.", ""),
    CHUNK_IS_NOT_YOURS("§cYou can't unclaim what's not yours.", ""),
    NO_UNCLAIM_PERM("§cYour faction does not allow you to unclaim territory.", ""),
    CANT_LEAVE("§cYou can't leave your faction if you are the leader. §e/f delete", ""),
    SUCCESSFULLY_LEFT_FACTION("§7You §asuccessfully §7left §b%faction%§7.", ""),
    PLAYER_UNCLAIMED("§7Player §6%player% §7unclaimed at §b%location%", ""),
    FACTION_NOT_FOUND("§cThis faction does not exist", ""),
    CONFIRM_DISBAND("§cAre you sure you want to delete that faction? All data will be gone forever (That's a long time!)", ""),
    CONFIRM_DISBAND_HOVER(" §c/f delete confirm", ""),
    FACTION_DISBANDED("§b§l$faction$ §cgot disbanded!", ""),
    PLAYER_KICKED("§a§l$player$ §7got kicked from your faction!", ""),
    ALREADY_CLAIMED("§cThis chunk is already claimed.", ""),
    NO_CLAIMING_POWER("§cYour faction has no power left to claim this chunk.", ""),
    CLAIM_QUESTIONMARK("§c§lClaim?", ""),
    CLICK_ME_TO_CLAIM("§b§lClick me to claim!", ""),
    PLAYER_NOT_LEADER("§cOnly your faction-leader can handover the ownership.", ""),
    PLAYER_ALREADY_LEADER("§cYou can't handover the faction to yourself.", ""),
    LEADERSHIP_TRANSFERED("§6%player% §7transferred §b%faction%§7's leadership to §6%target%§7.", ""),
    YOU_CANT_KICK_YOURSELF("§cYou can't kick yourself out of your faction.", ""),
    YOU_KICKED_PLAYER("§7You §asuccessfully §7kicked §6%player% §7from your faction.", ""),
    NO_PROMOTE_PERM("§cYour faction does not allow you to promote players.", ""),
    CHUNK_IS_FREE("§aChunk is free", ""),
    YOU_GOT_KICKED("§cYou got kicked from §b§l$faction$", ""),
    CHANGED_FAC_DESCRIPTION("§7Player §a§l$player$ §7changed your faction description to §b$desc$", ""),
    PAGE_DOES_NOT_EXISTS("§cThis page doesn't exists!", ""),
    FACTION_CHAT_ENABLED("§aEnabled §7factionchat.", ""),
    FACTION_CHAT_DISABLED("§cDisabled §7factionchat.", ""),
    NOT_A_NUMBER("§cPlease enter a number!", ""),
    HOME_SET_SUCCESSFULLY("§7Home §b%home% §7successfully set.", ""),
    HOME_REMOVE_SUCCESSFULLY("§7You §asuccessfully §7removed §b%home%§7.", ""),
    HOME_MAX("§cYou are only able to set {0} homes at the moment!", ""),
    HOME_ALREADY_EXISTS("§cA home with this name already exists!", ""),
    HOME_DOES_NOT_EXISTS("§cYou don't have a home with this name!", ""),
    HOMES_LEFT("§7You have §a%left% §7home(s) left.", ""),
    PLAYER_CLAIMED("§7Player §6%player% §7claimed at §b%location%", ""),
    HOME_LIST_FORMAT("   §8- §e%home%", ""),
    HOME_CLICK_TO_TELEPORT("§7Click here to teleport to home §a%home%", ""),
    NO_HOMES("§cYou don't have any homes at the moment.", ""),
    YOUR_HOMES("§7Your current homes §8(§a%size%§8):", ""),
    BANK_DEPOSIT("§aSuccessfully §7deposited §b$$deposit$§7.", ""),
    TRANSACTION_ERROR("§cYou don't have enough money for this action.", ""),
    BANK_WITHDRAW("§aSuccessfully §7withdrew §b$withdraw$§7.", ""),
    NOT_ENOUGH_MONEY_IN_FAC("§cYou don't have enough money in the faction.", ""),
    NO_CLAIM_PERM("§cYour faction does not allow you to claim territory.", ""),
    FACTION_DISCORD("§7Join your factions §dDiscord-server§7! §b%link%", ""),
    AUTOCLAIM_ENABLED("§7Autoclaim §aenabled§7.", ""),
    AUTOCLAIM_DISABLED("§7Autoclaim §cdisabled§7.", ""),
    AUTOUNCLAIM_ENABLED("§7Autounclaim §aenabled§7.", ""),
    AUTOUNCLAIM_DISABLED("§7Autounclaim §cdisabled§7.", ""),
    STAFFMODE_ENABLED("§7Staffmode §aenabled§7.", ""),
    STAFFMODE_DISABLED("§7Staffmode §cdisabled§7.", ""),
    MAX_MEMBER_COUNT("§cYour faction already has the maximum amount of members.", ""),
    NOPERM_INTERACT_FAC("§cYour faction does not allow you to interact with territory.", ""),
    NO_PERM_CHANGENAME("§cYour faction does not allow you to change its name.", ""),
    NO_PERM_CHANGEDISCORD("§cYour faciton does not allow you to change its discordlink.", ""),
    SAFEZONE_CLAIMED("§7You §asuccessfully §7claimed §aSafeZone §7at §6%loc%§7.", ""),
    ALREADY_SENT_ALLYREQUEST("§cYou already sent a ally-request to this faction.", ""),
    NO_PERMISSION_TO_ALLY("§cYour faction does not allow you ally factions.", ""),
    FACTIONS_ALLIED("§7Your faction is now allied with §5%ally%§7.", ""),
    CANT_HIT_ALLY("§cYou can't hit an ally!", ""),
    CANT_ALLY_SAFEZONE("§7You are not funny when you try to ally the §aSafeZone.", ""),
    CANT_ALLY_WILDERNESS("§7Allying the wilderness is not possible...", ""),
    ALREADY_ALLIED("§7You already are §5allied §7with §d%ally%§7.", ""),
    NO_PERMISSION_TO_NEUTRAL("§cYour faction does not allow you to neutral factions.", ""),
    NO_RELATION("§cYou have no relation to that nation.", ""),
    ALLY_REQUEST_REVOKED("§6%player% §7revoked your factions ally-request to §a%faction%§7.", ""),
    FACTION_REVOKED_ALLY_REQUEST("§a%faction% §7revoked their ally-request to your faction.", ""),
    FACTION_NEUTRALED("§7Your faction now has neutral relations with §a%faction%§7.", ""),
    FACTION_DOESNT_EXIST("§cThis faction does not exist.", ""),
    SENT_ALLY_REQUEST("§6%player% §7sent an ally-request to §d%ally%§7.", ""),
    RECIEVED_ALLY_REQUEST("§7You recieved an ally-request by §a%requester%§7.", ""),
    CANT_ALLY_YOURSELF("§cYou can't ally yourself!", ""),
    CANT_DAMAGE_IN_OWN_TERRITORY("§cYou can't damage people in their territory!", ""),
    CREATED_SAFEZONE("§7You created the §aSafeZone §7faction.", ""),
    SAFEZONE_ALREADY_CREATED("§cSafeZone faction already exists!", ""),
    CHANGED_DISCORDLINK("§7Player §6%player% §7changed your discordlink to §b%link%§7.", ""),
    WRONG_DISCORD_LINK("§cWrong formatting! §7Use this command like this: §a/f discord discord.gg/YourInvite", ""),
    PLAYER_CHANGED_FACTIONNAME("§7Player §6%player% §7changed your factions name to §b%name%§7.", ""),
    PLAYER_ENTERED_TERRITORY("§c%player% §7entered your faction-territory!", ""),
    FACTION_DOENST_ALLOW_YOU_ENEMY("§cYour faction does not allow you to enemy people.", ""),
    ALREADY_ENEMIED("§cYou are already enemied with this faction." ,""),
    ENEMIED_FACTIONS("§7Your faction is now enemied with §4%faction%§7.", ""),
    ENEMY_SAFEZONE("§7You are not funny when you try to enemy the §aSafeZone§7.", ""),
    PLAYER_FILLED("§7Player §6%player% §7claimed §a%chunks% §7chunks for your faction.", ""),
    FACTION_FORCEDISBANDED("§7Player §6%player% §7force-disbanded §c%faction%§7.", ""),
    CANT_ENEMY_YOURSELF("§cYou can't enemy yourself.", ""),
    HOME_HAS_TO_BE_IN_TERRITORY("§cFaction-home cannot be outside of the faction-territory.", ""),
    PLAYER_SETTED_FACTION_HOME("§6%player% §7has set your faction-home in §a%loc%", ""),
    NO_PERMISSION_TO_SETHOME("§cYour faction does not allow you to set homes.", ""),
    FACTION_HAS_NO_HOME("§cYour faction has not set a home.", ""),
    TELEPORTED_TO_FAC_HOME("§7You §asuccessfully §7teleported yourself to your factions home.",  ""),
    DELETED_HOME("§6%player% §7deleted your factions home.", ""),
    CANT_TP_HOME_BECAUSE_NO_ALLY("§cYou can only teleport to your allies homes.", ""),
    TELEPORTED_TO_ELSE_FAC_HOME("§7You got teleported to §d%fac%§7's home.", ""),
    PLAYER_CHANGED_FLAG("§6%player% §7changed the flag '%flag%§7' in your faction to %value%§7.", ""),
    NO_FLAG_PERM("§cYou faction does not allow you to edit flags.", ""),
    FORCE_JOINED_FAC("§7You §asuccessfully §7force-joined §b%fac%§7.", ""),
    RELATION_LIST_HEADLINE("§8-§b+§8-------§7[ §a§lRelations §7]§8------§b+§8-", ""),
    GROUP_DOES_NOT_EXIST("§7This group §cdoes not §7exist. Available groups: §c§lAdmin§8, §a§lMember§8, §3Newbie§8, §d§lAlly§8, §4§lEnemy", ""),
    PERMISSION_DOES_NOT_EXIST("§7This permission §cdoes not §7exist. §b/f perms list", ""),
    PLAYER_ALREADY_HAS_ACCESS_TO_CHUNK("§6%player% §7already has access to this chunk.", ""),
    GAVE_CHUNKACCESS_TO_PLAYER("§6%player% §7now has access to this chunk.", ""),
    PLAYER_GAINED_CHUNK_ACCESS("§7You §agained §7access to chunk: §a%loc%", ""),
    NO_PERMISSION_TO_GIVE_ACCESS_TO_CHUNK("§cYour faction does not allow you to give players access to faction-territory.", ""),
    NO_PERMISSION_TO_EDIT_PERMISSIONS("§cYour faction does not allow you to edit permissions.", ""),
    RANK_DOES_NOT_EXIST("§7This rank §cdoes not §7exist. Available ranks: §c§lAdmin§8, §a§lMember§8, §3Newbie", ""),
    PLAYER_ADDED_PERMISSION_TO_GROUP_FAC("§6%player% §7added the permission §b%perm% §7to the rank %rank%§7.", ""),
    PLAYER_REMOVED_PERMISSION_FROM_GROUP_FAC("§6%player% §7removed the permission §b%perm% §7from the rank %rank%§7.", ""),
    ALREADY_HAS_PREMISSION("§7This group §calready §7has that permission.", ""),
    RANK_DOESNT_HAVE_PERMISSION("§7This group §cdoes not §7have that permission.", ""),
    NO_PERMISSION_TO_DELETE_CAPITAL("§cYour faction does not allow you to delete the capital.", ""),
    APPLICATION_SUCCESSFULLY_SENT("§7You §asuccessfully §7sent an application to §b%faction%§7.", ""),
    PLAYER_SENT_APPLICATION("§6%player% §7sent an citizen-application to your faction. §a/f accept <name>", ""),
    PLAYER_DOESNT_EXIST("§7This player §cdoes not §7exist.", ""),
    APPLICATION_DOESNT_EXIST("§7This player never sent an application to your faction.", ""),
    PLAYER_JOINED_YOUR_FACTION("§7Player §6%player% §7joined your faction.", ""),
    SUCCESSFULLY_JOINED_FACTION("§7You §asuccessfully §7joined §b%faction%§7.", ""),
    NO_PERM_DYNMAPCOLOUR("§cYour faction does not allow you to change the dynmap-colour.", ""),
    CHANGED_DYNMAPCOLOUR("§6%player% §7changed your factions dynmapcolour to %col%§7.", ""),
    AVAILABLE_COLOURS_DYNCOL("§7Usage: /f dynmapcolour <§b§lBlue§8/§4§lRed§8/§6§lOrange§8/§a§lGreen§7>", ""),
    PLAYER_PURCHASED_UPGRADE("§6%player% §7purchased the §a%upgrade% §7upgrade for your faction.", ""),
    NO_FLY_UPGRADE("§cYour faction needs the Faction-Fly upgrade for you to use this command.", ""),
    FFLY_ACTIVATED("§7You §aactivated §7Faction-Fly.", ""),
    FFLY_DEACTIVATED("§7You §cdeactivated §7Faction-Fly.", ""),
    NO_PERM_FOR_FFLY("§cYour faction does not allow you to fly in your territory.", ""),
    PLAYER_WAS_NEVER_INVITED("§7You §cdont have §7any open invitation by that faction.", ""),
    CLAIM_FILLED_X_CHUNKS("§7You §asuccessfully §7claim-filled §a%x% §7chunks.", ""),
    MAX_FILL_REACHED("§7You §creached §7the maximum capacity of the fill function.", ""),
    PLAYER_FILL_CLAIMED_BROADCASTFAC("§6%player% §7used the fill-claim tool to claim §a%x% §7chunks.", ""),
    NO_DYNMAP_UPGRADE("§7Your faction §cdoes not §7have the Dynmapcolour upgrade!", ""),
    NO_1PW_UPGRADE("§7Your faction §cdoes not §7have the OnePublicWarp upgrade!", ""),
    NO_2PW_UPGRADE("§7Your faction §cdoes not §7have the TwoPublicWarp upgrade!", ""),
    NO_3PW_UPGRADE("§7Your faction §cdoes not §7have the ThreePublicWarp upgrade!", ""),
    TOO_MANY_WARPS("§7Your faction §ccannot §7have more than 3 warps.", ""),
    NO_PERM_SETWARP("§7Your faction §cdoes not §7allow you to set warps.", ""),
    WARP_MUST_BE_IN_TERRITORY("§7Faciton warps must be located within faction territory.", ""),
    WARP_SET("§6%player% §7set the warp §b%warp%§7.", ""),
    PLAYER_BANNED("§7You are §cbanned §7from this faction.", ""),
    WARP_DOESNT_EXIST("§7This warp §cdoes not §7exist.", ""),
    DEV_VERSION("§bDEV #305", null),
    HOME_HELP_PAGES(Lists.newArrayList(Factions.getInstance().getPrettyGson().toJson(new Gson().toJsonTree(new HelpPage(Lists.newArrayList(
            " "
            , "§7Home Commands §8[§e$page$§7/§e$max_pages$§8]",
            "§8» §b/home set [name] §8- §7Set a home",
            "§8» §b/home delete [name] §8- §7Delete a home",
            "§8» §b/home list §8- §7List your homes",
            "§8» §b/home (teleport) [name] §8- §7Teleport you to a home",
            " "
    ))))), ""),
    ;


    private Object defaultMessage;
    private Object message;

    Message(Object defaultMessage, Object message) {
        this.defaultMessage = defaultMessage;
        this.message = message;
    }

    public Object getMessageRaw() {
        return message;
    }

    public Object getDefaultMessage() {
        return defaultMessage;
    }

    public String getMessage() {
        return PREFIX.getMessageRaw() + ((String) message).replace("&", "§").replace("$online$", String.valueOf(Factions.getInstance().getPlayers().size())).replace("$max$", String.valueOf(Bukkit.getMaxPlayers()));
    }

    public String getMessage(final Player player) {
        return PREFIX.getMessageRaw() + ((String) message).replace("&", "§").replace("$player$", player.getName()).replace("$online$", String.valueOf(Factions.getInstance().getPlayers().size())).replace("$max$", String.valueOf(Bukkit.getMaxPlayers()));
    }

    public String getMessage(final String player) {
        return PREFIX.getMessageRaw() + ((String) message).replace("&", "§").replace("$player$", player).replace("$online$", String.valueOf(Factions.getInstance().getPlayers().size())).replace("$max$", String.valueOf(Bukkit.getMaxPlayers()));
    }

    public String getMessage(Object... arguments) {
        return PREFIX.getMessageRaw() + MessageFormat.format((String) message, arguments);
    }

    public void setMessage(final Object message) {
        this.message = message;
    }

}
