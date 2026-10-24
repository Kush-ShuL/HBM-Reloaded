package com.hbm.client.nuclear.waste;

import com.hbm.item.nuclear.waste.IsotopicWasteContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;

/** Fixed registered items distinguish real isotope composition; the actual existing SILEX category supplies processing. */
@JeiPlugin
public final class IsotopicWasteJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:isotopic_waste"); }

    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) {
        event.addExtraIngredients(VanillaTypes.ITEM_STACK, IsotopicWasteContent.allStacks());
    }

}
