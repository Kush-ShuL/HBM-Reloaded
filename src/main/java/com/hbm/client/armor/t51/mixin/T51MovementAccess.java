package com.hbm.client.armor.t51.mixin;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
/** Source ArmorFSB.steppy read-only access; no movement or stepping behavior is replaced. */
@Mixin(value=Entity.class,remap=false)
public interface T51MovementAccess {
 @Accessor("nextStep") float hbm$t51NextStep();
}
