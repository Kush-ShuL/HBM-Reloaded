package com.hbm.client.sourceslab;

import com.hbm.block.masonry.LegacyMultiSlabBlock;
import com.hbm.block.sourceslab.SourceSlabsContent;
import com.hbm.hazard.HbmHazards;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class SourceSlabsJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm", "source_slabs255"); }
    @Override public void registerItemSubtypes(ISubtypeRegistration registration) {
        SourceSlabsContent.stoneItems().forEach(item -> registration.registerSubtypeInterpreter(item, new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return ((LegacyMultiSlabBlock)item.getBlock()).material(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return getSubtypeData(stack, context).toString(); }
        }));
    }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, SourceSlabsContent.creativeStacks("block"));
    }
    @Override public void onRuntimeAvailable(IJeiRuntime runtime) {
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, java.util.List.of(
            new ItemStack(SourceSlabsContent.PINK_ITEM.get()), new ItemStack(SourceSlabsContent.PINK_DOUBLE_ITEM.get()), new ItemStack(SourceSlabsContent.STONE_DOUBLE_ITEM.get())));
    }
}
