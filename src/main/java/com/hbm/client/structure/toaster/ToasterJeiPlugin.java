package com.hbm.client.structure.toaster;

import com.hbm.block.toaster.ToasterContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin public final class ToasterJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:deco_toaster"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) { event.addExtraIngredients(VanillaTypes.ITEM_STACK, ToasterContent.creativeStacks("block")); }
}
