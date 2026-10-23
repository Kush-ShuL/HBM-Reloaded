package com.hbm.satellite.viewer;
import com.hbm.satellite.SatelliteContent;import com.hbm.satellite.item.SatelliteItem;import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.ingredients.subtypes.*;import mezz.jei.api.registration.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
/** Actual ready orbital metadata are distinct native JEI entries; source frequency is not an item subtype. */
@JeiPlugin public final class SatelliteJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:satellite_payload_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,SatelliteItem.creativeStacks());}
}
