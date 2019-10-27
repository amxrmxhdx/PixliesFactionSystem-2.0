package me.mickmmars.factions.message;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import me.mickmmars.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public enum Message {

    PREFIX("§8»", ""),
    NO_PERMISSIONS("§c§oInsufficient permissions!", ""),
    NOT_IN_A_FACTION("§c§oYou are not in a faction.", ""),
    PLAYER_NOT_FOUND("§a§l§o$player$ §7§odoes not exist!", ""),
    PLAYER_NOT_IN_YOUR_FACTION("§a§l§o$player$ §7§ois not in your faction!", ""),
    FACTION_NOT_FOUND("§c§oThis faction does not exist", ""),
    CONFIRM_DISBAND("§c§oAre you sure you want to delete that faction? All data will be gone forever (That's a long time!) §e§o/f delete confirm", ""),
    FACTION_DISBANDED("§b§l§o$faction$ §c§ogot disbanded!", ""),
    PLAYER_KICKED("§a§l§o$player$ §7§ogot kicked from your faction!", ""),
    YOU_GOT_KICKED("§c§oYou got kicked from §b§l§o$faction$", ""),
    CHANGED_FAC_DESCRIPTION("§7§oPlayer §a§l§o$player$ §7§ochanged your faction description to §b§o$desc$", ""),
    PAGE_DOES_NOT_EXISTS("$prefix$ §cThis page doesn't exists!", ""),
    NOT_A_NUMBER("$prefix$ §cPlease enter a number!", ""),
    HOME_SET_SUCCESSFULLY("$prefix$ §7§oHome §b§o{0} §7§osuccessfully set.", ""),
    HOME_REMOVE_SUCCESSFULLY("§7§oYouu §a§osuccessfully §7§oremoved §b§o{0}§7§o.", ""),
    HOME_MAX("$prefix$ §c§oYou are only able to set {0} homes at the moment!", ""),
    HOME_ALREADY_EXISTS("$prefix$ §c§oA home with this name already exists!", ""),
    HOME_DOES_NOT_EXISTS("$prefix$ §cYou don't have a home with this name!", ""),
    HOMES_LEFT("$prefix$ §7§oYou have §a§o{0} §7§ohome(s) left.", ""),
    HOME_LIST_FORMAT("   §8- §e{0}", ""),
    HOME_CLICK_TO_TELEPORT("§7§oClick here to teleport to home §a§o{0}", ""),
    NO_HOMES("$prefix$ §c§oYou don't have any homes at the moment.", ""),
    YOUR_HOMES("$prefix$ §7§oYour current homes §8(§a{0}§8):", ""),
    BANK_DEPOSIT("$prefix$ §a§oSuccessfully §7§odeposited §b§o$deposit$§7§o. New faction balance: §b§o$balance$ ", ""),
    TRANSACTION_ERROR("$prefix$ §c§oTransaction error.", ""),
    BANK_WITHDRAW("$prefix$ §a§oSuccessfully §7§owithdrew §b§o$withdraw$§7§o. New faction balance: §b§o$balance$", ""),
    NOT_ENOUGH_MONEY_IN_FAC("$prefix$ §c§oYou don't have enough money in the faction.", ""),
    HOME_HELP_PAGES(Lists.newArrayList(Factions.getInstance().getPrettyGson().toJson(new Gson().toJsonTree(new HelpPage(Lists.newArrayList(
            " "
            , "$prefix$ §7Home Commands §8[§e$page$§7/§e$max_pages$§8]",
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
