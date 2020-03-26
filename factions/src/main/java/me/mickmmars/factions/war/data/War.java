package me.mickmmars.factions.war.data;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.message.Message;
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

import java.util.*;
import java.util.concurrent.TimeUnit;

public class War {

    Factions instance = Factions.getInstance();

    private final CasusBelli cb;
    private FactionData attacker;
    private FactionData defender;
    private List<Player> cappingPlayers;
    private Map<String, Integer> tasks;
    private Map<FactionData, Integer> kills;
    private boolean gracePeriod;
    private boolean inTreaty;

    public War(CasusBelli cb) {
        this.cb = cb;
        this.attacker = instance.getFactionManager().getFactionById(cb.getAttackerId());
        this.defender = instance.getFactionManager().getFactionById(cb.getDefenderId());
        this.cappingPlayers = new ArrayList<>();
        this.tasks = new HashMap<>();
        this.kills = new HashMap<>();
        gracePeriod = false;
    }

    // GETTERS AND SETTERS
    public CasusBelli getCB() { return cb; }
    public FactionData getAttacker() { return attacker; }
    public FactionData getDefender() { return defender; }
    public List<Player> getCappingPlayers() { return cappingPlayers; }
    public Map<String, Integer> getTasks() { return tasks; }
    public Map<FactionData, Integer> getKills() { return kills; }
    public boolean inGracePeriod() { return gracePeriod; }
    public boolean isInTreaty() { return inTreaty; }


    // PREPARE EVERYTHING FOR THE WAR
    public void start() {
        // USE THE CB
        CasusBelli.useCB(cb);

        inTreaty = false;

        // SET THE KILLCOUNTER FOR BOTH FACTIONS TO 0
        kills.put(attacker, 0);
        kills.put(defender, 0);

        // SET THE TWO FACTIONS AS ENEMIES
        attacker.setIfIsInWar(true);
        defender.setIfIsInWar(true);
        attacker.setOpposingFactionId(defender.getId());
        defender.setOpposingFactionId(attacker.getId());
        instance.getFactionManager().updateFactionData(attacker);
        instance.getFactionManager().updateFactionData(defender);

        // CALL WARSTARTEVENT
        Bukkit.getPluginManager().callEvent(new WarStartEvent(cb));

        // ANNOUNCE THE WAR
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

        startGracePeriod();
    }

    // GRACEPERIOD STUFF
    private void startGracePeriod() {
        BossBar bossBar = Bukkit.createBossBar("§bGrace period", BarColor.BLUE, BarStyle.SEGMENTED_10);
        Double fullTime = 60D * 10D;

        // SET THE GRACEPERIOD TO TRUE
        gracePeriod = true;

        tasks.put("graceBar", new BukkitRunnable() {
            int seconds = 60 * 10;
            int secondsLeft = 60 * 10;
            int minutes = (secondsLeft % 3600)/60;
            int output_seconds = (secondsLeft % 3600)%60;
            @Override
            public void run() {
                if ((seconds -= 1) == 0) {

                    Bukkit.getScheduler().cancelTask(tasks.get("graceBar"));
                    bossBar.setTitle("§cGrace period ended.");
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bossBar.removeAll();
                    gracePeriod = false;
                    showFHomeParticles();
                    for (UUID pfac1 : attacker.listOnlineMembers())
                        Bukkit.getPlayer(pfac1).sendTitle("§cWar has begun", "§7Capture your enemies capital", 20, 20 * 3, 20);
                    for (UUID pfac2 : defender.listOnlineMembers())
                        Bukkit.getPlayer(pfac2).sendTitle("§cWar has begun", "§7Capture your enemies capital", 20, 20 * 3, 20);
                } else {
                    secondsLeft = secondsLeft - 1;
                    minutes = (secondsLeft % 3600) / 60;
                    output_seconds = (secondsLeft % 3600) % 60;
                    if (output_seconds < 10) {
                        bossBar.setTitle("§bGrace period §8| §3" + minutes + "§7:§30" + output_seconds);
                    } else {
                        bossBar.setTitle("§bGrace period §8| §3" + minutes + "§7:§3" + output_seconds);
                    }
                    bossBar.setProgress(seconds / fullTime);
                }
            }

        }.runTaskTimer(instance, 0, 20).getTaskId());
        bossBar.setVisible(true);
        for (UUID pfac1 : attacker.getOnlineMembers())
            bossBar.addPlayer(Bukkit.getPlayer(pfac1));
        for (UUID pfac2 : defender.getOnlineMembers())
            bossBar.addPlayer(Bukkit.getPlayer(pfac2));
    }

    // MARK THE F HOMES | ACTUAL WAR BEGINS
    private void showFHomeParticles() {
        // ATTACKER FACTION
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    if (z == 15 || z == 0 || x == 15 || x == 0) {
                        Location lbChange = new Location(attacker.getCapitalLocation().toBukkitLocation().getWorld(), attacker.getCapitalLocation().toBukkitLocation().getChunk().getX() * 16 + x + 0.5, y, attacker.getCapitalLocation().toBukkitLocation().getChunk().getZ() * 16 + z + 0.5);
                        tasks.put("fhomePart1", new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (attacker.isInWar()) {
                                    lbChange.getWorld().spawnParticle(Particle.REDSTONE, lbChange, 1);
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(instance, 0L, 20L).getTaskId());
                    }
                }
            }
        }
        // DEFENDER FACTION
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    if (z == 15 || z == 0 || x == 15 || x == 0) {
                        Location lbChange = new Location(defender.getCapitalLocation().toBukkitLocation().getWorld(), defender.getCapitalLocation().toBukkitLocation().getChunk().getX() * 16 + x + 0.5, y, defender.getCapitalLocation().toBukkitLocation().getChunk().getZ() * 16 + z + 0.5);
                        tasks.put("fhomePart2", new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (defender.isInWar()) {
                                    lbChange.getWorld().spawnParticle(Particle.REDSTONE, lbChange, 1);
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(instance, 0L, 20L).getTaskId());
                    }
                }
            }
        }
    }

    public void showCappingBossBar(Player capper, FactionData gettingCapped) {
        BossBar bossBar = Bukkit.createBossBar("§7Player §6" + capper.getName() + "§7 is capping §b" + gettingCapped.getName() + " §8| §b10§7:§b00", BarColor.RED, BarStyle.SEGMENTED_10);
        Double fullTime = 60D * 10D;
        tasks.put("capBar1" + new Random().nextInt(32 - 1) + 1, new BukkitRunnable() {
            int seconds = 60 * 10;
            int seconds_left = 60 * 10;
            int minutes = (seconds_left % 3600)/60;
            int output_seconds = (seconds_left % 3600)%60;
                @Override
                public void run() {
                    if ((seconds -= 1) == 0) {
                        bossBar.setTitle("§aWar ended");
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        this.cancel();
                        startTreatyTime(instance.getPlayerData(capper).getCurrentFactionData());
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
                            this.cancel();
                        }
                        seconds_left = seconds_left - 1;
                        minutes = (seconds_left % 3600) / 60;
                        output_seconds = (seconds_left % 3600) % 60;
                        bossBar.setProgress(seconds / fullTime);
                        if (output_seconds < 10) {
                            bossBar.setTitle("§7Player §6" + capper.getName() + "§7 is capping §b" + gettingCapped.getName() + " §8| §b" + minutes + "§7:§b0" + output_seconds);
                        } else {
                            bossBar.setTitle("§7Player §6" + capper.getName() + "§7 is capping §b" + gettingCapped.getName() + " §8| §b" + minutes + "§7:§b" + output_seconds);
                        }
                    }
                }
            }.runTaskTimer(instance, 0, 20).getTaskId());
        bossBar.setVisible(true);
        for (UUID pfac1 : attacker.getOnlineMembers())
            bossBar.addPlayer(Bukkit.getPlayer(pfac1));
        for (UUID pfac2 : defender.getOnlineMembers())
            bossBar.addPlayer(Bukkit.getPlayer(pfac2));
    }

    private void startTreatyTime(FactionData winner) {
        Bukkit.getPluginManager().callEvent(new WarEndEvent(winner, instance.getFactionManager().getFactionById(winner.getOpposingFactionId())));
        Bukkit.getScheduler().cancelTasks(instance);
        tasks.remove("fhomePart2");
        tasks.remove("fhomePart1");
        announceWarPlayers(Message.TREATY_TIME_STARTED.getMessage());
        inTreaty = true;
    }

    public void announceWarPlayers(String message) {
        for (UUID uuid : attacker.getOnlineMembers())
            Bukkit.getPlayer(uuid).sendMessage(message);
        for (UUID uuid : defender.getOnlineMembers())
            Bukkit.getPlayer(uuid).sendMessage(message);
    }

    public void sendTitleMessage(String message, String subMessage) {
        for (UUID uuid : attacker.getOnlineMembers())
            Bukkit.getPlayer(uuid).sendTitle(message, subMessage, 20, 20 * 3, 20);
        for (UUID uuid : defender.getOnlineMembers())
            Bukkit.getPlayer(uuid).sendTitle(message, subMessage, 20, 20 * 3, 20);
    }

    public void end() {
        CasusBelli.deleteCb(cb);
        attacker.setIfIsInWar(false);
        defender.setIfIsInWar(false);
        for (UUID uuid : attacker.getOnlineMembers()) {
            instance.getPlayerData(uuid).setIsCapping(false);
            instance.getChunkPlayer(uuid).updatePlayerData(instance.getPlayerData(uuid));
        }
        for (UUID uuid : defender.getOnlineMembers()) {
            instance.getPlayerData(uuid).setIsCapping(false);
            instance.getChunkPlayer(uuid).updatePlayerData(instance.getPlayerData(uuid));
        }
        attacker.updateData();
        defender.updateData();
        instance.getWarFactions().remove(attacker);
        instance.getWarFactions().remove(defender);
        for (int task : tasks.values())
            Bukkit.getScheduler().cancelTask(task);
    }

}
