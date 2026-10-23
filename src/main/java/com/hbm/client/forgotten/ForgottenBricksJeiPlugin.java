package com.hbm.client.forgotten;

import com.hbm.block.forgotten.ForgottenBricksContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

/** Actual NEIConfig95/96 wildcard hides both source identities; never publish fake process recipes. */
@JeiPlugin
public final class ForgottenBricksJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:forgotten_bricks246");}
    @Override public void onRuntimeAvailable(IJeiRuntime runtime){
        var manager=runtime.getIngredientManager();
        var hidden=manager.getAllIngredients(VanillaTypes.ITEM_STACK).stream()
                .filter(stack->ForgottenBricksContent.viewerHidden(stack.getItem())).toList();
        if(!hidden.isEmpty())manager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,hidden);
    }
}
