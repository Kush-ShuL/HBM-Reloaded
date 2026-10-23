package com.hbm.client.electricpress;

import com.hbm.client.press.PressJeiPlugin;
import com.hbm.machine.electricpress.*;
import com.hbm.machine.press.PressRecipe;
import java.util.*;
import mezz.jei.api.*;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.*;

/** Source NEI has one press table and three actual catalysts; new machines never duplicate its recipe category. */
@JeiPlugin public final class ElectricPressJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:electric_press_jei");}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(PressJeiPlugin.TYPE,ElectricPressContent.ELECTRIC_ITEM.get(),ElectricPressContent.CONVEYOR_ITEM.get());}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(ElectricPressScreen.class,37,23,24,18,PressJeiPlugin.TYPE);}
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration event){event.addRecipeTransferHandler(new IRecipeTransferInfo<ElectricPressMenu,PressRecipe>(){
        @Override public Class<ElectricPressMenu> getContainerClass(){return ElectricPressMenu.class;}
        @Override public Optional<MenuType<ElectricPressMenu>> getMenuType(){return Optional.of(ElectricPressContent.MENU.get());}
        @Override public RecipeType<PressRecipe> getRecipeType(){return PressJeiPlugin.TYPE;}
        @Override public boolean canHandle(ElectricPressMenu menu,PressRecipe recipe){return true;}
        @Override public List<Slot> getRecipeSlots(ElectricPressMenu menu,PressRecipe recipe){return List.of(menu.getSlot(2),menu.getSlot(1));}
        @Override public List<Slot> getInventorySlots(ElectricPressMenu menu,PressRecipe recipe){return menu.slots.subList(5,41);}
    });}
}
