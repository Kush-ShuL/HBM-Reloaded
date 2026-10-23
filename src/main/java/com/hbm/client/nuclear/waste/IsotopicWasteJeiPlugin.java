package com.hbm.client.nuclear.waste;

import com.hbm.item.nuclear.waste.IsotopicWasteContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Metadata distinguishes real isotope composition; the actual existing SILEX category supplies processing. */
@JeiPlugin
public final class IsotopicWasteJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:isotopic_waste"); }

    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) {
        event.addExtraIngredients(VanillaTypes.ITEM_STACK, IsotopicWasteContent.allStacks());
    }

    @Override public void registerItemSubtypes(ISubtypeRegistration event) {
        for (var holder : IsotopicWasteContent.LONG_ITEMS) {
            var item = holder.get();
            event.registerSubtypeInterpreter(item, new ISubtypeInterpreter<ItemStack>() {
                @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return item.metadata(stack); }
                @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return getSubtypeData(stack, context).toString(); }
            });
        }
        for (var holder : IsotopicWasteContent.SHORT_ITEMS) {
            var item = holder.get();
            event.registerSubtypeInterpreter(item, new ISubtypeInterpreter<ItemStack>() {
                @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return item.metadata(stack); }
                @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return getSubtypeData(stack, context).toString(); }
            });
        }
    }
}
