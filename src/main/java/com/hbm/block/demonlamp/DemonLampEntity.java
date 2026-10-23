package com.hbm.block.demonlamp;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.exposure.HbmExposure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** Actual TileEntityDemonLamp dose, inverse-square distance and per-block vanilla blast attenuation. */
public final class DemonLampEntity extends BlockEntity {
    private final NuclearChunkAccess chunks=new NuclearChunkAccess();
    public DemonLampEntity(BlockPos pos,BlockState state){super(DemonLampContent.ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,DemonLampEntity lamp){if(level instanceof ServerLevel server)lamp.radiate(server);}
    private void radiate(ServerLevel world){
        double x=worldPosition.getX()+.5,y=worldPosition.getY()+.5,z=worldPosition.getZ()+.5;
        for(var living:world.getEntitiesOfClass(LivingEntity.class,new AABB(x,y,z,x,y,z).inflate(25))){
            var delta=new Vec3(living.getX()-x,living.getY()+living.getEyeHeight()-y,living.getZ()-z);double length=delta.length();var direction=delta.normalize();float resistance=0;boolean ready=true;
            for(int i=1;i<length;i++){
                var at=BlockPos.containing(x+direction.x*i,y+direction.y*i,z+direction.z*i);
                // Read the same complete path only after its actual chunks are available; never join cold generation.
                var chunk=chunks.get(world,at.getX()>>4,at.getZ()>>4);if(chunk==null){ready=false;break;}
                resistance+=chunk.getBlockState(at).getBlock().getExplosionResistance();
            }
            if(!ready)continue; // The source is a fresh, non-random per-tick exposure; retry its current ray next tick.
            if(resistance<1)resistance=1;
            float rads=100000F;rads/=(float)resistance;rads/=(float)(length*length);
            HbmExposure.contaminate(living,HbmExposure.Contamination.CREATIVE,rads);
            if(length<2)living.hurt(world.damageSources().inFire(),100);
        }
    }
    @Override public void setRemoved(){chunks.close();super.setRemoved();}
    @Override public void onChunkUnloaded(){chunks.close();super.onChunkUnloaded();}
}
