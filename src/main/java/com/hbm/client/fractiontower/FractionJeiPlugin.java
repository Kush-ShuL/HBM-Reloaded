package com.hbm.client.fractiontower;
import com.hbm.machine.fractiontower.*;
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
public final class FractionJeiPlugin implements IModPlugin {
    static final RecipeType<FractionRecipe> TYPE=RecipeType.create("hbm","fractiontower",FractionRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","fractiontower_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,FractionRecipeConfig.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,FractionContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<FractionRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(FractionContent.ITEM.get()));}@Override public RecipeType<FractionRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_fraction_tower");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,FractionRecipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(FractionViews.input(recipe));var out=FractionViews.outputs(recipe);for(int i=0;i<out.size();i++)if(!out.get(i).isEmpty())builder.addSlot(RecipeIngredientRole.OUTPUT,FractionViews.OUTPUT[i][0],FractionViews.OUTPUT[i][1]).addItemStack(out.get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(FractionContent.ITEM.get()));}
        @Override public void draw(FractionRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){FractionViews.draw(graphics,0,0);}
    }
}
