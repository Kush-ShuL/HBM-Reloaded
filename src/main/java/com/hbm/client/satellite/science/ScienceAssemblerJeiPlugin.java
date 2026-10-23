package com.hbm.client.satellite.science;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.satellite.science.*;
import java.util.List;
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

@JeiPlugin
public final class ScienceAssemblerJeiPlugin implements IModPlugin {
    static final RecipeType<SpaceAssemblerRecipe> TYPE=RecipeType.create("hbm","space_assembler",SpaceAssemblerRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:science_assembler_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,SpaceAssemblerRecipes.recipes(level.getRecipeManager()).stream().filter(ScienceAssemblerViews::visible).toList());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalyst(ScienceAssemblerViews.machine(),TYPE);}
    private static final class Category implements IRecipeCategory<SpaceAssemblerRecipe> {
        final IDrawable background,icon;
        Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(ScienceAssemblerViews.machine());}
        @Override public RecipeType<SpaceAssemblerRecipe> getRecipeType(){return TYPE;}
        @Override public Component getTitle(){return Component.translatable("item.satellite.science_assembler.name");}
        @Override public IDrawable getBackground(){return background;}
        @Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,SpaceAssemblerRecipe recipe,IFocusGroup focuses){
            var category=ScienceAssemblerViews.category();var display=ScienceAssemblerViews.drawing(recipe);var inputs=ScienceAssemblerViews.inputs(recipe);var outputs=ScienceAssemblerViews.outputs(recipe);
            var ip=LegacyRecipeDisplays.inputCoordinates(category,inputs.size());var op=LegacyRecipeDisplays.outputCoordinates(category,display);
            for(int i=0;i<inputs.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(inputs.get(i));
            for(int i=0;i<outputs.size();i++){int lane=i;builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(outputs.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(ScienceAssemblerViews.outputTooltip(recipe,lane,stack))));}
            if(recipe.isPooled())builder.addSlot(RecipeIngredientRole.CATALYST,75,10).addItemStacks(ScienceAssemblerViews.blueprints(recipe));
            builder.addSlot(RecipeIngredientRole.CATALYST,75,recipe.isPooled()?38:31).addItemStacks(List.of(ScienceAssemblerViews.machine()));
        }
        @Override public void draw(SpaceAssemblerRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){ScienceAssemblerViews.draw(graphics,recipe,0,0);}
    }
}
