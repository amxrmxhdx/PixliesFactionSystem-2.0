package me.mickmmars.factions.config;

public enum Config {

    MESSAGES("messages_en_US"),
    AUTO_UPDATE(false),
    FACTION_WORLD("world"),
    MAX_HOMES_USER(5),
    MAX_HOMES_VIP(10);

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
