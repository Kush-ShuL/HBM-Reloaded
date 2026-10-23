package com.hbm.client.poweredcondenser;
import com.hbm.machine.poweredcondenser.PoweredCondenserContent;
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
@JeiPlugin public final class PoweredCondenserJeiPlugin implements IModPlugin {
 private static final RecipeType<PoweredCondenserViews.View> TYPE=RecipeType.create("hbm","powered_condenser",PoweredCondenserViews.View.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:powered_condenser_jei");}@Override public void registerCategories(IRecipeCategoryRegistration e){e.addRecipeCategories(new Category(e.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration e){e.addRecipes(TYPE,List.of(PoweredCondenserViews.view()));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration e){e.addRecipeCatalysts(TYPE,PoweredCondenserContent.ITEM.get());}
 private static final class Category implements IRecipeCategory<PoweredCondenserViews.View>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(PoweredCondenserContent.ITEM.get()));}@Override public RecipeType<PoweredCondenserViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_condenser_powered");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder b,PoweredCondenserViews.View r,IFocusGroup f){b.addSlot(RecipeIngredientRole.INPUT,30,24).addItemStack(r.input());b.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(r.output());}@Override public void draw(PoweredCondenserViews.View r,IRecipeSlotsView slots,GuiGraphics g,double x,double y){PoweredCondenserViews.draw(g,0,0);}}
}
