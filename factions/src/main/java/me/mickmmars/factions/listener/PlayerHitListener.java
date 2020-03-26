package me.mickmmars.factions.listener;

import me.mickmmars.factions.config.Config;
import me.mickmmars.factions.factions.flags.FactionFlag;
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
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            Player target = (Player) e.getEntity();

            if (instance.getStaffmode().contains(attacker.getUniqueId())) {
                e.setCancelled(false);
                return;
            }
            if (instance.getPlayerData(target).isInFaction() && !instance.getPlayerData(target).getCurrentFactionData().getChunks().containsKey(instance.getChunkManager().getChunkDataByChunk(target.getLocation().getChunk()))) {
                e.setCancelled(false);
                return;
            }
            if (instance.getPlayerData(target).isInFaction() && instance.getPlayerData(target).getCurrentFactionData().isInWar() && instance.getPlayerData(attacker).isInFaction() && instance.getPlayerData(attacker).getCurrentFactionData().isInWar() &&! instance.getWarFactions().get(instance.getPlayerData(attacker)).inGracePeriod()) {
                e.setCancelled(false);
                return;
            }
            if (instance.getChunkManager().isFree(target.getLocation().getChunk())) {
                e.setCancelled(false);
                return;
            }
/*            if (instance.getPlayerData(target).isInFaction() &&
                    instance.getPlayerData(attacker).isInFaction() &&
                    instance.getChunkManager().getFactionDataByChunk(target.getLocation().getChunk().getX(), target.getLocation().getChunk().getZ())
                            .equals(instance.getPlayerData(target).getCurrentFactionData()) &&
                    instance.getPlayerData(target).getCurrentFactionData() == instance.getPlayerData(attacker).getCurrentFactionData()
            ) {
                e.setCancelled(false);
                return;
            }*/
            e.setCancelled(true);
            attacker.sendMessage(Message.CANT_DAMAGE_IN_OWN_TERRITORY.getMessage());
        }

    }
}
