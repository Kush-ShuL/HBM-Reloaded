package com.hbm.client.siren;
import com.hbm.machine.siren.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
/** Real20 visible fixed tracks; manufacture uses existing native crafting/anvil categories. */
@JeiPlugin public final class SirenJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:siren106");}
    @Override public void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime runtime){runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,java.util.List.of(SirenContent.cassette(0)));}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,SirenContent.cassetteStacks());}
}
