package com.hbm.client.redroom;
import com.hbm.item.redroom.RedroomLootContent;
import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class RedroomLootJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:redroom_loot_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,RedroomLootContent.allStacks());}
}
