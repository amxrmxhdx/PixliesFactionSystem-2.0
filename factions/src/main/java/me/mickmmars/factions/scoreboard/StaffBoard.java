package me.mickmmars.factions.scoreboard;

import me.mickmmars.factions.util.FlickerlessScoreboard;
import me.mickmmars.factions.Factions;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class StaffBoard {

    private Factions instance = Factions.getInstance();

    public void setBoard(Player player) {
        FlickerlessScoreboard.Track staff = new FlickerlessScoreboard.Track("staff", "§7Staffmode: §aenabled", 0, "", "");
        FlickerlessScoreboard fs = new FlickerlessScoreboard("§6" + player.getName(), DisplaySlot.SIDEBAR, staff);
        player.setScoreboard(fs.getScoreboard());
    }

    public void removeBoard(Player player) {
        player.setScoreboard(null);
    }

}
