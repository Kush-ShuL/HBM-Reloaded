package com.hbm.worldgen.orecave;

import com.hbm.block.fluid.*;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.registry.HbmContent;
import com.hbm.worldgen.deposit.LegacyPerlin2D;
import com.hbm.worldgen.ore.OreWorldgen;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Original OreCave surface coating, sealed-pool test, and formation placement; it does not excavate a substitute cave. */
public final class OreCaveFeature extends Feature<NoneFeatureConfiguration> {
    public OreCaveFeature(){super(NoneFeatureConfiguration.CODEC);}
    private record Key(long seed,String name,int metadata,int centerY){}
    private static final Map<Key,LegacyPerlin2D> NOISE=new LinkedHashMap<>(8,.75F,true){@Override protected boolean removeEldestEntry(Map.Entry<Key,LegacyPerlin2D> entry){return size()>32;}};
    private static LegacyPerlin2D noise(long seed,OreCaves.Profile profile){synchronized(NOISE){return NOISE.computeIfAbsent(new Key(seed,profile.legacyOreName(),profile.metadata(),profile.centerY()),key->{
        // MetaBlock.getID() is its hashCode, NOT a numeric Block registry ID.
        // Each multiplication must overflow as int before it is added to long seed.
        int hash=1;hash=31*hash+key.name().hashCode();hash=31*hash+key.metadata();int salt=hash*31;
        return new LegacyPerlin2D(new Random(key.seed()+salt+key.centerY()),2);
    });}}
    private static final Direction[] DIRECTIONS={Direction.DOWN,Direction.UP,Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST};
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        var level=context.level();if(!com.hbm.config.WorldgenControls.ores())return false;if(!level.getLevel().dimension().equals(Level.OVERWORLD))return false;
        int x=context.origin().getX()&~15,z=context.origin().getZ()&~15,changed=0;var random=context.random();
        if(com.hbm.config.WorldgenControls.feature("ore_caves"))for(var profile:OreCaves.PROFILES)if(OreCaves.enabled(profile))changed+=profile(level,random,x,z,profile,0,level.getMinBuildHeight(),level.getMaxBuildHeight()-1,false);
        int minimum=level.getMinBuildHeight()+OreWorldgen.DEEP_BOTTOM_OFFSET.get(),maximum=Math.min(-1,Math.min(level.getMaxBuildHeight()-1,OreWorldgen.DEEP_TOP.get()));
        if(com.hbm.config.WorldgenControls.feature("deep_ore_caves")&&OreCaves.DEEP.get()&&OreWorldgen.DEEP.get()&&minimum<=maximum)for(var profile:OreCaves.PROFILES)if(OreCaves.enabled(profile))changed+=profile(level,random,x,z,profile,level.getMinBuildHeight()+OreCaves.NEGATIVE_OFFSET.get(),minimum,maximum,true);
        return changed>0;
    }
    private static int profile(WorldGenLevel level,RandomSource random,int chunkX,int chunkZ,OreCaves.Profile profile,int translation,int minimum,int maximum,boolean deep){
        var control=com.hbm.config.WorldgenControls.rule(!deep?"ore_caves":"deep_ore_caves",profile.name());if(!control.enabled())return 0;
        double threshold=control.decimal("threshold",profile.threshold()),scale=control.decimal("horizontalScale",.01D);int center=control.integer("centerY",profile.centerY()+translation),multiplier=control.integer("rangeMultiplier",profile.rangeMult()),maxRange=control.integer("maxRange",profile.maxRange());
        var noise=noise(level.getSeed(),profile);var ore=com.hbm.registry.HbmOres.resourceBlock("stone_resource",profile.metadata()).defaultBlockState();
        var fluid=profile.fluid()==null?null:HbmWorldFluids.block(profile.fluid()).defaultBlockState();int changed=0;
        for(int x=chunkX+8;x<chunkX+24;x++)for(int z=chunkZ+8;z<chunkZ+24;z++){
            double n=noise.sample(x*scale,z*scale);if(!(n>threshold))continue;
            int range=(int)((n-threshold)*multiplier);if(range>maxRange)range=maxRange*2-range;if(range<0)continue;
            for(int y=center-range;y<=center+range;y++){
                if(y<minimum||y>maximum)continue;BlockPos pos=new BlockPos(x,y,z);var target=level.getBlockState(pos);var material=LegacyMaterials.of(target);
                if(normal(level,pos,target)&&(material.name().equals("rock")||material.name().equals("ground"))&&(target.is(BlockTags.STONE_ORE_REPLACEABLES)||target.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES))){
                    boolean shouldGenerate=false,canGenerateFluid=random.nextBoolean();
                    for(var direction:DIRECTIONS){var neighborPos=pos.relative(direction);var neighbor=level.getBlockState(neighborPos);boolean airOrFormation=neighbor.isAir()||neighbor.getBlock() instanceof CaveFormationBlock;
                        if(airOrFormation)shouldGenerate=true;
                        // Preserve this source early break BEFORE the fluid face rule.
                        if(shouldGenerate&&(fluid==null||!canGenerateFluid))break;
                        if(fluid!=null)switch(direction){
                            case UP->{if(!airOrFormation)canGenerateFluid=false;}
                            case DOWN->{if(!normal(level,neighborPos,neighbor))canGenerateFluid=false;}
                            case NORTH,SOUTH,WEST,EAST->{if(!normal(level,neighborPos,neighbor)&&neighbor.getBlock()!=fluid.getBlock())canGenerateFluid=false;}
                        }
                    }
                    if(fluid!=null&&canGenerateFluid){
                        changed+=write(level,pos,fluid);changed+=write(level,pos.below(),ore);
                        for(var direction:new Direction[]{Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST}){var neighborPos=pos.relative(direction);if(normal(level,neighborPos,level.getBlockState(neighborPos)))changed+=write(level,neighborPos,ore);}
                    }else if(shouldGenerate)changed+=write(level,pos,ore);
                }else{
                    // Source consumes nextInt5 before checking liquid. Even the
                    // fluid branch must not move that draw after isLiquid().
                    if((target.isAir()||!normal(level,pos,target))&&random.nextInt(5)==0&&!material.isLiquid()){
                        var hanging=OreCaves.STALACTITE.get();var standing=OreCaves.STALAGMITE.get();
                        if(hanging.canPlaceAt(level,pos))changed+=write(level,pos,hanging.defaultBlockState().setValue(CaveFormationBlock.LEGACY_META,profile.metadata()));
                        else if(standing.canPlaceAt(level,pos))changed+=write(level,pos,standing.defaultBlockState().setValue(CaveFormationBlock.LEGACY_META,profile.metadata()));
                    }
                }
            }
        }return changed;
    }
    private static boolean normal(BlockGetter level,BlockPos pos,BlockState state){return com.hbm.block.LegacyBlockTraits.isNormalCube(level,pos,state);}
    private static int write(WorldGenLevel level,BlockPos pos,BlockState state){if(!level.ensureCanWrite(pos))throw new IllegalStateException("Source ore-cave write outside generation region at "+pos);level.setBlock(pos,state,2);return 1;}
}
