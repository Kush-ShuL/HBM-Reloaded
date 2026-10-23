package com.hbm.machine.foundry.client;
import com.hbm.machine.foundry.*;
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
public final class FoundryJeiPlugin implements IModPlugin {
 static final RecipeType<FoundryCastingViews.Casting> TYPE=RecipeType.create("hbm","foundry_casting",FoundryCastingViews.Casting.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","foundry_jei");}
 @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
 @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,FoundryCastingViews.recipes());}
 @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,FoundryContent.block(FoundryContent.Kind.MOLD),FoundryContent.block(FoundryContent.Kind.BASIN));}
 static final class Category implements IRecipeCategory<FoundryCastingViews.Casting>{
  final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(FoundryContent.block(FoundryContent.Kind.MOLD)));}
  @Override public RecipeType<FoundryCastingViews.Casting> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("recipe.hbm.foundry.casting");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
  @Override public void setRecipe(IRecipeLayoutBuilder builder,FoundryCastingViews.Casting recipe,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(recipe.liquid());builder.addSlot(RecipeIngredientRole.CATALYST,75,6).addItemStack(recipe.mold());builder.addSlot(RecipeIngredientRole.CATALYST,75,42).addItemStack(recipe.basin());builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());}
  @Override public void draw(FoundryCastingViews.Casting recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){FoundryCastingViews.draw(graphics,0,0);}
 }
}
