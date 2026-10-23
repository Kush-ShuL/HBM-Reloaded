package com.hbm.client.brickfurnace;

import com.hbm.machine.brickfurnace.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Same server SMELTING recipes and actual source fuel machine, no duplicate recipe table. */
@JeiPlugin public final class BrickFurnaceJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:brick_furnace");}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){registration.addRecipeCatalyst(new ItemStack(BrickFurnaceContent.OFF_ITEM.get()),RecipeTypes.SMELTING);}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration){registration.addRecipeClickArea(BrickFurnaceScreen.class,85,34,24,16,RecipeTypes.SMELTING);}
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){registration.addRecipeTransferHandler(BrickFurnaceMenu.class,BrickFurnaceContent.MENU.get(),RecipeTypes.SMELTING,0,1,4,36);}
}
