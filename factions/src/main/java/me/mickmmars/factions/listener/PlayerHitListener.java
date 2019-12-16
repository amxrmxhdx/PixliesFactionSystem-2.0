package me.mickmmars.factions.listener;

import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.Factions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerHitListener implements Listener {

    private Factions instance = Factions.getInstance();

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            Player target = (Player) e.getEntity();
            if (instance.getPlayerData(attacker).getCurrentFactionData().getName().equals(instance.getPlayerData(target).getCurrentFactionData().getName())) {
                e.setCancelled(true);
                e.setDamage(0);
                attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.CANT_HIT_FAC_MEMBER.getMessageRaw().toString()));
            } else if(instance.getPlayerData(attacker).getCurrentFactionData().getEnemies().contains(instance.getPlayerData(target).getFactionId())) {
                e.setCancelled(false);
            } else if (instance.getPlayerData(attacker).getCurrentFactionData().getAllies().contains(instance.getPlayerData(target).getCurrentFactionData().getId())) {
                e.setCancelled(true);
                e.setDamage(0);
                attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.CANT_HIT_ALLY.getMessageRaw().toString()));
            } else if (Config.DENY_DAMAGE_IN_OWN_TERRITORY.getData().equals(true)) {
                if (instance.getPlayerData(target).getCurrentFactionData().getChunks().contains(instance.getChunkManager().getChunkDataByChunk(attacker.getLocation().getChunk()))) {
                    e.setCancelled(true);
                    e.setDamage(0);
                    attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.CANT_DAMAGE_IN_OWN_TERRITORY.getMessageRaw().toString()));
                }
            }
        }

    }
}
