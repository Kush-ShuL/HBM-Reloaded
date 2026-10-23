package com.hbm.client.strandcaster;
import com.hbm.machine.strandcaster.StrandCasterContent;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class StrandCasterJeiPlugin implements IModPlugin {
 static final RecipeType<StrandCasterViews.Casting> TYPE=RecipeType.create("hbm","strand_casting",StrandCasterViews.Casting.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:strand_casting_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,StrandCasterViews.recipes());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,StrandCasterContent.ITEM.get());}
 private static final class Category implements IRecipeCategory<StrandCasterViews.Casting>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(StrandCasterContent.ITEM.get()));}@Override public RecipeType<StrandCasterViews.Casting> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineStrandCaster");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,StrandCasterViews.Casting recipe,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,28,20).addItemStack(recipe.molten());builder.addSlot(RecipeIngredientRole.CATALYST,58,20).addItemStack(recipe.mold());builder.addSlot(RecipeIngredientRole.OUTPUT,108,20).addItemStack(recipe.output());}@Override public void draw(StrandCasterViews.Casting recipe,IRecipeSlotsView slots,GuiGraphics g,double x,double y){StrandCasterViews.draw(recipe,g,0,0);}}
}
