package com.hbm.client.flatammo;

import com.hbm.weapon.sedna.ammo.FlatAmmoContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;

/** Native item identities need no metadata subtype interpreter or replacement recipe category. */
@JeiPlugin
public final class FlatAmmoJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:flat_ammunition");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,FlatAmmoContent.indexedItems());}
}
