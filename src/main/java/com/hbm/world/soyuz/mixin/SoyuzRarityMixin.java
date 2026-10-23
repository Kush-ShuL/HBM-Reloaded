package com.hbm.world.soyuz.mixin;

import com.hbm.world.soyuz.SoyuzItem;
import com.hbm.world.soyuz.GlassRecordItem;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Old Item.getRarity was per-stack and ignored enchantment rarity promotion. */
@Mixin(value = ItemStack.class, remap = false)
public abstract class SoyuzRarityMixin {
    @Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
    private void hbm$soyuzSkinRarity(CallbackInfoReturnable<Rarity> result) {
        var stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof SoyuzItem) result.setReturnValue(SoyuzItem.sourceRarity(stack));
        else if (stack.getItem() instanceof GlassRecordItem) result.setReturnValue(Rarity.RARE);
    }
}
