package com.hbm.worldgen.dimensionores;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
/** The original minable geometry, numeric order and random draws; explicit source netherrack/end-stone host. */
public final class DimensionOreVein {
    public static int place(WorldGenLevel level,RandomSource random,int x,int y,int z,int size,BlockState ore,net.minecraft.world.level.block.Block host){
        float angle=random.nextFloat()*(float)Math.PI;
        double x1=x+8+Mth.sin(angle)*size/8F,x2=x+8-Mth.sin(angle)*size/8F;
        double z1=z+8+Mth.cos(angle)*size/8F,z2=z+8-Mth.cos(angle)*size/8F;
        double y1=y+random.nextInt(3)-2,y2=y+random.nextInt(3)-2;
        int changed=0;var pos=new BlockPos.MutableBlockPos();
        for(int step=0;step<=size;step++){
            double centerX=x1+(x2-x1)*step/size,centerY=y1+(y2-y1)*step/size,centerZ=z1+(z2-z1)*step/size;
            double scale=random.nextDouble()*size/16D,width=(Mth.sin(step*(float)Math.PI/size)+1F)*scale+1D,height=(Mth.sin(step*(float)Math.PI/size)+1F)*scale+1D;
            int minX=Mth.floor(centerX-width/2D),minY=Mth.floor(centerY-height/2D),minZ=Mth.floor(centerZ-width/2D),maxX=Mth.floor(centerX+width/2D),maxY=Mth.floor(centerY+height/2D),maxZ=Mth.floor(centerZ+width/2D);
            for(int bx=minX;bx<=maxX;bx++){double dx=(bx+.5D-centerX)/(width/2D);if(dx*dx>=1D)continue;
                for(int by=minY;by<=maxY;by++){double dy=(by+.5D-centerY)/(height/2D);if(dx*dx+dy*dy>=1D)continue;
                    for(int bz=minZ;bz<=maxZ;bz++){double dz=(bz+.5D-centerZ)/(width/2D);if(dx*dx+dy*dy+dz*dz>=1D)continue;
                        if(by<level.getMinBuildHeight()||by>=level.getMaxBuildHeight())continue;pos.set(bx,by,bz);
                        if(!level.ensureCanWrite(pos))throw new IllegalStateException("Source dimension ore vein outside generation region at "+pos);
                        if(level.getBlockState(pos).is(host)){level.setBlock(pos,ore,2);changed++;}
                    }
                }
            }
        }return changed;
    }
 private DimensionOreVein(){}
}
