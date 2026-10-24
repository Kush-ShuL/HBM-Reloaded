package com.hbm.worldgen.gas;

import com.hbm.block.gas.GasBlock;
import com.hbm.block.gas.GasContent;
import com.hbm.block.gas.GasKind;
import com.hbm.config.WorldgenControls;
import com.hbm.worldgen.dimensionores.DimensionOreVein;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** The independent flammable/explosive gas calls before HbmWorldGen's ordinary ore switch. */
public final class GasBubbleFeature extends Feature<NoneFeatureConfiguration> {
    public GasBubbleFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var level=context.level();
        var dimension=level.getLevel().dimension();
        if(!WorldgenControls.oreFeature("gas_bubbles")||dimension.equals(Level.NETHER)||dimension.equals(Level.END)
                ||!dimension.equals(Level.OVERWORLD)&&!WorldgenControls.moddedDimensions())return false;
        int chunkX=context.origin().getX()&~15;
        int chunkZ=context.origin().getZ()&~15;
        // These are separate ordered trials; a successful first bubble must not suppress the second.
        int changed=branch(level,context.random(),chunkX,chunkZ,"flammable",12,GasKind.FLAMMABLE);
        changed+=branch(level,context.random(),chunkX,chunkZ,"explosive",0,GasKind.EXPLOSIVE);
        return changed>0;
    }

    private static int branch(WorldGenLevel level,RandomSource random,int chunkX,int chunkZ,
                              String name,int sourceChance,GasKind kind) {
        var control=WorldgenControls.rule("gas_bubbles",name);
        if(!control.enabled())return 0;
        int chance=control.chance(sourceChance);
        if(chance<=0||random.nextInt(chance)!=0)return 0;
        // Source metadata1 is the real sleeping/wake state, not a separate item identity.
        var gas=GasContent.BLOCKS_BY_KIND.get(kind).get().defaultBlockState().setValue(GasBlock.WAKE_MARKER,1);
        int changed=0;
        for(int vein=0;vein<control.count(1);vein++) {
            int x=chunkX+random.nextInt(16);
            int y=control.height(random,30,39,true);
            int z=chunkZ+random.nextInt(16);
            changed+=DimensionOreVein.placeReady(level,random,x,y,z,control.size(32),gas,Blocks.STONE);
        }
        return changed;
    }
}
