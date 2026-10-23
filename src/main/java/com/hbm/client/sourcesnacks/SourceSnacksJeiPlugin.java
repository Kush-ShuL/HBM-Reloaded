package com.hbm.client.sourcesnacks;

import com.hbm.item.sourcesnacks.ItemTemFlakes;
import com.hbm.item.sourcesnacks.SourceSnacksContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class SourceSnacksJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:source_snacks_jei"); }
    @Override public void registerItemSubtypes(ISubtypeRegistration event) {
        event.registerSubtypeInterpreter(SourceSnacksContent.TEM_FLAKES.get(), new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return ItemTemFlakes.metadata(stack); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return Integer.toString(ItemTemFlakes.metadata(stack)); }
        });
    }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) {
        event.addExtraIngredients(VanillaTypes.ITEM_STACK, SourceSnacksContent.indexedItems());
    }
}
