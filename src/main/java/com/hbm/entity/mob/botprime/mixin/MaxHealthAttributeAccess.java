package com.hbm.entity.mob.botprime.mixin;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
/** Only the vanilla validation upper bound is raised; default health and all actual AttributeInstance values remain native. */
@Mixin(RangedAttribute.class)
public interface MaxHealthAttributeAccess {
 @Accessor("maxValue") double hbm$getMaximum();
 @Mutable @Accessor("maxValue") void hbm$setMaximum(double value);
}
