package com.hbm.client.sourcesnacks;

import com.hbm.item.sourcesnacks.SourceSnacksContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class SourceSnacksJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:source_snacks_jei"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) {
        event.addExtraIngredients(VanillaTypes.ITEM_STACK, SourceSnacksContent.indexedItems());
    }
}
