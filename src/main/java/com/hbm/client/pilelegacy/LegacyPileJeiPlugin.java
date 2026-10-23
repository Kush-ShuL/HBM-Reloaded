package com.hbm.client.pilelegacy;

import com.hbm.machine.pilelegacy.LegacyPileContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;

/** Source hidden-but-registered identities remain searchable; no nonexistent legacy CP1 manufacturing recipe is invented. */
@JeiPlugin
public final class LegacyPileJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:pile_legacy");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,LegacyPileContent.allStacks());}
}
