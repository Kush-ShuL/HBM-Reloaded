package com.hbm.client.steelfurnace;
import com.hbm.machine.steelfurnace.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** The existing SMELTING table is the complete source furnace table. */
@JeiPlugin public final class SteelFurnaceJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:steel_furnace");}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){registration.addRecipeCatalyst(new ItemStack(SteelFurnaceContent.ITEM.get()),RecipeTypes.SMELTING);}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration){registration.addRecipeClickArea(SteelFurnaceScreen.class,16,16,18,18,RecipeTypes.SMELTING);}
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){registration.addRecipeTransferHandler(SteelFurnaceMenu.class,SteelFurnaceContent.MENU.get(),RecipeTypes.SMELTING,0,3,6,36);}
}
