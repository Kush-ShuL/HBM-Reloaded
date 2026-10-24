package com.hbm.worldgen.depthdeposit;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.registry.HbmContent;
import com.hbm.worldgen.ore.OreWorldgen;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** DepthDeposit's original X/Y/Z loop, old float-root distance, conditional random draws and bedrock replacement. */
public final class DepthDepositsFeature extends Feature<NoneFeatureConfiguration> {
    public DepthDepositsFeature(){super(NoneFeatureConfiguration.CODEC);}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        var level=context.level();if(!com.hbm.config.WorldgenControls.ores())return false;var dimension=level.getLevel().dimension();if(dimension.equals(Level.NETHER)||dimension.equals(Level.END)||!dimension.equals(Level.OVERWORLD)&&!com.hbm.config.WorldgenControls.moddedDimensions()||!OreWorldgen.OVERWORLD.get())return false;
        int chunkX=context.origin().getX()&~15,chunkZ=context.origin().getZ()&~15;RandomSource random=context.random();int count=0;
        // Preserve all six original positive-band calls before consuming any random
        // numbers for the optional modern extension.
        if(com.hbm.config.WorldgenControls.feature("depth_deposits")&&DepthDeposits.LEGACY_POSITIVE.get())for(var profile:DepthDeposits.PROFILES)count+=condition(level,random,chunkX,chunkZ,profile,0,1,126,profile.chance());
        int base=level.getMinBuildHeight()+DepthDeposits.NEGATIVE_BASE_OFFSET.get();
        int top=Math.min(-1,Math.min(OreWorldgen.DEEP_TOP.get(),base+126));
        if(com.hbm.config.WorldgenControls.feature("deep_depth_deposits")&&DepthDeposits.NEGATIVE.get()&&OreWorldgen.DEEP.get()&&base+1<=top){
            double multiplier=DepthDeposits.NEGATIVE_CHANCE_MULTIPLIER.get();
            for(var profile:DepthDeposits.PROFILES)count+=condition(level,random,chunkX,chunkZ,profile,base,base+1,top,Math.max(1,(int)Math.round(profile.chance()*multiplier)));
        }
        return count>0;
    }
    private static int condition(WorldGenLevel level,RandomSource random,int chunkX,int chunkZ,DepthDeposits.Profile profile,int base,int minimum,int maximum,int chance){
        var control=com.hbm.config.WorldgenControls.rule(minimum==1?"depth_deposits":"deep_depth_deposits",profile.symbol());if(!control.enabled())return 0;chance=control.chance(chance);if(chance==0||random.nextInt(chance)!=0)return 0;
        // Source call argument evaluation order is X, Y, Z.
        int x=chunkX+random.nextInt(16)+8,y=control.height(random,base+profile.minY(),base+profile.minY()+profile.variance()-1,true),z=chunkZ+random.nextInt(16)+8;
        return generate(level,random,x,y,z,control.size(profile.size()),control.decimal("fill",profile.fill()),HbmContent.block(profile.symbol()).defaultBlockState(),DepthDeposits.STONE_DEPTH.get().defaultBlockState(),minimum,maximum);
    }
    public static int generate(WorldGenLevel level,RandomSource random,int x,int y,int z,int size,double fill,BlockState mineral,BlockState filler,int minimum,int maximum){
        int changed=0;var pos=new BlockPos.MutableBlockPos();
        for(int i=x-size;i<=x+size;i++)for(int j=y-size;j<=y+size;j++)for(int k=z-size;k<=z+size;k++){
            if(j<minimum||j>maximum||j<level.getMinBuildHeight()||j>=level.getMaxBuildHeight())continue;
            double distance=LegacyVector.createVectorHelper(x-i,y-j,z-k).lengthVector();pos.set(i,j,k);
            if(!level.ensureCanWrite(pos))throw new IllegalStateException("Source DepthDeposit outside generation region at "+pos);
            BlockState previous=level.getBlockState(pos);
            // Original Forge predicate defaults to identity with Blocks.stone OR
            // Blocks.bedrock. Current modern stone/deepslate base-rock host tags
            // extend that identity to1.18+ terrain, never to existing mineral rock.
            if(previous.is(Blocks.BEDROCK)||previous.is(BlockTags.STONE_ORE_REPLACEABLES)||previous.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)){
                if(distance+random.nextInt(2)<size*fill){level.setBlock(pos,mineral,3);changed++;}
                else if(distance+random.nextInt(2)<=size){level.setBlock(pos,filler,3);changed++;}
            }
        }
        return changed;
    }
}
