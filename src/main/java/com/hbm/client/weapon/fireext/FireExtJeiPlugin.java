package com.hbm.client.weapon.fireext;
import com.hbm.weapon.sedna.gun.fireext.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class FireExtJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:fire_extinguisher_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,FireExtContent.indexedItems());}
}
