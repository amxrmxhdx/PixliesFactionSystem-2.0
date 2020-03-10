package me.mickmmars.factions.war;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.message.Message;
import me.mickmmars.factions.war.data.CasusBelli;
import me.mickmmars.factions.war.events.PlayerCappingFailedEvent;
import me.mickmmars.factions.war.events.WarEndEvent;
import me.mickmmars.factions.war.events.WarStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WarManager {

    private Factions instance = Factions.getInstance();

    public List<CasusBelli> listCBs() {
        List<CasusBelli> Cbs = new ArrayList<>();
        for (FactionData facs : instance.getFactionManager().getFactions()) {
            if (facs.listCbs().size() != 0)
                for (CasusBelli cb : facs.listCbs())
                    Cbs.add(cb);
        }
        return Cbs;
    }

    public static CasusBelli latestCb;

    public void createCb(FactionData Attacker, FactionData Defender, CBReason reason, String evidence) {
        List<String> evidences = new ArrayList<>();
        evidences.add(evidence);
        CasusBelli cb = new CasusBelli(instance.generateKey(7), Attacker.getId(), Defender.getId(), evidences, false, reason.getName().toUpperCase(), false, true, false);
        if (Attacker.listCbs().contains(cb)) {
            return;
        }
        List<CasusBelli> Cbs = new ArrayList<>(Attacker.listCbs());
        Cbs.add(cb);
        Attacker.setCbs(Cbs);
        instance.getFactionManager().updateFactionData(instance.getFactionManager().getFactionById(cb.getAttackerId()));
        latestCb = cb;
    }

    public Boolean exists(CasusBelli cb) {
        return listCBs().contains(cb);
    }

    public void addEvidence(CasusBelli cb, String evidence) {
        List<String> evidences = new ArrayList<String>(cb.getEvidence());
        if (evidences.contains(evidence)) return;
        evidences.add(evidence);
        cb.setEvidence(evidences);
        instance.getFactionManager().updateFactionData(instance.getFactionManager().getFactionById(cb.getAttackerId()));
    }

    public void removeEvidence(CasusBelli cb, String evidence) {
        List<String> evidences = new ArrayList<String>(cb.getEvidence());
        if (!evidences.contains(evidence)) return;
        evidences.remove(evidence);
        cb.setEvidence(evidences);
        instance.getFactionManager().updateFactionData(instance.getFactionManager().getFactionById(cb.getAttackerId()));
    }

    public void deleteCb(CasusBelli cb) {
        if (instance.getFactionManager().getFactionById(cb.getAttackerId()).listCbs().contains(cb)) {
            List<CasusBelli> Cbs = new ArrayList<>(instance.getFactionManager().getFactionById(cb.getAttackerId()).listCbs());
            Cbs.remove(cb);
            instance.getFactionManager().getFactionById(cb.getAttackerId()).setCbs(Cbs);
            instance.getFactionManager().updateFactionData(instance.getFactionManager().getFactionById(cb.getAttackerId()));
        }
    }

    public Boolean checkIfFactionAlreadySentCB(FactionData fac, FactionData def) {
        if (fac.listCbs().size() == 0) return false;
        for (CasusBelli cbs : fac.listCbs())
            if (cbs.isPending())
                return true;
        return false;
    }

    public CasusBelli getCbById(String id) {
        for (CasusBelli cb : listCBs())
            if (cb.getId().equals(id))
                return cb;
        return null;
    }

    public CasusBelli getUsed(FactionData attacker) {
        for (CasusBelli cb : attacker.listCbs())
            if (cb.isUsed())
                return cb;
        return null;
    }

    public void acceptCb(CasusBelli cb) {
        FactionData attacker = instance.getFactionManager().getFactionById(cb.getAttackerId());
        List<CasusBelli> cbs = new ArrayList<>(attacker.listCbs());
        cbs.remove(cb);
        cb.setAccepted(true);
        cb.setPending(false);
        cbs.add(cb);
        instance.getFactionManager().updateFactionData(attacker);
    }

    public void rejectCb(CasusBelli cb) {
        FactionData attacker = instance.getFactionManager().getFactionById(cb.getAttackerId());
        List<CasusBelli> cbs = new ArrayList<>(attacker.listCbs());
        cbs.remove(cb);
        cb.setRejected(true);
        cb.setPending(false);
        cbs.add(cb);
        instance.getFactionManager().updateFactionData(attacker);
    }

    public void useCB(CasusBelli cb) {
        List<CasusBelli> cbs = new ArrayList<>(instance.getFactionManager().getFactionById(cb.getAttackerId()).listCbs());
        cbs.remove(cb);
        cb.setUsed(true);
        cbs.add(cb);
        instance.getFactionManager().getFactionById(cb.getAttackerId()).setCbs(cbs);
        instance.getFactionManager().updateFactionData(instance.getFactionManager().getFactionById(cb.getAttackerId()));
    }

    private BukkitTask task;

    public void showFHomeParticles(FactionData fac1, FactionData fac2) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    if (z == 15 || z == 0 || x == 15 || x == 0) {
                        Location lbChange = new Location(fac1.getCapitalLocation().toBukkitLocation().getWorld(), fac1.getCapitalLocation().toBukkitLocation().getChunk().getX() * 16 + x + 0.5, y, fac1.getCapitalLocation().toBukkitLocation().getChunk().getZ() * 16 + z + 0.5);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (fac1.isInWar()) {
                                    lbChange.getWorld().spawnParticle(Particle.REDSTONE, lbChange, 1, new Particle.DustOptions(org.bukkit.Color.RED, 1));
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(instance, 0L, 20L);
                    }
                }
            }
        }
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    if (z == 15 || z == 0 || x == 15 || x == 0) {
                        Location lbChange = new Location(fac2.getCapitalLocation().toBukkitLocation().getWorld(), fac2.getCapitalLocation().toBukkitLocation().getChunk().getX() * 16 + x + 0.5, y, fac2.getCapitalLocation().toBukkitLocation().getChunk().getZ() * 16 + z + 0.5);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (fac2.isInWar()) {
                                    lbChange.getWorld().spawnParticle(Particle.REDSTONE, lbChange, 1, new Particle.DustOptions(org.bukkit.Color.RED, 1));
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(instance, 0L, 20L);
                    }
                }
            }
        }
    }

    public void startTreatyTime(CasusBelli cb, FactionData winner) {
        FactionData attacker = instance.getFactionManager().getFactionById(cb.getAttackerId());
        FactionData defender = instance.getFactionManager().getFactionById(cb.getDefenderId());

        Bukkit.getPluginManager().callEvent(new WarEndEvent(winner, instance.getFactionManager().getFactionById(winner.getOpposingFactionId())));
        announceWarPlayers(cb, Message.TREATY_TIME_STARTED.getMessage());
    }

    public void endWar(CasusBelli cb, FactionData winner) {
        FactionData attacker = instance.getFactionManager().getFactionById(cb.getAttackerId());
        FactionData defender = instance.getFactionManager().getFactionById(cb.getDefenderId());

        winner.setIfIsInWar(false);
        instance.getFactionManager().getFactionById(winner.getOpposingFactionId()).setIfIsInWar(false);
        instance.getFactionManager().updateFactionData(winner);
        instance.getFactionManager().updateFactionData(instance.getFactionManager().getFactionById(winner.getOpposingFactionId()));
        instance.getWarKills().put(attacker, 0);
        instance.getWarKills().put(defender, 0);
        deleteCb(cb);

        Bukkit.getScheduler().cancelTasks(instance);

    }

    public void showGraceBossBar(CasusBelli cb, boolean ended) {
        BossBar bossBar = Bukkit.createBossBar("§bGrace period", BarColor.BLUE, BarStyle.SEGMENTED_10);
        Double fullTime = 60D * 10D;
        instance.inGracePeriod().put(instance.getFactionManager().getFactionById(cb.getAttackerId()), true);
        instance.inGracePeriod().put(instance.getFactionManager().getFactionById(cb.getDefenderId()), true);

        if (task == null) {
            task = new BukkitRunnable() {
                int seconds = 60 * 10;
                @Override
                public void run() {
                    if ((seconds -= 1) == 0) {
                        task.cancel();
                        bossBar.setTitle("§cGrace period ended.");
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bossBar.removeAll();
                        endGracePeriod(cb);
                    } else {
                        bossBar.setProgress(seconds / fullTime);
                    }
                }
            }.runTaskTimer(instance, 0, 20);
        }
        bossBar.setVisible(true);
        for (UUID pfac1 : instance.getFactionManager().getFactionById(cb.getAttackerId()).listOnlineMembers())
            bossBar.addPlayer(Bukkit.getPlayer(pfac1));
        for (UUID pfac1 : instance.getFactionManager().getFactionById(cb.getDefenderId()).listOnlineMembers())
            bossBar.addPlayer(Bukkit.getPlayer(pfac1));
    }

    public void endGracePeriod(CasusBelli cb) {
        showFHomeParticles(instance.getFactionManager().getFactionById(cb.getAttackerId()), instance.getFactionManager().getFactionById(cb.getDefenderId()));
        for (UUID pfac1 : instance.getFactionManager().getFactionById(cb.getAttackerId()).listOnlineMembers())
            Bukkit.getPlayer(pfac1).sendTitle("§cWar has begun", "§7Capture your enemies capital", 20, 20 * 3, 20);
        for (UUID pfac1 : instance.getFactionManager().getFactionById(cb.getDefenderId()).listOnlineMembers())
            Bukkit.getPlayer(pfac1).sendTitle("§cWar has begun", "§7Capture your enemies capital", 20, 20 * 3, 20);
        instance.inGracePeriod().put(instance.getFactionManager().getFactionById(cb.getAttackerId()), false);
        instance.inGracePeriod().put(instance.getFactionManager().getFactionById(cb.getDefenderId()), false);
    }

    public void startTestWar(CasusBelli cb) {
        FactionData attacker = instance.getFactionManager().getFactionById(cb.getAttackerId());
        FactionData defender = instance.getFactionManager().getFactionById(cb.getDefenderId());

        useCB(cb);

        instance.getWarCB().put(attacker, cb);
        instance.getWarCB().put(defender, cb);

        attacker.setIfIsInWar(true);
        attacker.setOpposingFactionId(defender.getId());
        defender.setOpposingFactionId(attacker.getId());
        defender.setIfIsInWar(true);
        instance.getFactionManager().updateFactionData(attacker);
        instance.getFactionManager().updateFactionData(defender);

        Bukkit.getPluginManager().callEvent(new WarStartEvent(cb));

        for (UUID uuid : attacker.listOnlineMembers()) {
            Player oplayer = Bukkit.getPlayer(uuid);
            instance.getPlayerData(oplayer).setIsCapping(false);
            instance.getChunkPlayer(oplayer).updatePlayerData(instance.getPlayerData(oplayer));
            oplayer.playSound(oplayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
        }
        for (UUID uuid : defender.listOnlineMembers()) {
            Player oplayer = Bukkit.getPlayer(uuid);
            instance.getPlayerData(oplayer).setIsCapping(false);
            instance.getChunkPlayer(oplayer).updatePlayerData(instance.getPlayerData(oplayer));
            oplayer.playSound(oplayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
        }
        for (Player oplayer : Bukkit.getOnlinePlayers())
            oplayer.sendMessage(Message.WAR_STARTED.getMessage().replace("%attacker%", attacker.getName()).replace("%defender%", defender.getName()).replace("%cb%", cb.getReason()));
        endGracePeriod(cb);
    }

    public void startWar(CasusBelli cb) {
        FactionData attacker = instance.getFactionManager().getFactionById(cb.getAttackerId());
        FactionData defender = instance.getFactionManager().getFactionById(cb.getDefenderId());

        useCB(cb);

        instance.getWarKills().put(attacker, 0);
        instance.getWarKills().put(defender, 0);

        instance.getWarCB().put(attacker, cb);
        instance.getWarCB().put(defender, cb);

        attacker.setIfIsInWar(true);
        attacker.setOpposingFactionId(defender.getId());
        defender.setOpposingFactionId(attacker.getId());
        defender.setIfIsInWar(true);
        instance.getFactionManager().updateFactionData(attacker);
        instance.getFactionManager().updateFactionData(defender);

        Bukkit.getPluginManager().callEvent(new WarStartEvent(cb));

        for (UUID uuid : attacker.listOnlineMembers()) {
            Player oplayer = Bukkit.getPlayer(uuid);
            instance.getPlayerData(oplayer).setIsCapping(false);
            instance.getChunkPlayer(oplayer).updatePlayerData(instance.getPlayerData(oplayer));
            oplayer.playSound(oplayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
        }
        for (UUID uuid : defender.listOnlineMembers()) {
            Player oplayer = Bukkit.getPlayer(uuid);
            instance.getPlayerData(oplayer).setIsCapping(false);
            instance.getChunkPlayer(oplayer).updatePlayerData(instance.getPlayerData(oplayer));
            oplayer.playSound(oplayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
        }
        for (Player oplayer : Bukkit.getOnlinePlayers())
            oplayer.sendMessage(Message.WAR_STARTED.getMessage().replace("%attacker%", attacker.getName()).replace("%defender%", defender.getName()).replace("%cb%", cb.getReason()));
        showGraceBossBar(cb, false);
    }

    public void showCappingBossBar(CasusBelli cb, FactionData gettingCapped, Player capper) {
        BossBar bossBar = Bukkit.createBossBar("§7Player §6" + capper.getName() + "§7 is capping §b" + gettingCapped.getName(), BarColor.RED, BarStyle.SEGMENTED_10);
        instance.getFactionsWarringBossbars().put(cb, bossBar);
        Double fullTime = 60D * 10D;
        if (task == null) {
            task = new BukkitRunnable() {
                int seconds = 60 * 10;
                @Override
                public void run() {
                    if ((seconds -= 1) == 0) {
                        bossBar.setTitle("§aWar ended");
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        task.cancel();
                        startTreatyTime(cb, instance.getPlayerData(capper).getCurrentFactionData());
                        bossBar.removeAll();
                    } else {
                        if (capper.getLocation().getChunk() != gettingCapped.getCapitalLocation().toBukkitLocation().getChunk()) {
                            Bukkit.getPluginManager().callEvent(new PlayerCappingFailedEvent(capper, cb, gettingCapped));
                            instance.getPlayerData(capper).setIsCapping(false);
                            instance.getChunkPlayer(capper).updatePlayerData(instance.getPlayerData(capper));
                            for (UUID pfac1 : instance.getPlayerData(capper).getCurrentFactionData().listOnlineMembers())
                                Bukkit.getPlayer(pfac1).sendTitle("§cCapping failed", "§b" + capper.getName() + " §7could not cap 10 minutes.", 20, 20 * 3, 20);
                            for (UUID pfac1 : instance.getFactionManager().getFactionById(instance.getPlayerData(capper).getCurrentFactionData().getOpposingFactionId()).listOnlineMembers())
                                Bukkit.getPlayer(pfac1).sendTitle("§aYour capital is safe now.", "§b" + capper.getName() + " §7could not cap 10 minutes.", 20, 20 * 3, 20);
                            bossBar.removeAll();
                            task.cancel();
                        }
                        bossBar.setProgress(seconds / fullTime);
                    }
                }
            }.runTaskTimer(instance, 0, 20);
        }
        bossBar.setVisible(true);
        for (UUID pfac1 : instance.getFactionManager().getFactionById(cb.getAttackerId()).listOnlineMembers())
            bossBar.addPlayer(Bukkit.getPlayer(pfac1));
        for (UUID pfac2 : instance.getFactionManager().getFactionById(cb.getDefenderId()).listOnlineMembers())
            bossBar.addPlayer(Bukkit.getPlayer(pfac2));
    }

    public void announceWarPlayers(CasusBelli cb, String message) {
        for (UUID pfac1 : instance.getFactionManager().getFactionById(cb.getAttackerId()).listOnlineMembers())
            Bukkit.getPlayer(pfac1).sendMessage(message);
        for (UUID pfac1 : instance.getFactionManager().getFactionById(cb.getDefenderId()).listOnlineMembers())
            Bukkit.getPlayer(pfac1).sendMessage(message);
    }

}
