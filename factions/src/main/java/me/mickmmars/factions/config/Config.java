package me.mickmmars.factions.config;

public enum Config {

    MESSAGES("messages_en_US"),
    AUTO_UPDATE(false),
    FACTION_WORLD("world"),
    DEFAULT_BALANCE(0),
    USE_CHAT_TO_CLAIM(true),
    FACTIONCHAT_FORMAT("§bFACTION §8| %prefix%%name% §8» §f%message%"),
    FACTIONCHAT_SPY_FORMAT("§cFCHAT-SPY §8| §c%faction% §| %prefix%%name% §8» §f%message%"),
    ALLYCHAT_FORMAT("§dALLY §8| §c%faction% §| %prefix%%name% §8» §f%message%"),
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
    FACTION_FLY_PRICE(300000.0),
    DYNMAPCOLOUR_PRICE(200000.0),
    ONEPUPPET_PRICE(100000.0),
    TWOPUPPET_PRICE(200000.0),
    THREEPUPPET_PRICE(300000.0),
    BIGGERFCHEST_PRICE(100000.0),
    COMMUNISM_PRICE(30000.0),
    FASCISM_PRICE(30000.0),
    ALLOW_UNCONNECTED_CLAIMS(false),
    MAX_FILL_SIZE(500),
    USE_AUTOMATED_WAR_SYSTEM(true),
    USE_WAR_BOT(true),
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
