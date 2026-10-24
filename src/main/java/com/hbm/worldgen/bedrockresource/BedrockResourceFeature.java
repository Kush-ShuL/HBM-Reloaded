package com.hbm.worldgen.bedrockresource;

import com.hbm.block.ore.BedrockOreEntity;
import com.hbm.fluid.FluidStack;
import com.hbm.registry.HbmContent;
import com.hbm.worldgen.depthdeposit.DepthDeposits;
import com.hbm.worldgen.dimensionores.DimensionOres;
import com.hbm.worldgen.ore.OreWorldgen;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Actual source3x3 floor resource nodes and independent7x7 shell, not vanilla ellipsoid ore. */
public final class BedrockResourceFeature extends Feature<NoneFeatureConfiguration> {
    private final boolean nether;
    public BedrockResourceFeature(boolean nether){super(NoneFeatureConfiguration.CODEC);this.nether=nether;}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        if(!com.hbm.config.WorldgenControls.oreFeature(nether?"nether_bedrock":"overworld_bedrock"))return false;var control=com.hbm.config.WorldgenControls.rule(nether?"nether_bedrock":"overworld_bedrock",nether?"weighted_resources":"bedrock_ore_base");if(!control.enabled())return false;
        var level=context.level();var dimension=level.getLevel().dimension();
        if(nether?(!dimension.equals(Level.NETHER)||!BedrockResources.NETHER.get()||!DimensionOres.NETHER.get()):(dimension.equals(Level.NETHER)||dimension.equals(Level.END)||!dimension.equals(Level.OVERWORLD)&&!com.hbm.config.WorldgenControls.moddedDimensions()||!BedrockResources.OVERWORLD.get()||!OreWorldgen.OVERWORLD.get()))return false;
        var random=context.random();int chance=control.chance(nether?BedrockResources.NETHER_CHANCE.get():BedrockResources.OVERWORLD_CHANCE.get());if(chance==0||random.nextInt(chance)!=0)return false;
        ItemStack stack;int color,tier;FluidStack acid;
        if(nether){
            int total=0;for(var definition:BedrockResources.NETHER_DEFINITIONS)total+=BedrockResources.weight(definition);
            if(total<=0)return false;
            int roll=random.nextInt(total);BedrockResources.Definition chosen=null;
            for(var definition:BedrockResources.NETHER_DEFINITIONS){roll-=BedrockResources.weight(definition);if(roll<0){chosen=definition;break;}}
            if(chosen==null)throw new IllegalStateException("Invalid actual Nether bedrock weight sum");
            stack=chosen.stack();color=chosen.color();tier=chosen.tier();acid=null;
        }else{stack=new ItemStack(BedrockResources.RAW_ORE.get());color=0xD78A16;tier=0;acid=null;}
        int x=(context.origin().getX()&~15)+random.nextInt(2)+8,z=(context.origin().getZ()&~15)+random.nextInt(2)+8;
        if(!nether){double density=BedrockDensity.average(BedrockDensity.levels(x,z));tier=BedrockDensity.getTier(density);acid=BedrockDensity.getBoreFluid(density);}
        int base=nether?DimensionOres.netherBase(level):(BedrockResources.ADAPT_OVERWORLD_FLOOR.get()?level.getMinBuildHeight():0)+BedrockResources.OVERWORLD_OFFSET.get();
        var filler=nether?DimensionOres.STONE_DEPTH_NETHER.get().defaultBlockState():DepthDeposits.STONE_DEPTH.get().defaultBlockState();
        return generate(level,x,z,base,stack,acid,color,tier,filler,level.getRandom())>0;
    }
    /** Public native generation endpoint for future source generators; keeps resource data in the real existing BE. */
    public static int generate(WorldGenLevel level,int x,int z,int base,ItemStack resource,FluidStack acid,int color,int tier,BlockState filler,RandomSource worldRandom){
        int changed=0;var bedrock=HbmContent.block("ore_bedrock").defaultBlockState();
        for(int ix=x-1;ix<=x+1;ix++)for(int iz=z-1;iz<=z+1;iz++){
            var pos=new BlockPos(ix,base,iz);if(level.isOutsideBuildHeight(pos)||!level.getBlockState(pos).is(Blocks.BEDROCK))continue;
            // Source world.rand is a distinct stream from IWorldGenerator rand.
            if((ix==x&&iz==z)||worldRandom.nextBoolean()){
                write(level,pos,bedrock);var tile=level.getBlockEntity(pos);
                if(!(tile instanceof BedrockOreEntity ore))throw new IllegalStateException("Real generated bedrock ore BE missing at "+pos);
                ore.resource=resource.copy();ore.color=color;ore.shape=worldRandom.nextInt(10);ore.acidRequirement=acid==null?null:new FluidStack(acid.type,acid.fill,acid.pressure);ore.tier=tier;ore.setChanged();changed++;
            }
        }
        // The source runs this shell even when no candidate floor was bedrock.
        for(int ix=x-3;ix<=x+3;ix++)for(int iz=z-3;iz<=z+3;iz++)for(int iy=1;iy<7;iy++){
            var pos=new BlockPos(ix,base+iy,iz);if(level.isOutsideBuildHeight(pos))continue;var state=level.getBlockState(pos);
            if((iy<3||state.is(Blocks.BEDROCK))&&(state.is(Blocks.STONE)||state.is(Blocks.BEDROCK)||(base<0&&state.is(Blocks.DEEPSLATE)))){write(level,pos,filler);changed++;}
        }
        return changed;
    }
    private static void write(WorldGenLevel level,BlockPos pos,BlockState state){if(!level.ensureCanWrite(pos))throw new IllegalStateException("Source bedrock resource outside generation region at "+pos);level.setBlock(pos,state,3);}
}
