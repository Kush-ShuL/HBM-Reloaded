package com.hbm.explosion.vanillant.standard;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockAllocator;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
/** Exact source fixed-radius dig rays: no energy attenuation, RNG or alternate explosion engine. */
public final class BlockAllocatorGlyphidDig implements IBlockAllocator {
    private final double maximum;private final int resolution;
    public BlockAllocatorGlyphidDig(double maximum){this(maximum,16);}
    public BlockAllocatorGlyphidDig(double maximum,int resolution){this.maximum=maximum;this.resolution=resolution;}
    @Override public HashSet<BlockPos> allocate(ExplosionVNT explosion,Level world,double x,double y,double z,float size){
        var affected=new HashSet<BlockPos>();
        for(int i=0;i<resolution;i++)for(int j=0;j<resolution;j++)for(int k=0;k<resolution;k++){
            if(i!=0&&j!=0&&k!=0&&i!=resolution-1&&j!=resolution-1&&k!=resolution-1)continue;
            double dx=(float)i/((float)resolution-1F)*2F-1F,dy=(float)j/((float)resolution-1F)*2F-1F,dz=(float)k/((float)resolution-1F)*2F-1F;
            double length=Math.sqrt(dx*dx+dy*dy+dz*dz);dx/=length;dy/=length;dz/=length;double cx=x,cy=y,cz=z,distance=0;
            for(float step=.3F;distance<=explosion.size;){
                double vx=cx-x,vy=cy-y,vz=cz-z;distance=Math.sqrt(vx*vx+vy*vy+vz*vz);var pos=BlockPos.containing(cx,cy,cz);var state=world.getBlockState(pos);
                if(!state.isAir()){
                    float resistance=state.getExplosionResistance(world,pos,explosion.compat);
                    if(explosion.exploder!=null)resistance=explosion.exploder.getBlockExplosionResistance(explosion.compat,world,pos,state,world.getFluidState(pos),resistance);
                    if(maximum<resistance||BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(ResourceLocation.parse("hbm:glyphid_spawner")))break;
                }
                if(explosion.exploder==null||explosion.exploder.shouldBlockExplode(explosion.compat,world,pos,state,explosion.size))affected.add(pos);
                cx+=dx*(double)step;cy+=dy*(double)step;cz+=dz*(double)step;
            }
        }
        return affected;
    }
}
