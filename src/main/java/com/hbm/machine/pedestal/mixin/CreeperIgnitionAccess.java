package com.hbm.machine.pedestal.mixin;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value=Creeper.class,remap=false)
public interface CreeperIgnitionAccess {
    @Accessor("DATA_IS_IGNITED") static EntityDataAccessor<Boolean> hbm$ignited(){throw new AssertionError();}
}
