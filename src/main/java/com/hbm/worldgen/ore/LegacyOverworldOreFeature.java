package com.hbm.worldgen.ore;

import com.hbm.registry.HbmDeepslateOres;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Source ordinary ore loops and geographic coltan/australium rules, plus the requested deep layer. */
public final class LegacyOverworldOreFeature extends Feature<NoneFeatureConfiguration> {
    public LegacyOverworldOreFeature() { super(NoneFeatureConfiguration.CODEC); }
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!com.hbm.config.WorldgenControls.ores() || !OreWorldgen.OVERWORLD.get()) return false;
        WorldGenLevel level = context.level(); RandomSource random = context.random();
        int chunkX = context.origin().getX() & ~15, chunkZ = context.origin().getZ() & ~15;
        int placed = com.hbm.config.WorldgenControls.feature("overworld_ores") ? layer(level, random, chunkX, chunkZ, false) : 0;
        if (com.hbm.config.WorldgenControls.feature("deepslate_ores") && OreWorldgen.DEEP.get() && deepMin(level) <= deepMax(level)) placed += layer(level, random, chunkX, chunkZ, true);
        return placed > 0;
    }
    private static int deepMin(WorldGenLevel level) { return level.getMinBuildHeight() + OreWorldgen.DEEP_BOTTOM_OFFSET.get(); }
    private static int deepMax(WorldGenLevel level) { return Math.min(-1, Math.min(level.getMaxBuildHeight() - 1, OreWorldgen.DEEP_TOP.get())); }
    private static int count(int original, boolean deep) { return deep ? (int) Math.round(original * OreWorldgen.DEEP_MULTIPLIER.get()) : original; }
    private static int layer(WorldGenLevel level, RandomSource random, int chunkX, int chunkZ, boolean deep) {
        int placed = 0;
        for (OreProfile profile : OreWorldgen.PROFILES) {
            if (!profile.enabled() || !HbmDeepslateOres.has(profile.symbol())) continue;
            var control=com.hbm.config.WorldgenControls.rule(deep?"deepslate_ores":"overworld_ores",profile.symbol());
            if(!control.enabled())continue;
            for (int vein = 0; vein < control.count(count(OreWorldgen.count(profile), deep)); vein++) {
                int x = chunkX + random.nextInt(16);
                int y = control.height(random,deep?deepMin(level):profile.minY(),deep?deepMax(level):profile.minY()+Math.max(0,profile.variance()-1),deep||profile.variance()>0);
                int z = chunkZ + random.nextInt(16);
                placed += LegacyOreVein.place(level, random, x, y, z, control.size(profile.size()), profile.symbol(), deep);
            }
        }
        var coltanRandomControl=com.hbm.config.WorldgenControls.rule(deep?"deepslate_ores":"overworld_ores","coltan_random");
        if (coltanRandomControl.enabled() && com.hbm.config.GeneralModes.coltanRandom()) for (int vein = 0; vein < coltanRandomControl.count(count(com.hbm.config.GeneralModes.coltanFrequency(), deep)); vein++) {
            int x = chunkX + random.nextInt(16), y = coltanRandomControl.height(random,deep?deepMin(level):15,deep?deepMax(level):54,true), z = chunkZ + random.nextInt(16);
            placed += LegacyOreVein.place(level, random, x, y, z, coltanRandomControl.size(4), "ore_coltan", deep);
        }
        var coltanDeposit = ColtanDepositLocation.fromSeed(level.getSeed());
        int coltanX = coltanDeposit.x(), coltanZ = coltanDeposit.z();
        var coltanDepositControl=com.hbm.config.WorldgenControls.rule(deep?"deepslate_ores":"overworld_ores","coltan_deposit");
        if (coltanDepositControl.enabled() && com.hbm.config.GeneralModes.coltanDeposit()) for (int k = 0; k < coltanDepositControl.count(2); k++) for (int ring = 1; ring <= 5; ring++) {
            int x = chunkX + random.nextInt(16), y = coltanDepositControl.height(random,deep?deepMin(level):15,deep?deepMax(level):39,true), z = chunkZ + random.nextInt(16);
            int range = 750 / ring;
            if (x <= coltanX + range && x >= coltanX - range && z <= coltanZ + range && z >= coltanZ - range)
                placed += LegacyOreVein.place(level, random, x, y, z, coltanDepositControl.size(4), "ore_coltan", deep);
        }
        // Preserve the original random loop bound: nextInt(4) is re-evaluated on every test.
        var australiumControl=com.hbm.config.WorldgenControls.rule(deep?"deepslate_ores":"overworld_ores","australium");
        if(australiumControl.enabled())for (int k = 0; k < (australiumControl.overrides("count")?australiumControl.count(0):random.nextInt(4)); k++) {
            int x = chunkX + random.nextInt(16), y = australiumControl.height(random,deep?deepMin(level):15,deep?deepMax(level):29,true), z = chunkZ + random.nextInt(16);
            if (x <= -350 && x >= -450 && z <= -350 && z >= -450)
                placed += LegacyOreVein.place(level, random, x, y, z, australiumControl.size(50), "ore_australium", deep);
        }
        return placed;
    }
}
