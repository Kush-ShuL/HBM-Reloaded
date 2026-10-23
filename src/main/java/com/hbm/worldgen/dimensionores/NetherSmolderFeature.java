package com.hbm.worldgen.dimensionores;

import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Source30 surface trials after the ore toggle: X,Z,16+nextInt96, six ascending samples and exposed netherrack only. */
public final class NetherSmolderFeature extends Feature<NoneFeatureConfiguration> {
    public NetherSmolderFeature(){super(NoneFeatureConfiguration.CODEC);}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        var level=context.level();if(!level.getLevel().dimension().equals(Level.NETHER))return false;var control=com.hbm.config.WorldgenControls.rule("nether_smolder","ore_nether_smoldering");
        int chunkX=context.origin().getX()&~15,chunkZ=context.origin().getZ()&~15,changed=0;var random=context.random();var pos=new BlockPos.MutableBlockPos();
        var result=HbmContent.block("ore_nether_smoldering").defaultBlockState();
        if(com.hbm.config.WorldgenControls.oreFeature("nether_smolder")&&control.enabled()&&DimensionOres.SMOLDER.get())for(int trial=0;trial<control.count(30);trial++){
            int x=chunkX+random.nextInt(16),z=chunkZ+random.nextInt(16),sourceDepth=control.overrides("minY")||control.overrides("maxY")?control.height(random,DimensionOres.height(level,true,16),DimensionOres.height(level,true,111),true):16+random.nextInt(96);
            for(int sourceY=sourceDepth-5;sourceY<=sourceDepth;sourceY++){
                int y=control.overrides("minY")||control.overrides("maxY")?sourceY:DimensionOres.height(level,true,sourceY);if(y<level.getMinBuildHeight()||y>=level.getMaxBuildHeight()-1)continue;pos.set(x,y,z);
                if(!level.ensureCanWrite(pos))throw new IllegalStateException("Source smolder placement outside generation region at "+pos);
                if(level.getBlockState(pos.above()).isAir()&&level.getBlockState(pos).is(Blocks.NETHERRACK)){level.setBlock(pos,result,3);changed++;}
            }
        }
        // Actual HbmWorldGen: one independent exposed-netherrack trial after the thirty smolder trials.
        var geyserControl=com.hbm.config.WorldgenControls.rule("nether_geyser","geysir_nether");
        if(com.hbm.config.WorldgenControls.oreFeature("nether_geyser")&&geyserControl.enabled())for(int trial=0;trial<geyserControl.count(1);trial++){
            int x=chunkX+random.nextInt(16),z=chunkZ+random.nextInt(16),depth=geyserControl.overrides("minY")||geyserControl.overrides("maxY")?geyserControl.height(random,DimensionOres.height(level,true,16),DimensionOres.height(level,true,111),true):16+random.nextInt(96);
            for(int sourceY=depth-5;sourceY<=depth;sourceY++){
                int y=geyserControl.overrides("minY")||geyserControl.overrides("maxY")?sourceY:DimensionOres.height(level,true,sourceY);if(y<level.getMinBuildHeight()||y>=level.getMaxBuildHeight()-1)continue;pos.set(x,y,z);
                if(!level.ensureCanWrite(pos))throw new IllegalStateException("Source Nether geyser placement outside generation region at "+pos);
                if(level.getBlockState(pos.above()).isAir()&&level.getBlockState(pos).is(Blocks.NETHERRACK)){level.setBlock(pos,com.hbm.world.structure.geyser.GeyserContent.NETHER.get().defaultBlockState(),3);changed++;}
            }
        }return changed>0;
    }
}
