package com.hbm.client.holotape;
import com.hbm.item.holotape.*;import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.registration.*;import net.minecraft.resources.ResourceLocation;
@JeiPlugin
public final class HolotapeJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:holotape_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,HolotapeContent.imageStacks());}
}
