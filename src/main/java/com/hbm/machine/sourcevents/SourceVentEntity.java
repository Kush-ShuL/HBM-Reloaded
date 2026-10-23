package com.hbm.machine.sourcevents;

import com.hbm.block.LegacyBlockTraits;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.entity.aircraft.bomber.EntityPinkCloudFX;
import com.hbm.entity.particle.EntityChlorineFX;
import com.hbm.entity.particle.EntityCloudFX;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Actual TileEntityVent: private java.util.Random, three Gaussian draws per powered tick and exact real entity constructors. */
public final class SourceVentEntity extends BlockEntity {
    private final Random sourceRandom=new Random();
    private final NuclearChunkAccess access=new NuclearChunkAccess();
    private BlockPos pending;
    private double x,y,z;
    public SourceVentEntity(BlockPos pos,BlockState state){super(SourceVentContent.VENT_ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,SourceVentEntity entity){
        if(!(level instanceof ServerLevel server))return;
        if(!server.hasNeighborSignal(pos)){entity.clear();return;}
        if(!(state.getBlock()instanceof SourceVentBlock block)||block.kind()==SourceVentBlock.Kind.SEAL){entity.clear();return;}
        try{
            if(entity.pending==null){
                double scale=switch(block.kind()){case CHLORINE->1.5D;case CLOUD->1.75D;case PINK->2D;default->throw new IllegalStateException("Not a source gas vent");};
                entity.x=entity.sourceRandom.nextGaussian()*scale;entity.y=entity.sourceRandom.nextGaussian()*scale;entity.z=entity.sourceRandom.nextGaussian()*scale;
                // Cast each offset before adding the integer block coordinate, exactly as the original.
                entity.pending=pos.offset((int)entity.x,(int)entity.y,(int)entity.z);
            }
            BlockPos target=entity.pending;
            if(!server.isOutsideBuildHeight(target)&&server.getChunkSource().getChunkNow(target.getX()>>4,target.getZ()>>4)==null&&entity.access.get(server,target.getX()>>4,target.getZ()>>4)==null)return;
            if(!LegacyBlockTraits.isNormalCube(server,target,server.getBlockState(target))){
                var cloud=switch(block.kind()){
                    case CHLORINE->new EntityChlorineFX(server,target.getX(),target.getY(),target.getZ(),entity.x/2,entity.y/2,entity.z/2);
                    case CLOUD->new EntityCloudFX(server,target.getX(),target.getY(),target.getZ(),entity.x/2,entity.y/2,entity.z/2);
                    case PINK->new EntityPinkCloudFX(server,target.getX(),target.getY(),target.getZ(),entity.x/2,entity.y/2,entity.z/2);
                    default->throw new IllegalStateException("Not a source gas vent");
                };
                server.addFreshEntity(cloud);
            }
            entity.clear();
        }catch(RuntimeException|Error failure){entity.clear();throw failure;}
    }
    private void clear(){pending=null;access.close();}
    @Override public void setRemoved(){clear();super.setRemoved();}
    @Override public void onChunkUnloaded(){clear();super.onChunkUnloaded();}
}
