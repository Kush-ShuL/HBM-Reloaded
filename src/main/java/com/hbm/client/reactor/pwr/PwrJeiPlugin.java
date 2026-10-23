package com.hbm.client.reactor.pwr;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.reactor.pwr.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class PwrJeiPlugin implements IModPlugin {
    static final RecipeType<PwrFuelCycles> TYPE=RecipeType.create("hbm","pwr_fission",PwrFuelCycles.class);
    @Override public ResourceLocation getPluginUid(){return PwrContent.id("pwr_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,PwrFuelCycles.recipes());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,PwrContent.CONTROLLER_ITEM.get());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(PwrContent.FUEL.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return PwrFuelType.from(PwrFuelItem.metadata(stack));}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return PwrFuelType.from(PwrFuelItem.metadata(stack)).serializedName();}});}
    private static final class Category implements IRecipeCategory<PwrFuelCycles>{
        private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(PwrContent.CONTROLLER_ITEM.get()));}
        @Override public RecipeType<PwrFuelCycles> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.pwrController");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,PwrFuelCycles recipe,IFocusGroup focus){var input=LegacyRecipeDisplays.inputCoordinates(PwrFuelCycles.CATEGORY,1)[0];var output=LegacyRecipeDisplays.outputCoordinates(PwrFuelCycles.CATEGORY,recipe.display())[0];builder.addSlot(RecipeIngredientRole.INPUT,input[0],input[1]).addItemStack(recipe.input());builder.addSlot(RecipeIngredientRole.OUTPUT,output[0],output[1]).addItemStack(recipe.output());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(PwrContent.CONTROLLER_ITEM.get()));}
        @Override public void draw(PwrFuelCycles recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){recipe.draw(graphics,0,0);}
    }
}
