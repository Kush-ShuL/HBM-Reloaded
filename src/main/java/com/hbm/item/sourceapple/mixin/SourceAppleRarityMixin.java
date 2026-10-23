package com.hbm.item.sourceapple.mixin;

import com.hbm.item.sourceapple.SourceMetalAppleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Modern ItemStack owns rarity; preserve only these actual source per-stack Item.getRarity overrides. */
@Mixin(ItemStack.class)
public abstract class SourceAppleRarityMixin {
    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void hbm$sourceAppleRarity(CallbackInfoReturnable<Rarity> callback) {
        var stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof SourceMetalAppleItem apple) callback.setReturnValue(apple.sourceRarity(stack));
    }
}
