package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockAllocator;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BlockAllocatorStandard implements IBlockAllocator {
    protected int resolution;
    public BlockAllocatorStandard(){this(16);}
    public BlockAllocatorStandard(int resolution){this.resolution=resolution;}
    @Override public HashSet<BlockPos> allocate(ExplosionVNT explosion,Level world,double x,double y,double z,float size){
        var affected=new HashSet<BlockPos>();
        for(int i=0;i<resolution;i++)for(int j=0;j<resolution;j++)for(int k=0;k<resolution;k++){
            if(i!=0&&i!=resolution-1&&j!=0&&j!=resolution-1&&k!=0&&k!=resolution-1)continue;
            double dx=(float)i/((float)resolution-1F)*2F-1F,dy=(float)j/((float)resolution-1F)*2F-1F,dz=(float)k/((float)resolution-1F)*2F-1F;
            double norm=Math.sqrt(dx*dx+dy*dy+dz*dz);dx/=norm;dy/=norm;dz/=norm;
            float power=size*(.7F+world.random.nextFloat()*.6F);
            double cx=x,cy=y,cz=z;
            for(float step=.3F;power>0;power-=step*.75F){
                BlockPos pos=BlockPos.containing(cx,cy,cz);var state=world.getBlockState(pos);
                if(!state.isAir()){
                    float resistance=state.getExplosionResistance(world,pos,explosion.compat);
                    if(explosion.exploder!=null)resistance=explosion.exploder.getBlockExplosionResistance(explosion.compat,world,pos,state,world.getFluidState(pos),resistance);
                    power-=(resistance+.3F)*step;
                }
                if(power>0&&(explosion.exploder==null||explosion.exploder.shouldBlockExplode(explosion.compat,world,pos,state,power)))affected.add(pos);
                cx+=dx*(double)step;cy+=dy*(double)step;cz+=dz*(double)step;
            }
        }
        return affected;
    }
}
