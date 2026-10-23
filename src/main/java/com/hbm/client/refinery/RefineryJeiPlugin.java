package com.hbm.client.refinery;
import com.hbm.machine.refinery.*;
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
public final class RefineryJeiPlugin implements IModPlugin {
    static final RecipeType<RefineryRecipe> TYPE=RecipeType.create("hbm","refinery",RefineryRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","refinery_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,RefineryRecipeConfig.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,RefineryContent.ITEM.get());}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(RefineryScreen.class,34,34,14,34,TYPE);}
    private static final class Category implements IRecipeCategory<RefineryRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(RefineryContent.ITEM.get()));}@Override public RecipeType<RefineryRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineRefinery");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,RefineryRecipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(RefineryViews.input(recipe));var out=RefineryViews.outputs(recipe);for(int i=0;i<out.size();i++)if(!out.get(i).isEmpty())builder.addSlot(RecipeIngredientRole.OUTPUT,RefineryViews.OUTPUT[i][0],RefineryViews.OUTPUT[i][1]).addItemStack(out.get(i));}
        @Override public void draw(RefineryRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){RefineryViews.draw(graphics,0,0);}
    }
}
