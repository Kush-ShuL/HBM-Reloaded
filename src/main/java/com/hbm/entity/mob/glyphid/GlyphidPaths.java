package com.hbm.entity.mob.glyphid;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.entity.mob.glyphid.ai.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
/** Source PathFinderUtils endpoint projection; all actual path search remains in the unique LegacyMobPath. */
public final class GlyphidPaths {
    public static LegacyMobPath toEntity(LegacyCreatureMob from,Entity target,float maximum){return toCoordinates(from,target.getX(),target.getY(),target.getZ(),maximum);}
    public static LegacyMobPath toCoordinates(LegacyCreatureMob from,double tx,double ty,double tz,float maximum){
        Vec3 delta=new Vec3(tx-from.getX(),ty-from.getY(),tz-from.getZ()).normalize().scale(maximum);
        int x=Mth.floor(from.getX()+delta.x),y=Mth.floor(from.getY()+delta.y),z=Mth.floor(from.getZ()+delta.z);boolean solid=false;
        for(int iy=y;iy>y-10;iy--)if(ground(from,x,iy,z)){solid=true;y=iy;break;}
        if(!solid)for(int iy=y+10;iy>y;iy--)if(ground(from,x,iy,z)){y=iy;break;}
        return LegacyMobPath.toPosition(from,x,y,z,maximum,true,false,true,true);
    }
    private static boolean ground(LegacyCreatureMob from,int x,int y,int z){var pos=new BlockPos(x,y,z);return !LegacyMaterials.of(from.level().getBlockState(pos)).blocksMovement()&&from.level().getBlockState(pos.below()).isRedstoneConductor(from.level(),pos.below());}
    private GlyphidPaths(){}
}
