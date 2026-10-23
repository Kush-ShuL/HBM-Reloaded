package com.hbm.machine.tesla.mixin;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
/** Source Tesla changes the charged watcher directly; lightning would add unrelated damage/fire. */
@Mixin(value=Creeper.class,remap=false)
public interface CreeperPowerAccess {
    @Accessor("DATA_IS_POWERED") static EntityDataAccessor<Boolean> hbm$powered(){throw new AssertionError("Mixin accessor was not applied");}
}
