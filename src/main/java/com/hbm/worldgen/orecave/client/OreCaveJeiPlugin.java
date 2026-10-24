package com.hbm.worldgen.orecave.client;
import com.hbm.worldgen.orecave.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class OreCaveJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:ore_caves_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,OreCaves.indexedItems());}
}
