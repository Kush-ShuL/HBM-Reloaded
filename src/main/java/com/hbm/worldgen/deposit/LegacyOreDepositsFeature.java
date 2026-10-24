package com.hbm.worldgen.deposit;

import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.registry.HbmContent;
import com.hbm.worldgen.ore.OreWorldgen;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** True source OreLayer3D scalar XYZ-product mineral deposits, including enable-order noise IDs. */
public final class LegacyOreDepositsFeature extends Feature<NoneFeatureConfiguration> {
    public LegacyOreDepositsFeature(){super(NoneFeatureConfiguration.CODEC);}
    private record Key(long seed,int mask){}
    private record Field(OreDeposits.Profile profile,LegacyPerlin2D x,LegacyPerlin2D y,LegacyPerlin2D z){}
    // Immutable noise fields can safely be reused by parallel generation workers.
    // Bound this cache rather than retaining each visited save/seed indefinitely.
    private static final Map<Key,List<Field>> FIELDS=new LinkedHashMap<>(8,.75F,true){@Override protected boolean removeEldestEntry(Map.Entry<Key,List<Field>> entry){return size()>16;}};
    private static List<Field> fields(long seed,int mask){synchronized(FIELDS){return FIELDS.computeIfAbsent(new Key(seed,mask),key->{var result=new ArrayList<Field>();int id=0;for(int index=0;index<OreDeposits.PROFILES.size();index++)if((mask&(1<<index))!=0){var profile=OreDeposits.PROFILES.get(index);result.add(new Field(profile,new LegacyPerlin2D(new Random(seed+101+id),4),new LegacyPerlin2D(new Random(seed+102+id),4),new LegacyPerlin2D(new Random(seed+103+id),4)));id++;}return List.copyOf(result);});}}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        var level=context.level();if(!com.hbm.config.WorldgenControls.ores())return false;if(!level.getLevel().dimension().equals(net.minecraft.world.level.Level.OVERWORLD))return false;int mask=OreDeposits.enabledMask();if(mask==0)return false;
        int chunkX=context.origin().getX()&~15,chunkZ=context.origin().getZ()&~15,changed=0;
        boolean deep=OreDeposits.DEEP.get()&&OreWorldgen.DEEP.get();
        int low=level.getMinBuildHeight()+OreWorldgen.DEEP_BOTTOM_OFFSET.get(),high=Math.min(-1,Math.min(level.getMaxBuildHeight()-1,OreWorldgen.DEEP_TOP.get()));
        for(var field:fields(level.getSeed(),mask)){
            var output=com.hbm.registry.HbmOres.resourceBlock("stone_resource",field.profile.metadata()).defaultBlockState();
            if(com.hbm.config.WorldgenControls.feature("layer_deposits"))changed+=placeBand(level,chunkX,chunkZ,field,output,Math.max(6,level.getMinBuildHeight()),Math.min(64,level.getMaxBuildHeight()-1),1D,false);
            if(com.hbm.config.WorldgenControls.feature("deep_layer_deposits")&&deep&&low<=high)changed+=placeBand(level,chunkX,chunkZ,field,output,low,high,OreDeposits.DEEP_THRESHOLD_MULTIPLIER.get(),true);
        }
        return changed>0;
    }
    private static int placeBand(WorldGenLevel level,int chunkX,int chunkZ,Field field,BlockState output,int bottom,int top,double density,boolean deep){
        var control=com.hbm.config.WorldgenControls.rule(deep?"deep_layer_deposits":"layer_deposits",field.profile().name());if(!control.enabled())return 0;bottom=Math.max(level.getMinBuildHeight(),control.integer("minY",bottom));top=Math.min(level.getMaxBuildHeight()-1,control.integer("maxY",top));
        if(bottom>top)return 0;var profile=field.profile();double scaleH=control.decimal("horizontalScale",profile.scaleH()),scaleV=control.decimal("verticalScale",profile.scaleV()),threshold=control.decimal("threshold",profile.threshold());int span=top-bottom+1;
        double[][] cacheX=new double[16][span],cacheZ=new double[16][span];
        for(int offset=0;offset<16;offset++)for(int y=top;y>=bottom;y--){cacheX[offset][y-bottom]=field.x.sample(y*scaleV,(chunkZ+8+offset)*scaleH);cacheZ[offset][y-bottom]=field.z.sample((chunkX+8+offset)*scaleH,y*scaleV);}
        var pos=new BlockPos.MutableBlockPos();int changed=0;
        for(int ox=0;ox<16;ox++){int x=chunkX+8+ox;for(int oz=0;oz<16;oz++){int z=chunkZ+8+oz;double ny=field.y.sample(x*scaleH,z*scaleH);
            for(int y=top;y>=bottom;y--){double nx=cacheX[oz][y-bottom],nz=cacheZ[ox][y-bottom];
                if(!(nx*ny*nz>threshold*density))continue;
                pos.set(x,y,z);if(!level.ensureCanWrite(pos))throw new IllegalStateException("Source deposit placement outside generation region at "+pos);
                BlockState previous=level.getBlockState(pos);
                // Source requires both normal-cube and stone-host replacement.
                // Use the existing modern stone/deepslate host sets, so the 1.18
                // negative-Y extension never overwrites ores, minerals or cavities.
                if(previous.isRedstoneConductor(level,pos)&&(previous.is(BlockTags.STONE_ORE_REPLACEABLES)||previous.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES))){level.setBlock(pos,output,2);changed++;}
            }
        }}return changed;
    }
}
