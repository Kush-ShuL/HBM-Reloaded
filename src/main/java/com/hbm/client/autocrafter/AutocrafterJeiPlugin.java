package com.hbm.client.autocrafter;
import com.hbm.machine.autocrafter.AutocrafterContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Uses the one actual vanilla crafting category/recipe set; no duplicate HBM crafting engine or cloned recipes. */
@JeiPlugin public final class AutocrafterJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:autocrafter_jei");}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalyst(new ItemStack(AutocrafterContent.ITEM.get()),RecipeTypes.CRAFTING);}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(AutocrafterScreen.class,98,40,16,16,RecipeTypes.CRAFTING);}
}
