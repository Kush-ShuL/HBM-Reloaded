package com.hbm.client.reactor.rbmk.irradiation;
import com.hbm.machine.reactor.rbmk.irradiation.*;
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
public final class RbmkIrradiationJeiPlugin implements IModPlugin {
    static final RecipeType<RbmkIrradiationRecipe> TYPE=RecipeType.create("hbm","irradiation",RbmkIrradiationRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:irradiation_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,RbmkIrradiationRecipes.recipes(level.getRecipeManager()));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,RbmkOutgasserContent.ITEM.get());var fusion=RbmkIrradiationViews.fusion();if(!fusion.isEmpty())event.addRecipeCatalysts(TYPE,fusion);}@Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(RbmkOutgasserScreen.class,75,26,16,32,TYPE);}
    private static final class Category implements IRecipeCategory<RbmkIrradiationRecipe>{private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(RbmkOutgasserContent.ITEM.get()));}@Override public RecipeType<RbmkIrradiationRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.rbmk_outgasser");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,RbmkIrradiationRecipe recipe,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(recipe.input().examples());var outputs=RbmkIrradiationViews.outputs(recipe);for(int i=0;i<outputs.size();i++)builder.addSlot(RecipeIngredientRole.OUTPUT,102+i*18,24).addItemStack(outputs.get(i));var machines=RbmkIrradiationViews.machines(recipe);if(!machines.isEmpty())builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStacks(machines);}@Override public void draw(RbmkIrradiationRecipe recipe,IRecipeSlotsView slots,GuiGraphics g,double x,double y){RbmkIrradiationViews.draw(recipe,g,0,0);}}
}
