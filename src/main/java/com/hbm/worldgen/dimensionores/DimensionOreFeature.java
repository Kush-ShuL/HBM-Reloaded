package com.hbm.worldgen.dimensionores;

import com.hbm.registry.HbmContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Exact eight active ordinary Nether/End DungeonToolbox calls and original explicit host identities. */
public final class DimensionOreFeature extends Feature<NoneFeatureConfiguration> {
    private final boolean nether;
    public DimensionOreFeature(boolean nether){super(NoneFeatureConfiguration.CODEC);this.nether=nether;}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        var level=context.level();if(!com.hbm.config.WorldgenControls.oreFeature(nether?"nether_ores":"end_ores"))return false;if(!level.getLevel().dimension().equals(nether?Level.NETHER:Level.END)||(nether?!DimensionOres.NETHER.get():!DimensionOres.END.get()))return false;
        int chunkX=context.origin().getX()&~15,chunkZ=context.origin().getZ()&~15,changed=0;var random=context.random();
        for(var profile:DimensionOres.PROFILES){
            if(!profile.dimension().equals(nether?"nether":"end")||profile.plutonium()&&!DimensionOres.PLUTONIUM.get())continue;
            var control=com.hbm.config.WorldgenControls.rule(nether?"nether_ores":"end_ores",profile.symbol());if(!control.enabled())continue;
            var result=HbmContent.block(profile.symbol()).defaultBlockState();int count=control.count(DimensionOres.count(profile));
            for(int i=0;i<count;i++){
                int x=chunkX+random.nextInt(16),y=control.overrides("minY")||control.overrides("maxY")?control.height(random,DimensionOres.height(level,nether,profile.minY()),DimensionOres.height(level,nether,profile.minY()+Math.max(0,profile.variance()-1)),true):DimensionOres.height(level,nether,profile.minY()+(profile.variance()>0?random.nextInt(profile.variance()):0)),z=chunkZ+random.nextInt(16);
                changed+=DimensionOreVein.place(level,random,x,y,z,control.size(profile.size()),result,nether?Blocks.NETHERRACK:Blocks.END_STONE);
            }
        }return changed>0;
    }
}
