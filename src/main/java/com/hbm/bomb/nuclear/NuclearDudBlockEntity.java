package com.hbm.bomb.nuclear;

import com.hbm.exposure.HbmExposure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Original two-tick radiation aura, using the victim's center and its linear radial falloff. */
public final class NuclearDudBlockEntity extends BlockEntity {
 public NuclearDudBlockEntity(BlockPos pos,BlockState state){super(NuclearSpecialContent.DUD_ENTITY.get(),pos,state);}
 public void serverTick(){if(level==null||level.isClientSide||level.getGameTime()%2!=0)return;var type=NuclearDudBlock.type(getBlockState());float dose=switch(type){case BALEFIRE->1F;case NUKE->.25F;case SALTED->.5F;default->0F;};if(dose==0)return;double range=type==NuclearDudBlock.Type.BALEFIRE?15D:10D,x=worldPosition.getX()+.5D,y=worldPosition.getY()+.5D,z=worldPosition.getZ()+.5D;for(var entity:level.getEntitiesOfClass(LivingEntity.class,new AABB(x,y,z,x,y,z).inflate(range))){double dx=x-entity.getX(),dy=y-(entity.getY()+entity.getBbHeight()/2F),dz=z-entity.getZ(),distance=Math.sqrt(dx*dx+dy*dy+dz*dz);if(distance<=range)HbmExposure.contaminate(entity,HbmExposure.Contamination.CREATIVE,dose*(float)(1D-distance/range));}}
}
