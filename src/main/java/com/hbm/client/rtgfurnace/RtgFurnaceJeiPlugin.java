package com.hbm.client.rtgfurnace;

import com.hbm.machine.rtgfurnace.*;
import java.util.List;
import mezz.jei.api.*;
import mezz.jei.api.constants.*;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Source hides only on-state; existing native smelting recipes remain the only recipe table. */
@JeiPlugin public final class RtgFurnaceJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:rtg_furnace");}
    @Override public void onRuntimeAvailable(IJeiRuntime runtime){runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,List.of(new ItemStack(RtgFurnaceContent.ON_ITEM.get())));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){registration.addRecipeCatalyst(new ItemStack(RtgFurnaceContent.OFF_ITEM.get()),RecipeTypes.SMELTING);}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration){registration.addRecipeClickArea(RtgFurnaceScreen.class,79,34,24,17,RecipeTypes.SMELTING);}
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){registration.addRecipeTransferHandler(RtgFurnaceMenu.class,RtgFurnaceContent.MENU.get(),RecipeTypes.SMELTING,0,1,5,36);}
}
