package com.hbm.worldgen.dimensionores;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Source lower0..2 and upper125..127 neodymium DepthDeposits, preserving both bedrock boundary layers. */
public final class NetherDepthFeature extends Feature<NoneFeatureConfiguration> {
    public NetherDepthFeature(){super(NoneFeatureConfiguration.CODEC);}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        var level=context.level();if(!com.hbm.config.WorldgenControls.oreFeature("nether_depth"))return false;if(!level.getLevel().dimension().equals(Level.NETHER)||!DimensionOres.NETHER.get()||!DimensionOres.DEPTH.get())return false;
        int chunkX=context.origin().getX()&~15,chunkZ=context.origin().getZ()&~15;var random=context.random();int changed=0;
        var mineral=HbmContent.block("ore_depth_nether_neodymium").defaultBlockState();var filler=DimensionOres.STONE_DEPTH_NETHER.get().defaultBlockState();
        int minimum=DimensionOres.netherBase(level)+1,maximum=DimensionOres.netherBase(level)+DimensionOres.netherHeight(level)-2;
        var pos=new BlockPos.MutableBlockPos();
        for(int sourceMin:new int[]{0,125}){
            var control=com.hbm.config.WorldgenControls.rule("nether_depth",sourceMin==0?"lower":"upper");if(!control.enabled())continue;int chance=control.chance(16),size=control.size(7);double fill=control.decimal("fill",.6D);if(chance==0||random.nextInt(chance)!=0)continue;
            int x=chunkX+random.nextInt(16)+8,y=control.overrides("minY")||control.overrides("maxY")?control.height(random,DimensionOres.height(level,true,sourceMin),DimensionOres.height(level,true,sourceMin+2),true):DimensionOres.height(level,true,sourceMin+random.nextInt(3)),z=chunkZ+random.nextInt(16)+8;
            for(int i=x-size;i<=x+size;i++)for(int j=y-size;j<=y+size;j++)for(int k=z-size;k<=z+size;k++){
                if(j<minimum||j>maximum||j<level.getMinBuildHeight()||j>=level.getMaxBuildHeight())continue;
                double distance=LegacyVector.createVectorHelper(x-i,y-j,z-k).lengthVector();pos.set(i,j,k);
                if(!level.ensureCanWrite(pos))throw new IllegalStateException("Source Nether depth deposit outside generation region at "+pos);
                var previous=level.getBlockState(pos);
                if(previous.is(Blocks.NETHERRACK)||previous.is(Blocks.BEDROCK)){
                    if(distance+random.nextInt(2)<size*fill){level.setBlock(pos,mineral,3);changed++;}
                    else if(distance+random.nextInt(2)<=size){level.setBlock(pos,filler,3);changed++;}
                }
            }
        }return changed>0;
    }
}
