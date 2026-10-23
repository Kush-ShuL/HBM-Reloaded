package com.hbm.machine.foundry.crucible.client;
import com.hbm.machine.foundry.crucible.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
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
public final class CrucibleJeiPlugin implements IModPlugin {
 static final RecipeType<CrucibleViews.View> SMELTING=RecipeType.create("hbm","crucible_smelting",CrucibleViews.View.class),ALLOYING=RecipeType.create("hbm","crucible_alloying",CrucibleViews.View.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","crucible_jei");}
 @Override public void registerCategories(IRecipeCategoryRegistration event){var helper=event.getJeiHelpers().getGuiHelper();event.addRecipeCategories(new Category(helper,false),new Category(helper,true));}
 @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(SMELTING,CrucibleViews.smelting(event.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK)));event.addRecipes(ALLOYING,CrucibleViews.alloying());}
 @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(SMELTING,CrucibleContent.CRUCIBLE.get());event.addRecipeCatalysts(ALLOYING,CrucibleContent.CRUCIBLE.get());}
 static final class Category implements IRecipeCategory<CrucibleViews.View>{
  final boolean alloy;final IDrawable background,icon;Category(IGuiHelper helper,boolean alloy){this.alloy=alloy;background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(CrucibleContent.CRUCIBLE.get()));}
  @Override public RecipeType<CrucibleViews.View> getRecipeType(){return alloy?ALLOYING:SMELTING;}@Override public Component getTitle(){return Component.translatable(alloy?"recipe.hbm.crucible.alloying":"recipe.hbm.crucible.smelting");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
  @Override public void setRecipe(IRecipeLayoutBuilder builder,CrucibleViews.View recipe,IFocusGroup focus){for(int i=0;i<recipe.inputs().size();i++)builder.addSlot(RecipeIngredientRole.INPUT,CrucibleViews.inputX(recipe,i),CrucibleViews.inputY(recipe,i)).addItemStacks(recipe.inputs().get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,42).addItemStack(new ItemStack(CrucibleContent.CRUCIBLE.get()));for(int i=0;i<recipe.outputs().size();i++)builder.addSlot(RecipeIngredientRole.OUTPUT,102+i%3*18,6+i/3*18).addItemStack(recipe.outputs().get(i));}
  @Override public void draw(CrucibleViews.View recipe,IRecipeSlotsView slots,GuiGraphics g,double x,double y){CrucibleViews.draw(g,0,0,alloy);}
 }
}
