package com.hbm.client.guidebook;

import com.hbm.item.guidebook.GuideBookContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class GuideBookJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:guidebook_jei"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) {
        event.addExtraIngredients(VanillaTypes.ITEM_STACK, GuideBookContent.visibleStacks());
    }
    @Override public void onRuntimeAvailable(IJeiRuntime runtime) {
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, GuideBookContent.hiddenStacks());
    }
}
