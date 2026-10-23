package com.hbm.client.bobmazondeps;
import com.hbm.item.bobmazondeps.BobmazonDependenciesContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class BobmazonDependenciesJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:bobmazon_dependencies73");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,BobmazonDependenciesContent.visibleTrinkets());}
}
