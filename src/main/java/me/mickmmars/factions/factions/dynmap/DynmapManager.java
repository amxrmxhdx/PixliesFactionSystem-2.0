package me.mickmmars.factions.factions.dynmap;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.chunk.data.ChunkData;
import me.mickmmars.factions.factions.data.FactionData;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class DynmapManager {

/*    public final static int BLOCKS_PER_CHUNK = 16;

    public final static String DYNMAP_INTEGRATION = "\u00A7dDynmap Integration: \u00A7e";

    public final static String FACTIONS = "factions";
    public final static String FACTIONS_ = FACTIONS + "_";

    public final static String FACTIONS_MARKERSET = FACTIONS_ + "markerset";

    public final static String FACTIONS_HOME = FACTIONS_ + "home";
    public final static String FACTIONS_HOME_ = FACTIONS_HOME + "_";

    public final static String FACTIONS_PLAYERSET = FACTIONS_ + "playerset";
    public final static String FACTIONS_PLAYERSET_ = FACTIONS_PLAYERSET + "_";

    private static DynmapManager i = new DynmapManager();

    public static DynmapManager getInstance() {
        return i;
    }

    private DynmapManager() {
    }

    public DynmapAPI dynmapApi;
    public MarkerAPI markerApi;
    public MarkerSet markerset;

    public void init() {
        Plugin dynmap = Bukkit.getServer().getPluginManager().getPlugin("dynmap");

        if (dynmap == null) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Factions.getInstance(), new Runnable() {
            @Override
            public void run() {

                final Map<String, TempMarker> capitals = createCapitals();
                final Map<String, TempAreaMarker> areas = createAreas();
                final Map<String, Set<String>> playerSets = createPlayersets();

                if (!updateCore()) {
                    return;
                }

                if (!updateLayer(createLayer())) {
                    return;
                }

                updateCapitals(capitals);
                updateAreas(areas);
                updatePlayersets(playerSets);
            }
        }, 100L, 100L);
    }

    // Thread Safe / Asynchronous: No
    public boolean updateCore() {
        // Get DynmapAPI
        this.dynmapApi = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
        if (this.dynmapApi == null) {
            severe("Could not retrieve the DynmapAPI.");
            return false;
        }

        // Get MarkerAPI
        this.markerApi = this.dynmapApi.getMarkerAPI();
        if (this.markerApi == null) {
            severe("Could not retrieve the MarkerAPI.");
            return false;
        }

        return true;
    }

    public TempMarkerSet createLayer() {
        TempMarkerSet ret = new TempMarkerSet();
        ret.label = Config.DYNMAPLAYERNAME.getData().toString();
        ret.minimumZoom = Integer.parseInt(Config.DYNMAPLAYERMINIMUMZOOM.getData().toString());
        ret.priority = Integer.parseInt(Config.DYNMAPLAYERPRIORITY.getData().toString());
        ret.hideByDefault = !Boolean.getBoolean(Config.DYNMAPLAYERVISIBLE.getData().toString());
        return ret;
    }

    public boolean updateLayer(TempMarkerSet temp) {
        this.markerset = this.markerApi.getMarkerSet(FACTIONS_MARKERSET);
        if (this.markerset == null) {
            this.markerset = temp.create(this.markerApi, FACTIONS_MARKERSET);
            if (this.markerset == null) {
                severe("Could not create the Faction Markerset/Layer");
                return false;
            }
        } else {
            temp.update(this.markerset);
        }
        return true;
    }

    public Map<String, TempMarker> createCapitals() {
        Map<String, TempMarker> ret = new HashMap<String, TempMarker>();

        for (FactionData faction : Factions.getInstance().getFactionManager().getFactions()) {
            Location ps = faction.getCapitalLocation().toBukkitLocation();
            if (ps == null) {
                continue;
            }

            DynmapStyle style = getStyle(faction);

            String markerId = FACTIONS_HOME_ + faction.getId();

            TempMarker temp = new TempMarker();
            temp.label = ChatColor.stripColor(faction.getName());
            temp.world = ps.getWorld().toString();
            temp.x = ps.getX();
            temp.y = ps.getY();
            temp.z = ps.getZ();
            temp.iconName = style.getHomeMarker();
            temp.description = getDescription(faction);

            ret.put(markerId, temp);
        }

        return ret;
    }

    public void updateCapitals(Map<String, TempMarker> capitals) {
        Map<String, Marker> markers = new HashMap<String, Marker>();
        for (Marker marker : this.markerset.getMarkers()) {
            markers.put(marker.getMarkerID(), marker);
        }

        for (Map.Entry<String, TempMarker> entry : capitals.entrySet()) {
            String markerId = entry.getKey();
            TempMarker temp = entry.getValue();

            Marker marker = markers.remove(markerId);
            if (marker == null) {
                marker = temp.create(this.markerApi, this.markerset, markerId);
                if (marker == null) {
                    DynmapManager.severe("Could not get/create the capital marker " + markerId);
                }
            } else {
                temp.update(this.markerApi, marker);
            }
        }

        for (Marker marker : markers.values()) {
            marker.deleteMarker();
        }
    }

    public static void severe(String msg) {
        String message = DYNMAP_INTEGRATION + ChatColor.RED.toString() + msg;
        System.out.println(message);
    }

    private String getDescription(FactionData faction) {
        String ret = "<div class=\"regioninfo\">" + Config. + "</div>";

        // Name
        String name = faction.getTag();
        name = ChatColor.stripColor(name);
        name = escapeHtml(name);
        ret = ret.replace("%name%", name);

        // Description
        String description = faction.getDescription();
        description = ChatColor.stripColor(description);
        description = escapeHtml(description);
        ret = ret.replace("%description%", description);

        // Money

        String money = "unavailable";
        if (Conf.bankEnabled && Conf.dynmapDescriptionMoney) {
            money = String.format("%.2f", Econ.getBalance(faction.getAccountId()));
        }
        ret = ret.replace("%money%", money);


        // Players
        Set<FPlayer> playersList = faction.get();
        String playersCount = String.valueOf(playersList.size());
        String players = getHtmlPlayerString(playersList);

        FPlayer playersLeaderObject = faction.getFPlayerAdmin();
        String playersLeader = getHtmlPlayerName(playersLeaderObject);

        ArrayList<FPlayer> playersAdminsList = faction.getFPlayersWhereRole(Role.ADMIN);
        String playersAdminsCount = String.valueOf(playersAdminsList.size());
        String playersAdmins = getHtmlPlayerString(playersAdminsList);

        ArrayList<FPlayer> playersModeratorsList = faction.getFPlayersWhereRole(Role.MODERATOR);
        String playersModeratorsCount = String.valueOf(playersModeratorsList.size());
        String playersModerators = getHtmlPlayerString(playersModeratorsList);


        ArrayList<FPlayer> playersNormalsList = faction.getFPlayersWhereRole(Role.NORMAL);
        String playersNormalsCount = String.valueOf(playersNormalsList.size());
        String playersNormals = getHtmlPlayerString(playersNormalsList);


        ret = ret.replace("%players%", players);
        ret = ret.replace("%players.count%", playersCount);
        ret = ret.replace("%players.leader%", playersLeader);
        ret = ret.replace("%players.admins%", playersAdmins);
        ret = ret.replace("%players.admins.count%", playersAdminsCount);
        ret = ret.replace("%players.moderators%", playersModerators);
        ret = ret.replace("%players.moderators.count%", playersModeratorsCount);
        ret = ret.replace("%players.normals%", playersNormals);
        ret = ret.replace("%players.normals.count%", playersNormalsCount);

        return ret;
    }



}*/

    public DynmapAPI dynmapApi;
    public MarkerAPI markerApi;
    public MarkerSet markerset;

    public void createMarkerSet() {
        for (FactionData data : Factions.getInstance().getFactionManager().getFactions()) {
            for (ChunkData chunks : data.getChunks()) {
                MarkerSet m = this.markerApi.createMarkerSet("earth.markerset", "Factions", this.markerApi.getMarkerIcons(), false);
                String markerid = data.getName();
                AreaMarker am = m.createAreaMarker(markerid, data.getName(), false, chunks.getMinLocation().getWorld(), new double[1000], new double[1000], false);
                double[] d1 = {chunks.getMinLocation().toBukkitLocation().getBlockX(), chunks.getMinLocation().toBukkitLocation().getBlockZ()};
                double[] d2 = {chunks.getMaxLocation().toBukkitLocation().getBlockX(), chunks.getMaxLocation().toBukkitLocation().getBlockZ()};
                am.setCornerLocations(d2, d2);
                am.setLabel(data.getName());
                am.setDescription(data.getDescription());
            }
        }
    }

    void cleanup() {
        markerset = null;
    }

}
