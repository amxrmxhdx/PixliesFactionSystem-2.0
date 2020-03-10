package me.mickmmars.factions.listener;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

public class WarCommandsListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();

        if (instance.getPlayerData(player).getCurrentFactionData() != null && instance.getPlayerData(player).getCurrentFactionData().isInWar()) {
            String msg = event.getMessage().toLowerCase();
            List<String> cmds = Arrays.asList("/factions", "/f", "/countries", "/fac");

            for (String s : cmds) {
                if (event.getMessage().toLowerCase().startsWith(s)) {
                    if (msg.startsWith(s + " claim")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Message.CMD_DISABLED_DURING_WAR.getMessage());
                    } else if (msg.startsWith(s + " unclaim")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Message.CMD_DISABLED_DURING_WAR.getMessage());
                    } else if (msg.startsWith(s + " menu")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Message.CMD_DISABLED_DURING_WAR.getMessage());
                    } else if (msg.startsWith(s + " map")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Message.CMD_DISABLED_DURING_WAR.getMessage());
                    } else if (msg.startsWith(s + " delete")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Message.CMD_DISABLED_DURING_WAR.getMessage());
                    } else if (msg.equalsIgnoreCase(s + " fly")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Message.CMD_DISABLED_DURING_WAR.getMessage());
                    }
                }
            }
        }

    }

}
