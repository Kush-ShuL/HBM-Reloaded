package com.hbm.machine.radiationcleanup;

import com.hbm.exposure.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Original TileEntityDecon. The source has no inventory/HE/private fluid or GUI and no custom saved fields. */
public final class DeconBlockEntity extends BlockEntity {
    public static java.util.function.BiConsumer<Level,BlockPos> CLIENT_PARTICLE=(level,pos)->{};
    public DeconBlockEntity(BlockPos pos,BlockState state){super(RadiationCleanupContent.DECON_ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,DeconBlockEntity machine){
        if(!level.isClientSide){
            var box=new AABB(pos.getX()-.5,pos.getY(),pos.getZ()-.5,pos.getX()+1.5,pos.getY()+2,pos.getZ()+1.5);
            for(var living:level.getEntitiesOfClass(LivingEntity.class,box)){
                HbmExposure.incrementRadiation(living,-.5F);
                living.removeEffect(ExposureEffects.RADIATION);
                HbmExposure.clearContamination(living);
            }
        }else CLIENT_PARTICLE.accept(level,pos);
    }
}
