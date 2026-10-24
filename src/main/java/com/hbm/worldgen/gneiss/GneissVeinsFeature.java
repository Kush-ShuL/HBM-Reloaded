package com.hbm.worldgen.gneiss;

import com.hbm.registry.HbmContent;
import com.hbm.worldgen.ore.OreWorldgen;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Original DungeonToolbox eight gneiss-host calls; WorldGenMinable ellipsoids only replace the actual host identity. */
public final class GneissVeinsFeature extends Feature<NoneFeatureConfiguration> {
    public GneissVeinsFeature(){super(NoneFeatureConfiguration.CODEC);}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        var level=context.level();if(!com.hbm.config.WorldgenControls.ores())return false;var dimension=level.getLevel().dimension();if(dimension.equals(Level.NETHER)||dimension.equals(Level.END)||!dimension.equals(Level.OVERWORLD)&&!com.hbm.config.WorldgenControls.moddedDimensions()||!OreWorldgen.OVERWORLD.get())return false;
        int chunkX=context.origin().getX()&~15,chunkZ=context.origin().getZ()&~15,changed=0;var random=context.random();
        if(com.hbm.config.WorldgenControls.feature("gneiss_ores"))for(var profile:GneissGeology.PROFILES)changed+=population(level,random,chunkX,chunkZ,profile,GneissGeology.count(profile),0,level.getMinBuildHeight(),level.getMaxBuildHeight()-1,false);
        int minimum=GneissGeology.deepMin(level),maximum=GneissGeology.deepMax(level);
        if(com.hbm.config.WorldgenControls.feature("deep_gneiss_ores")&&GneissGeology.deep()&&minimum<=maximum)for(var profile:GneissGeology.PROFILES){int count=(int)Math.round(GneissGeology.count(profile)*OreWorldgen.DEEP_MULTIPLIER.get());changed+=population(level,random,chunkX,chunkZ,profile,count,GneissGeology.deepBase(level),minimum,maximum,true);}
        return changed>0;
    }
    private static int population(WorldGenLevel level,RandomSource random,int chunkX,int chunkZ,GneissGeology.Profile profile,int count,int base,int minimum,int maximum,boolean deep){
        var control=com.hbm.config.WorldgenControls.rule(!deep?"gneiss_ores":"deep_gneiss_ores",profile.symbol());if(!control.enabled())return 0;count=control.count(count);
        int changed=0;BlockState ore=HbmContent.block(profile.symbol()).defaultBlockState();
        for(int vein=0;vein<count;vein++){int x=chunkX+random.nextInt(16),y=control.height(random,base+profile.minY(),base+profile.minY()+Math.max(0,profile.variance()-1),profile.variance()>0),z=chunkZ+random.nextInt(16);changed+=vein(level,random,x,y,z,control.size(profile.size()),ore,minimum,maximum);}return changed;
    }
    /** Same original WorldGenMinable geometry/numeric operations as existing LegacyOreVein, with source explicit gneiss target. */
    private static int vein(WorldGenLevel level,RandomSource random,int x,int y,int z,int size,BlockState ore,int minimum,int maximum){
        float angle=random.nextFloat()*(float)Math.PI;
        double x1=x+8+Mth.sin(angle)*size/8F,x2=x+8-Mth.sin(angle)*size/8F;
        double z1=z+8+Mth.cos(angle)*size/8F,z2=z+8-Mth.cos(angle)*size/8F;
        double y1=y+random.nextInt(3)-2,y2=y+random.nextInt(3)-2;
        var host=HbmContent.block("stone_gneiss");int changed=0;var pos=new BlockPos.MutableBlockPos();
        for(int step=0;step<=size;step++){
            double centerX=x1+(x2-x1)*step/size,centerY=y1+(y2-y1)*step/size,centerZ=z1+(z2-z1)*step/size;
            double scale=random.nextDouble()*size/16D,width=(Mth.sin(step*(float)Math.PI/size)+1F)*scale+1D,height=(Mth.sin(step*(float)Math.PI/size)+1F)*scale+1D;
            int minX=Mth.floor(centerX-width/2D),minY=Mth.floor(centerY-height/2D),minZ=Mth.floor(centerZ-width/2D),maxX=Mth.floor(centerX+width/2D),maxY=Mth.floor(centerY+height/2D),maxZ=Mth.floor(centerZ+width/2D);
            for(int bx=minX;bx<=maxX;bx++){double dx=(bx+.5D-centerX)/(width/2D);if(dx*dx>=1D)continue;
                for(int by=minY;by<=maxY;by++){double dy=(by+.5D-centerY)/(height/2D);if(dx*dx+dy*dy>=1D)continue;
                    for(int bz=minZ;bz<=maxZ;bz++){double dz=(bz+.5D-centerZ)/(width/2D);if(dx*dx+dy*dy+dz*dz>=1D)continue;
                        if(by<minimum||by>maximum||by<level.getMinBuildHeight()||by>=level.getMaxBuildHeight())continue;pos.set(bx,by,bz);
                        if(!level.ensureCanWrite(pos))throw new IllegalStateException("Source gneiss vein outside generation region at "+pos);
                        if(level.getBlockState(pos).is(host)){level.setBlock(pos,ore,2);changed++;}
                    }
                }
            }
        }return changed;
    }
}
