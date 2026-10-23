package com.hbm.weapon.sedna.gun.quadro;
import com.hbm.block.fluid.LegacyFluidBlock;
import com.hbm.block.digamma.DigammaFlameBlock;
import com.hbm.block.gas.GasBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;

/** Library.rayTrace(player,200,1) -> official1.7.10 World.func_147447_a(false,false,true).
 * Keep source DDA arithmetic, tie order,201transition bound and last-MISS position.
 * Native selection boxes express the actual migrated/modern block geometry. */
public final class QuadroTargetRay {
    private QuadroTargetRay(){}
    private record Target(Vec3 hit,BlockPos block){}
    static Vec3 trace(Player player,double range){var result=target(player,range);return result==null?null:result.hit;}
    /** The source returned the actual selected/last-MISS cell, not floor(hitVec). */
    public static BlockPos traceBlock(Player player,double range){var result=target(player,range);return result==null?null:result.block;}
    /** Source rangefinder uses false,true,false: ignore no-bounding-box blocks and no last MISS. */
    public static Vec3 traceSolid(Player player,double range){var result=target(player,range,true,false);return result==null?null:result.hit;}
    /** Original false,true,false ray for source callers with explicit, non-eye/non-look endpoints. */
    public static BlockPos traceSolidBlock(Level level,Vec3 start,Vec3 end,Player player){var result=trace(level,start,end,player,true,false);return result==null?null:result.block;}
    private static Target target(Player player,double range){return target(player,range,false,true);}
    private static Target target(Player player,double range,boolean ignoreNoBox,boolean returnLast){
        float yaw=-player.getYRot()*.017453292F-(float)Math.PI,pitch=-player.getXRot()*.017453292F;
        float horizontal=-Mth.cos(pitch);
        Vec3 start=new Vec3(player.getX(),player.getY()+player.getEyeHeight(),player.getZ());
        Vec3 end=start.add((Mth.sin(yaw)*horizontal)*range,Mth.sin(pitch)*range,(Mth.cos(yaw)*horizontal)*range);
        return trace(player.level(),start,end,player,ignoreNoBox,returnLast);
    }
    private static Target trace(Level level,Vec3 start,Vec3 end,Player player,boolean ignoreNoBox,boolean returnLast){
        if(nan(start)||nan(end))return null;
        int ex=Mth.floor(end.x),ey=Mth.floor(end.y),ez=Mth.floor(end.z),x=Mth.floor(start.x),y=Mth.floor(start.y),z=Mth.floor(start.z);
        var position=new BlockPos.MutableBlockPos(x,y,z);var state=level.getBlockState(position);
        if(collidable(state)&&(!ignoreNoBox||!state.getCollisionShape(level,position,CollisionContext.of(player)).isEmpty())){var hit=intercept(state,level,position,start,end,player);if(hit!=null)return new Target(hit,position.immutable());}
        Target last=null;int steps=200;
        while(steps-->=0){
            if(nan(start))return null;
            if(x==ex&&y==ey&&z==ez)return returnLast?last:null;
            boolean stepX=true,stepY=true,stepZ=true;double bx=999D,by=999D,bz=999D;
            if(ex>x)bx=x+1D;else if(ex<x)bx=x+0D;else stepX=false;
            if(ey>y)by=y+1D;else if(ey<y)by=y+0D;else stepY=false;
            if(ez>z)bz=z+1D;else if(ez<z)bz=z+0D;else stepZ=false;
            double tx=999D,ty=999D,tz=999D,dx=end.x-start.x,dy=end.y-start.y,dz=end.z-start.z;
            if(stepX)tx=(bx-start.x)/dx;if(stepY)ty=(by-start.y)/dy;if(stepZ)tz=(bz-start.z)/dz;
            int face;
            if(tx<ty&&tx<tz){face=ex>x?4:5;start=new Vec3(bx,start.y+dy*tx,start.z+dz*tx);}
            else if(ty<tz){face=ey>y?0:1;start=new Vec3(start.x+dx*ty,by,start.z+dz*ty);}
            else{face=ez>z?2:3;start=new Vec3(start.x+dx*tz,start.y+dy*tz,bz);}
            x=Mth.floor(start.x);if(face==5)x--;y=Mth.floor(start.y);if(face==1)y--;z=Mth.floor(start.z);if(face==3)z--;
            position.set(x,y,z);state=level.getBlockState(position);
            if(collidable(state)&&(!ignoreNoBox||!state.getCollisionShape(level,position,CollisionContext.of(player)).isEmpty())){var hit=intercept(state,level,position,start,end,player);if(hit!=null)return new Target(hit,position.immutable());}
            else last=new Target(start,position.immutable());
        }
        return returnLast?last:null;
    }
    private static boolean nan(Vec3 point){return Double.isNaN(point.x)||Double.isNaN(point.y)||Double.isNaN(point.z);}
    // Source overrides: BlockAir, BlockLiquid/Forge fluid, BlockFire, BlockGasBase and DigammaFlame.
    // A portal is deliberately NOT rejected here: source keeps its ordinary canCollideCheck,
    // and its actual selection geometry decides the hit; do not synthesize a last-MISS merely for non-solid portals.
    private static boolean collidable(BlockState state){return !state.isAir()&&!(state.getBlock() instanceof LiquidBlock)&&!(state.getBlock() instanceof LegacyFluidBlock)&&!(state.getBlock() instanceof BaseFireBlock)&&!(state.getBlock() instanceof GasBlock)&&!(state.getBlock() instanceof DigammaFlameBlock);}
    private static Vec3 intercept(BlockState state,Level level,BlockPos position,Vec3 start,Vec3 end,Player player){
        Vec3 localStart=start.subtract(position.getX(),position.getY(),position.getZ()),localEnd=end.subtract(position.getX(),position.getY(),position.getZ()),best=null;
        for(var box:state.getShape(level,position,CollisionContext.of(player)).toAabbs()){
            for(int face=0;face<6;face++){
                int axis=face>>1;double from=axis==0?localStart.x:axis==1?localStart.y:localStart.z,to=axis==0?localEnd.x:axis==1?localEnd.y:localEnd.z;
                double plane=axis==0?(face==0?box.minX:box.maxX):axis==1?(face==2?box.minY:box.maxY):(face==4?box.minZ:box.maxZ),delta=to-from;
                if(delta*delta<1.0000000116860974E-7D)continue;
                double t=(plane-from)/delta;if(t<0D||t>1D)continue;
                Vec3 point=new Vec3(localStart.x+(localEnd.x-localStart.x)*t,localStart.y+(localEnd.y-localStart.y)*t,localStart.z+(localEnd.z-localStart.z)*t);
                boolean inside=axis==0?point.y>=box.minY&&point.y<=box.maxY&&point.z>=box.minZ&&point.z<=box.maxZ:axis==1?point.x>=box.minX&&point.x<=box.maxX&&point.z>=box.minZ&&point.z<=box.maxZ:point.x>=box.minX&&point.x<=box.maxX&&point.y>=box.minY&&point.y<=box.maxY;
                if(inside&&(best==null||localStart.distanceToSqr(point)<localStart.distanceToSqr(best)))best=point;
            }
        }
        return best==null?null:best.add(position.getX(),position.getY(),position.getZ());
    }
}
