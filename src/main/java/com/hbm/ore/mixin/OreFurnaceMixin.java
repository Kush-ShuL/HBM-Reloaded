package com.hbm.ore.mixin;

import com.hbm.ore.OreProcessing;
import com.hbm.ore.RawOreContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Native smelting/blasting retains fuel, time, experience, recipes-used and hopper behavior. */
@Mixin(value = AbstractFurnaceBlockEntity.class, remap = false)
public abstract class OreFurnaceMixin {
    @ModifyArg(method = {"serverTick", "getTotalCookTime"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"), index = 0)
    private static RecipeInput hbm$processingIdentity(RecipeInput input) {
        return input instanceof SingleRecipeInput single
                ? new SingleRecipeInput(RawOreContent.processingStack(single.item())) : input;
    }

    @Redirect(method = "canBurn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/AbstractCookingRecipe;assemble(Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack hbm$reserveMaximum(AbstractCookingRecipe recipe, SingleRecipeInput input, HolderLookup.Provider lookup) {
        var result = recipe.assemble(new SingleRecipeInput(RawOreContent.processingStack(input.item())), lookup);
        return OreProcessing.maximumOutput(result, OreProcessing.rate(input.item()));
    }

    @Inject(method = "canBurn", at = @At("HEAD"), cancellable = true)
    private static void hbm$respectEmptySlotLimit(RegistryAccess access, RecipeHolder<?> holder,
            NonNullList<ItemStack> items, int limit, AbstractFurnaceBlockEntity furnace,
            CallbackInfoReturnable<Boolean> result) {
        if (holder == null || items.getFirst().isEmpty()) return;
        ItemStack input = items.getFirst();
        if (!RawOreContent.isRaw(input) && !RawOreContent.isManagedOre(input)) return;
        var recipe = (AbstractCookingRecipe) holder.value();
        var output = recipe.assemble(new SingleRecipeInput(RawOreContent.processingStack(input)), access);
        if (OreProcessing.maximumCount(output.getCount(), OreProcessing.rate(input)) > Math.min(limit, output.getMaxStackSize()))
            result.setReturnValue(false);
    }

    @Redirect(method = "burn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/AbstractCookingRecipe;assemble(Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack hbm$committedYield(AbstractCookingRecipe recipe, SingleRecipeInput input,
            HolderLookup.Provider lookup, RegistryAccess access, RecipeHolder<?> holder,
            NonNullList<ItemStack> items, int limit, AbstractFurnaceBlockEntity furnace) {
        var result = recipe.assemble(new SingleRecipeInput(RawOreContent.processingStack(input.item())), lookup);
        return OreProcessing.output(result, OreProcessing.rate(input.item()), furnace.getLevel().random);
    }
}
