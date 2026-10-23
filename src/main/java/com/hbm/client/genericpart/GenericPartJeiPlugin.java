package com.hbm.client.genericpart;
import com.hbm.item.genericpart.GenericPartContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
/** Each real part has its own native Item identity; no metadata subtype interpreter. */
@JeiPlugin
public final class GenericPartJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:generic_parts_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,GenericPartContent.indexedItems());}
}
