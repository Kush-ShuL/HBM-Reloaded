package com.hbm.client.structure.woodfixtures46;

import com.hbm.block.woodfixtures46.WoodFixturesContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;

/** Three fixed source forms stay distinct in the native crafting category; no new recipe system. */
@JeiPlugin public final class WoodFixturesJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:wood_fixtures46"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) { event.addExtraIngredients(VanillaTypes.ITEM_STACK, WoodFixturesContent.creativeStacks("block")); }
}
