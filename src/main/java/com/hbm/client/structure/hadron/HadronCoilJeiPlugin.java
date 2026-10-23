package com.hbm.client.structure.hadron;
import com.hbm.world.structure.dependencies.hadron.HadronCoilContent;
import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class HadronCoilJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:hadron_coils_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,HadronCoilContent.allStacks());}
}
