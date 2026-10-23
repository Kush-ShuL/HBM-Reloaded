package com.hbm.client.ammopress;
import com.hbm.machine.ammopress.*;
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
public final class AmmoPressJeiPlugin implements IModPlugin {
 private static final RecipeType<AmmoPressRecipe> TYPE=RecipeType.create("hbm","ammo_press",AmmoPressRecipe.class);
 @Override public ResourceLocation getPluginUid(){return AmmoPressContent.id("ammo_press_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,AmmoPressRecipes.recipes(level.getRecipeManager()));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,AmmoPressContent.ITEM.get());}
 private static final class Category implements IRecipeCategory<AmmoPressRecipe>{private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(144,64);icon=helper.createDrawableItemStack(new ItemStack(AmmoPressContent.ITEM.get()));}@Override public RecipeType<AmmoPressRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_ammo_press");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,AmmoPressRecipe recipe,IFocusGroup focus){for(int i=0;i<9;i++){var input=recipe.cell(i);if(input!=null)builder.addSlot(RecipeIngredientRole.INPUT,8+i%3*18,4+i/3*18).addItemStacks(input.examples());}builder.addSlot(RecipeIngredientRole.OUTPUT,116,22).addItemStack(recipe.output());}@Override public void draw(AmmoPressRecipe recipe,IRecipeSlotsView slots,GuiGraphics g,double x,double y){AmmoPressViews.draw(g,0,0);}}
}
