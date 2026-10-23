package com.hbm.client.reactor.iter.breeder;
import com.hbm.machine.reactor.iter.breeder.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class FluidBreedingJeiPlugin implements IModPlugin {
 private static final RecipeType<FluidBreedingRecipe> TYPE=RecipeType.create("hbm","fluid_breeding",FluidBreedingRecipe.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:fluid_breeding_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,FluidBreedingRecipes.recipes(level.getRecipeManager()));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,FusionBreederContent.ITEM.get());}@Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(FusionBreederScreen.class,67,46,42,14,TYPE);}
 private static final class Category implements IRecipeCategory<FluidBreedingRecipe>{private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(FusionBreederContent.ITEM.get()));}@Override public RecipeType<FluidBreedingRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.fusionBreeder");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,FluidBreedingRecipe recipe,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(FluidBreedingViews.input(recipe));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(FluidBreedingViews.output(recipe));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(FusionBreederContent.ITEM.get()));}@Override public void draw(FluidBreedingRecipe recipe,IRecipeSlotsView slots,GuiGraphics g,double x,double y){FluidBreedingViews.draw(g,0,0);}}
}
