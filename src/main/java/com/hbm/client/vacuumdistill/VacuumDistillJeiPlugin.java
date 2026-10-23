package com.hbm.client.vacuumdistill;
import com.hbm.machine.vacuumdistill.*;
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
public final class VacuumDistillJeiPlugin implements IModPlugin {
    static final RecipeType<VacuumDistillRecipe> TYPE=RecipeType.create("hbm","vacuumdistill",VacuumDistillRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","vacuumdistill_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,VacuumDistillRecipeConfig.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,VacuumDistillContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<VacuumDistillRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(VacuumDistillContent.ITEM.get()));}@Override public RecipeType<VacuumDistillRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.vacuumDistill");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,VacuumDistillRecipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(VacuumDistillViews.input(recipe));var out=VacuumDistillViews.outputs(recipe);for(int i=0;i<out.size();i++)if(!out.get(i).isEmpty())builder.addSlot(RecipeIngredientRole.OUTPUT,VacuumDistillViews.OUTPUT[i][0],VacuumDistillViews.OUTPUT[i][1]).addItemStack(out.get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(VacuumDistillContent.ITEM.get()));}
        @Override public void draw(VacuumDistillRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){VacuumDistillViews.draw(graphics,0,0);}
    }
}
