package com.hbm.ore.mixin;

import com.hbm.ore.RawOreContent;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Shift-click recognizes the same processing identity as the native furnace ticker. */
@Mixin(value = AbstractFurnaceMenu.class, remap = false)
public abstract class OreFurnaceMenuMixin {
    @ModifyArg(method = "canSmelt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"), index = 1)
    private RecipeInput hbm$rawInput(RecipeInput input) {
        return input instanceof SingleRecipeInput single
                ? new SingleRecipeInput(RawOreContent.processingStack(single.item())) : input;
    }
}
