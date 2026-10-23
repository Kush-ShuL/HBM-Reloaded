package com.hbm.explosion.vanillant.standard;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockAllocator;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
/** Original constant-range outer-cube rays: resistance threshold, not energy attenuation; all 0.3F steps including source one-step overshoot. */
public class BlockAllocatorBulkie implements IBlockAllocator {
 protected double maximum;protected int resolution;
 public BlockAllocatorBulkie(double maximum){this(maximum,16);}public BlockAllocatorBulkie(double maximum,int resolution){this.maximum=maximum;this.resolution=resolution;}
 @Override public HashSet<BlockPos> allocate(ExplosionVNT explosion,Level world,double x,double y,double z,float size){var affected=new HashSet<BlockPos>();for(int i=0;i<resolution;i++)for(int j=0;j<resolution;j++)for(int k=0;k<resolution;k++){if(i!=0&&i!=resolution-1&&j!=0&&j!=resolution-1&&k!=0&&k!=resolution-1)continue;double dx=(float)i/((float)resolution-1F)*2F-1F,dy=(float)j/((float)resolution-1F)*2F-1F,dz=(float)k/((float)resolution-1F)*2F-1F,length=Math.sqrt(dx*dx+dy*dy+dz*dz);dx/=length;dy/=length;dz/=length;double cx=x,cy=y,cz=z,distance=0;for(float step=.3F;distance<=explosion.size;){double ax=cx-x,ay=cy-y,az=cz-z;distance=Math.sqrt(ax*ax+ay*ay+az*az);var pos=BlockPos.containing(cx,cy,cz);var state=world.getBlockState(pos);if(!state.isAir()){float resistance=state.getExplosionResistance(world,pos,explosion.compat);if(explosion.exploder!=null)resistance=explosion.exploder.getBlockExplosionResistance(explosion.compat,world,pos,state,world.getFluidState(pos),resistance);if(maximum<resistance)break;}if(explosion.exploder==null||explosion.exploder.shouldBlockExplode(explosion.compat,world,pos,state,explosion.size))affected.add(pos);cx+=dx*(double)step;cy+=dy*(double)step;cz+=dz*(double)step;}}return affected;}
}
