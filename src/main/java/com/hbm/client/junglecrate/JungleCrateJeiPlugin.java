package com.hbm.client.junglecrate;
import com.hbm.block.junglecrate.JungleCrateContent;
import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class JungleCrateJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:jungle_crate_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,JungleCrateContent.allStacks());}
}
