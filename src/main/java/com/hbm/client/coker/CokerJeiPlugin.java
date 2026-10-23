package com.hbm.client.coker;
import com.hbm.machine.coker.*;
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
public final class CokerJeiPlugin implements IModPlugin {
    static final RecipeType<CokerRecipe> TYPE=RecipeType.create("hbm","coker",CokerRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","coker_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,CokerRecipeConfig.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,CokerContent.ITEM.get());}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(CokerScreen.class,55,15,36,18,TYPE);}
    private static final class Category implements IRecipeCategory<CokerRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(CokerContent.ITEM.get()));}@Override public RecipeType<CokerRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineCoker");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,CokerRecipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(CokerViews.input(recipe));var out=CokerViews.outputs(recipe);for(int i=0;i<out.size();i++)if(!out.get(i).isEmpty())builder.addSlot(RecipeIngredientRole.OUTPUT,CokerViews.OUTPUT[i][0],CokerViews.OUTPUT[i][1]).addItemStack(out.get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(CokerContent.ITEM.get()));}
        @Override public void draw(CokerRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){CokerViews.draw(graphics,0,0);}
    }
}
