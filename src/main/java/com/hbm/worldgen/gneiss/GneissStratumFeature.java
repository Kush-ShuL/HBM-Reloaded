package com.hbm.worldgen.gneiss;

import com.hbm.registry.HbmContent;
import com.hbm.worldgen.deposit.LegacyPerlin2D;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Original SchistStratum (despite its name, its real output is stone_gneiss), before decorator ores. */
public final class GneissStratumFeature extends Feature<NoneFeatureConfiguration> {
    public GneissStratumFeature(){super(NoneFeatureConfiguration.CODEC);}
    private static final Map<Long,LegacyPerlin2D> NOISE=new LinkedHashMap<>(8,.75F,true){@Override protected boolean removeEldestEntry(Map.Entry<Long,LegacyPerlin2D> entry){return size()>16;}};
    private static LegacyPerlin2D noise(long seed){synchronized(NOISE){return NOISE.computeIfAbsent(seed,value->new LegacyPerlin2D(new Random(value),4));}}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        var level=context.level();if(!com.hbm.config.WorldgenControls.ores())return false;if(!level.getLevel().dimension().equals(Level.OVERWORLD)||!GneissGeology.STRATUM.get())return false;
        int chunkX=context.origin().getX()&~15,chunkZ=context.origin().getZ()&~15,changed=0;var field=noise(level.getSeed());
        BlockState result=HbmContent.block("stone_gneiss").defaultBlockState();var pos=new BlockPos.MutableBlockPos();
        boolean deep=com.hbm.config.WorldgenControls.feature("deep_gneiss_stratum")&&GneissGeology.deep();int base=GneissGeology.deepBase(level),deepMin=GneissGeology.deepMin(level),deepMax=GneissGeology.deepMax(level);
        for(int x=chunkX+8;x<chunkX+24;x++)for(int z=chunkZ+8;z<chunkZ+24;z++){
            
            // The folded range is not clamped: very high noise produces no host,
            // exactly as source's unusual range=8-range branch specifies.
            if(com.hbm.config.WorldgenControls.feature("gneiss_stratum"))changed+=band(level,pos,field,x,z,result,false,0,level.getMinBuildHeight(),level.getMaxBuildHeight()-1);
            if(deep)changed+=band(level,pos,field,x,z,result,true,base,deepMin,deepMax);
        }
        return changed>0;
    }
    private static int band(WorldGenLevel level,BlockPos.MutableBlockPos pos,LegacyPerlin2D field,int x,int z,BlockState result,boolean deep,int base,int min,int max){
        var control=com.hbm.config.WorldgenControls.rule(deep?"deep_gneiss_stratum":"gneiss_stratum","stone_gneiss");if(!control.enabled())return 0;
        double scale=control.decimal("horizontalScale",.01D),threshold=control.decimal("threshold",5);double n=field.sample(x*scale,z*scale);if(!(n>threshold))return 0;
        int multiplier=control.integer("rangeMultiplier",3),maxRange=control.integer("maxRange",4),range=(int)((n-threshold)*multiplier);if(range>maxRange)range=maxRange*2-range;if(range<0)return 0;
        int center=control.integer("centerY",30+base),changed=0;for(int y=center-range;y<=center+range;y++)if(y>=min&&y<=max)changed+=replace(level,pos,x,y,z,result);return changed;
    }
    private static int replace(WorldGenLevel level,BlockPos.MutableBlockPos pos,int x,int y,int z,BlockState result){
        if(y<level.getMinBuildHeight()||y>=level.getMaxBuildHeight())return 0;pos.set(x,y,z);
        if(!level.ensureCanWrite(pos))throw new IllegalStateException("Source gneiss stratum outside generation region at "+pos);
        var state=level.getBlockState(pos);
        if(state.isRedstoneConductor(level,pos)&&(state.is(BlockTags.STONE_ORE_REPLACEABLES)||state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES))){level.setBlock(pos,result,2);return 1;}return 0;
    }
}
