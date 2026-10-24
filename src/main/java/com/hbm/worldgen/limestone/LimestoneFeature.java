package com.hbm.worldgen.limestone;

import com.hbm.config.WorldgenControls;
import com.hbm.registry.HbmOres;
import com.hbm.worldgen.dimensionores.DimensionOreVein;
import com.hbm.worldgen.ore.OreWorldgen;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** The actual source metadata-four limestone vein call inside the surface ore switch. */
public final class LimestoneFeature extends Feature<NoneFeatureConfiguration> {
    public LimestoneFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if(!WorldgenControls.oreFeature("limestone")||!OreWorldgen.OVERWORLD.get())return false;
        var level=context.level();
        var dimension=level.getLevel().dimension();
        if(dimension.equals(Level.NETHER)||dimension.equals(Level.END)
                ||(!dimension.equals(Level.OVERWORLD)&&!WorldgenControls.moddedDimensions()))return false;
        var control=WorldgenControls.rule("limestone","stone_resource_limestone");
        if(!control.enabled())return false;
        int chunkX=context.origin().getX()&~15;
        int chunkZ=context.origin().getZ()&~15;
        var random=context.random();
        var limestone=HbmOres.resourceBlock("stone_resource",4).defaultBlockState();
        int changed=0;
        for(int vein=0;vein<control.count(1);vein++) {
            // Preserve source argument draw order: X, then Y, then Z.
            int x=chunkX+random.nextInt(16);
            int y=control.height(random,25,54,true);
            int z=chunkZ+random.nextInt(16);
            changed+=DimensionOreVein.placeReady(level,random,x,y,z,control.size(16),limestone,Blocks.STONE);
        }
        return changed>0;
    }
}
