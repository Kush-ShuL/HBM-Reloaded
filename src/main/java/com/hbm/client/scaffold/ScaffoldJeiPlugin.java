package com.hbm.client.scaffold;

import com.hbm.block.scaffold.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class ScaffoldJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm", "scaffold_jei"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration) { registration.addExtraIngredients(VanillaTypes.ITEM_STACK, ScaffoldContent.variants()); }
    @Override public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ScaffoldContent.ITEM.get(), new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return SteelScaffoldItem.color(stack); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return Integer.toString(SteelScaffoldItem.color(stack)); }
        });
    }
}
