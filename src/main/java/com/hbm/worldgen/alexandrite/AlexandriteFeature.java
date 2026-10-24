package com.hbm.worldgen.alexandrite;

import com.hbm.config.WorldgenControls;
import com.hbm.registry.HbmContent;
import com.hbm.worldgen.dimensionores.DimensionOreVein;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Source HbmWorldGen's independent alexandrite trial before the ordinary-ore switch. */
public final class AlexandriteFeature extends Feature<NoneFeatureConfiguration> {
    public AlexandriteFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var level=context.level();
        var dimension=level.getLevel().dimension();
        if(!WorldgenControls.oreFeature("alexandrite")||dimension.equals(Level.NETHER)||dimension.equals(Level.END)
                ||!dimension.equals(Level.OVERWORLD)&&!WorldgenControls.moddedDimensions())return false;
        var control=WorldgenControls.rule("alexandrite","ore_alexandrite");
        if(!control.enabled())return false;
        int chance=control.chance(100);
        var random=context.random();
        if(chance<=0||random.nextInt(chance)!=0)return false;
        int chunkX=context.origin().getX()&~15;
        int chunkZ=context.origin().getZ()&~15;
        var ore=HbmContent.block("ore_alexandrite").defaultBlockState();
        int changed=0;
        for(int vein=0;vein<control.count(1);vein++) {
            int x=chunkX+random.nextInt(16);
            int y=control.height(random,10,14,true);
            int z=chunkZ+random.nextInt(16);
            changed+=DimensionOreVein.placeReady(level,random,x,y,z,control.size(3),ore,Blocks.STONE);
        }
        return changed>0;
    }
}
