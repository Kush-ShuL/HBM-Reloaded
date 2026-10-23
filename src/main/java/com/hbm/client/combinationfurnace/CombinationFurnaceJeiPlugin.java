package com.hbm.client.combinationfurnace;
import com.hbm.machine.combinationfurnace.*;
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
public final class CombinationFurnaceJeiPlugin implements IModPlugin {
    static final RecipeType<CombinationFurnaceRecipe> TYPE=RecipeType.create("hbm","combination",CombinationFurnaceRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","combinationfurnace_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,CombinationFurnaceRecipes.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,CombinationFurnaceContent.ITEM.get());}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(CombinationFurnaceScreen.class,55,55,18,18,TYPE);}
    private static final class Category implements IRecipeCategory<CombinationFurnaceRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(CombinationFurnaceContent.ITEM.get()));}@Override public RecipeType<CombinationFurnaceRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.furnaceCombination");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,CombinationFurnaceRecipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(CombinationFurnaceViews.inputs(recipe));var out=CombinationFurnaceViews.outputs(recipe);for(int i=0;i<out.size();i++)if(!out.get(i).isEmpty())builder.addSlot(RecipeIngredientRole.OUTPUT,CombinationFurnaceViews.OUTPUT[i][0],CombinationFurnaceViews.OUTPUT[i][1]).addItemStack(out.get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(CombinationFurnaceContent.ITEM.get()));}
        @Override public void draw(CombinationFurnaceRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){CombinationFurnaceViews.draw(recipe,graphics,0,0);}
    }
}
