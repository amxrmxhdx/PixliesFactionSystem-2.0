package me.mickmmars.factions.factions.itemstacks;

import me.mickmmars.factions.chunk.location.ChunkLocation;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BannerMeta;

public class BannerData {

    private final Material material;
    private final BannerMeta meta;

    public BannerData(Material material, BannerMeta meta) {
        this.material = material;
        this.meta = meta;
    }

    public Material getMaterial() { return material; }

    public BannerMeta getMeta() { return meta; }

}
