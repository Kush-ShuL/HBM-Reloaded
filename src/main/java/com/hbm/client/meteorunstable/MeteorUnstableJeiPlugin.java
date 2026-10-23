package com.hbm.client.meteorunstable;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.meteorunstable.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class MeteorUnstableJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm", "meteor_unstable_jei"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration) { registration.addExtraIngredients(VanillaTypes.ITEM_STACK, MeteorUnstableContent.metastableVariants()); registration.addExtraIngredients(VanillaTypes.ITEM_STACK, MeteorUnstableContent.starmetalVariants()); }
    @Override public void registerItemSubtypes(ISubtypeRegistration registration) {
        var interpreter = new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return Integer.toString(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)); }
        };
        registration.registerSubtypeInterpreter(MeteorUnstableContent.METASTABLE.get(), interpreter);
        registration.registerSubtypeInterpreter(MeteorUnstableContent.STARMETAL.get(), interpreter);
    }
}
