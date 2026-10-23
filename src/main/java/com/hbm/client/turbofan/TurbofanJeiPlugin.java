package com.hbm.client.turbofan;
import com.hbm.machine.turbofan.TurbofanContent;
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
@JeiPlugin public final class TurbofanJeiPlugin implements IModPlugin {
 private static final RecipeType<TurbofanFuelViews.View> TYPE=RecipeType.create("hbm","turbofan_fuels",TurbofanFuelViews.View.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:turbofan_jei");}@Override public void registerCategories(IRecipeCategoryRegistration e){e.addRecipeCategories(new Category(e.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration e){e.addRecipes(TYPE,TurbofanFuelViews.all());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration e){e.addRecipeCatalysts(TYPE,TurbofanContent.ITEM.get());}
 private static final class Category implements IRecipeCategory<TurbofanFuelViews.View>{final IDrawable background,icon;Category(IGuiHelper h){background=h.createBlankDrawable(166,65);icon=h.createDrawableItemStack(new ItemStack(TurbofanContent.ITEM.get()));}@Override public RecipeType<TurbofanFuelViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_turbofan");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder b,TurbofanFuelViews.View r,IFocusGroup f){b.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(r.fuel());b.addSlot(RecipeIngredientRole.CATALYST,102,24).addItemStack(new ItemStack(TurbofanContent.ITEM.get()));}@Override public void draw(TurbofanFuelViews.View r,IRecipeSlotsView slots,GuiGraphics g,double x,double y){TurbofanFuelViews.draw(g,0,0,r.energy());}}
}
