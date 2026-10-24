package com.hbm.client.entity.aircraft.bomber;
import com.hbm.entity.aircraft.bomber.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class BomberJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:source_airstrike");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,BomberContent.sourceVisible());}
 @Override public void onRuntimeAvailable(IJeiRuntime runtime){runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,BomberContent.sourceHidden());}
}
