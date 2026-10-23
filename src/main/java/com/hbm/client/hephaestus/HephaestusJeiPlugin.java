package com.hbm.client.hephaestus;
import com.hbm.machine.hephaestus.HephaestusContent;
import java.util.List;
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
@JeiPlugin public final class HephaestusJeiPlugin implements IModPlugin {
 private static final RecipeType<HephaestusViews.View> TYPE=RecipeType.create("hbm","hephaestus",HephaestusViews.View.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:hephaestus_jei");}@Override public void registerCategories(IRecipeCategoryRegistration e){e.addRecipeCategories(new Category(e.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration e){e.addRecipes(TYPE,HephaestusViews.views());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration e){e.addRecipeCatalysts(TYPE,HephaestusContent.ITEM.get());}
 private static final class Category implements IRecipeCategory<HephaestusViews.View>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,80);icon=helper.createDrawableItemStack(new ItemStack(HephaestusContent.ITEM.get()));}@Override public RecipeType<HephaestusViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_hephaestus");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder b,HephaestusViews.View r,IFocusGroup f){b.addSlot(RecipeIngredientRole.INPUT,30,24).addItemStack(r.input());b.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(r.output());}@Override public void draw(HephaestusViews.View r,IRecipeSlotsView slots,GuiGraphics g,double x,double y){HephaestusViews.draw(g,r,0,0);}}
}
