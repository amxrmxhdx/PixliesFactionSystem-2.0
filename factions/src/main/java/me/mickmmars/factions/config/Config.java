package me.mickmmars.factions.config;

public enum Config {

    MESSAGES("messages_en_US"),
    AUTO_UPDATE(false),
    FACTION_WORLD("world"),
    DEFAULT_BALANCE(0),
    USE_CHAT_TO_CLAIM(true),
    FACTIONCHAT_FORMAT("§bFACTION §8| %prefix%%name% §8» §f%message%"),
    FACTIONCHAT_SPY_FORMAT("§cFCHAT-SPY §8| §c%faction% §| %prefix%%name% §8» §f%message%"),
    CHATFORMAT_WITHFACTION("%prefix%%faction% §8| %rank%%player%  §8» §f%message%"),
    CHATFORMAT_NOFACTION("%rank%%player% §8» §f%message%"),
    DEFAULT_PLAYER_POWER(300),
    CHAT_FCLAIM("fclaim"),
    CHAT_FINFO("finfo"),
    MAX_HOMES_USER(5),
    MAX_HOMES_VIP(10),
    MAX_FACTION_MEMBERS(-1),
    DENY_DAMAGE_IN_OWN_TERRITORY(true),
    USE_INTEGRATED_CHAT(true),
    ENABLE_DYNMAP(true),
    ONE_PUBLIC_WARP_PRICE(25000.0),
    TWO_PUBLIC_WARPS_PRICE(50000.0),
    THREE_PUBLIC_WARPS_PRICE(100000.0),
    FACTION_FLY_PRICE(150000.0),
    DYNMAPCOLOUR_PRICE(200000.0),
    MOREMEMBERS_PRICE(150000.0),
    INFORM_FACTION_WHEN_FOREIGNER_ENTERS(true);
    private Object data;

    Config(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
