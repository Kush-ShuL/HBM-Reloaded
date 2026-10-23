package com.hbm.client.cyclotron;
import com.hbm.machine.cyclotron.*;
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
@JeiPlugin public final class CyclotronJeiPlugin implements IModPlugin {
 static final RecipeType<CyclotronRecipe> TYPE=RecipeType.create("hbm","cyclotron",CyclotronRecipe.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:cyclotron_jei");}@Override public void registerCategories(IRecipeCategoryRegistration e){e.addRecipeCategories(new Category(e.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration e){var level=Minecraft.getInstance().level;if(level!=null)e.addRecipes(TYPE,CyclotronRecipes.visibleRecipes(level.getRecipeManager()));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration e){e.addRecipeCatalysts(TYPE,CyclotronContent.ITEM.get());}
 @Override public void registerGuiHandlers(IGuiHandlerRegistration e){e.addRecipeClickArea(CyclotronScreen.class,43,16,34,34,TYPE);}@Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration e){e.addRecipeTransferHandler(CyclotronMenu.class,CyclotronContent.MENU.get(),TYPE,0,6,12,36);}
 private static final class Category implements IRecipeCategory<CyclotronRecipe>{private final IDrawable background,icon;Category(IGuiHelper h){background=h.createBlankDrawable(166,65);icon=h.createDrawableItemStack(new ItemStack(CyclotronContent.ITEM.get()));}@Override public RecipeType<CyclotronRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.cyclotron");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder b,CyclotronRecipe r,IFocusGroup f){b.addSlot(RecipeIngredientRole.INPUT,21,24).addIngredients(r.inputItems().get(0).ingredient());b.addSlot(RecipeIngredientRole.INPUT,75,24).addIngredients(r.inputItems().get(1).ingredient());b.addSlot(RecipeIngredientRole.OUTPUT,129,24).addItemStack(r.output());}@Override public void draw(CyclotronRecipe r,IRecipeSlotsView slots,GuiGraphics g,double x,double y){CyclotronViews.draw(g,0,0);}}
}
