package com.hbm.client.capacitors;
import com.hbm.machine.capacitors.CapacitorContent;
import mezz.jei.api.*;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class CapacitorJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:capacitor_items");}@Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,CapacitorContent.allStacks());}
}
