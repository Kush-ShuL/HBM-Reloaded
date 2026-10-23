package com.hbm.client.creative;

import com.hbm.registry.HbmCreativeTabs;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

/** Render carriers never become gameplay ingredients in search. */
@JeiPlugin
public final class CreativeIconJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm","creative_icon_jei"); }
    @Override public void onRuntimeAvailable(IJeiRuntime runtime) { runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,HbmCreativeTabs.ORDER.stream().map(HbmCreativeTabs::uiIcon).toList()); }
}
