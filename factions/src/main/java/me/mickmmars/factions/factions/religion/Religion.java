package me.mickmmars.factions.factions.religion;

import org.bukkit.DyeColor;

import java.util.ArrayList;
import java.util.List;

public enum Religion {

    SHIAISLAM("§aShia-Islam", DyeColor.LIME),
    SUNNIISLAM("§2Sunni-Islam", DyeColor.GREEN),
    EVANGELIC("§3Evangelic", DyeColor.LIGHT_BLUE),
    CATHOLIC("§bCatholic", DyeColor.CYAN),
    CHRISTORTHODOX("§eChristian-Orthodox", DyeColor.YELLOW),
    JUDAISM("§9Judaism", DyeColor.BLUE),
    BUDDHISM("§6Buddhism", DyeColor.ORANGE),
    ATHEIST("§7Atheist", DyeColor.GRAY),
    OTHER("§cOther", DyeColor.RED);


    private String name;
    private DyeColor color;

    Religion(String name, DyeColor color) {
        this.name = name;
        this.color = color;
    }

    public DyeColor getColor() { return color; }

    public String getName() { return name; }

    public static List<Religion> listReligions() {
        List<Religion> religions = new ArrayList<>();
        religions.add(SHIAISLAM);
        religions.add(SUNNIISLAM);
        religions.add(EVANGELIC);
        religions.add(CATHOLIC);
        religions.add(CHRISTORTHODOX);
        religions.add(JUDAISM);
        religions.add(BUDDHISM);
        religions.add(ATHEIST);
        religions.add(OTHER);
        return religions;
    }

    public static Religion getByName(String name) {
        for (Religion religion : listReligions())
            if (religion.getName().equals(name))
                return religion;
            return null;
    }

}
