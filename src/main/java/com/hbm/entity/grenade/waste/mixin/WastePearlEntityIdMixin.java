package com.hbm.entity.grenade.waste.mixin;

import com.hbm.entity.grenade.waste.WastePearlContent;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Source Forge mod-entity save name; no deleted item or unrelated entity alias. */
@Mixin(value=EntityType.class,remap=false)
public abstract class WastePearlEntityIdMixin {
    @Inject(method="by(Lnet/minecraft/nbt/CompoundTag;)Ljava/util/Optional;",at=@At("HEAD"),cancellable=true)
    private static void hbm$sourceWastePearl(CompoundTag tag,CallbackInfoReturnable<Optional<EntityType<?>>> result){
        if(tag.getString("id").equals("hbm.entity_waste_pearl"))result.setReturnValue(Optional.of(WastePearlContent.WASTE_PEARL.get()));
    }
}
