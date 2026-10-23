package com.hbm.client.ironfurnace;
import com.hbm.machine.ironfurnace.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** The existing SMELTING table is the complete source furnace table. */
@JeiPlugin public final class IronFurnaceJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:iron_furnace");}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){registration.addRecipeCatalyst(new ItemStack(IronFurnaceContent.ITEM.get()),RecipeTypes.SMELTING);}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration){registration.addRecipeClickArea(IronFurnaceScreen.class,70,16,18,18,RecipeTypes.SMELTING);}
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){registration.addRecipeTransferHandler(IronFurnaceMenu.class,IronFurnaceContent.MENU.get(),RecipeTypes.SMELTING,0,1,5,36);}
}
