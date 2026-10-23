package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockAllocator;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** Source naval-mine allocator: rays ignore liquid resistance and never select liquid blocks. */
public final class BlockAllocatorWater implements IBlockAllocator {
    protected int resolution;
    public BlockAllocatorWater(int resolution){this.resolution=resolution;}
    @Override public HashSet<BlockPos> allocate(ExplosionVNT explosion,Level level,double x,double y,double z,float size){
        var result=new HashSet<BlockPos>();
        for(int i=0;i<resolution;i++)for(int j=0;j<resolution;j++)for(int k=0;k<resolution;k++){
            if(i!=0&&i!=resolution-1&&j!=0&&j!=resolution-1&&k!=0&&k!=resolution-1)continue;
            double dx=(float)i/((float)resolution-1F)*2F-1F,dy=(float)j/((float)resolution-1F)*2F-1F,dz=(float)k/((float)resolution-1F)*2F-1F;
            double norm=Math.sqrt(dx*dx+dy*dy+dz*dz);dx/=norm;dy/=norm;dz/=norm;
            float power=size*(.7F+level.random.nextFloat()*.6F);double cx=x,cy=y,cz=z;
            for(float step=.3F;power>0;power-=step*.75F){
                var pos=BlockPos.containing(cx,cy,cz);var state=level.getBlockState(pos);boolean liquid=state.liquid();
                if(!state.isAir()&&!liquid){float resistance=state.getExplosionResistance(level,pos,explosion.compat);if(explosion.exploder!=null)resistance=explosion.exploder.getBlockExplosionResistance(explosion.compat,level,pos,state,state.getFluidState(),resistance);power-=(resistance+.3F)*step;}
                if(power>0&&(explosion.exploder==null||explosion.exploder.shouldBlockExplode(explosion.compat,level,pos,state,power))&&!liquid)result.add(pos);
                cx+=dx*(double)step;cy+=dy*(double)step;cz+=dz*(double)step;
            }
        }
        return result;
    }
}
