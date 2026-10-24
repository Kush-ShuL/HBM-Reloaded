package com.hbm.client.crystallizer;
import com.hbm.machine.crystallizer.*;
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
public final class CrystallizerJeiPlugin implements IModPlugin {
    static final RecipeType<CrystallizerRecipe> TYPE=RecipeType.create("hbm","crystallizer",CrystallizerRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","crystallizer_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,CrystallizerRecipeConfig.recipes(level.getRecipeManager()).stream().filter(CrystallizerViews::visible).toList());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,CrystallizerContent.ITEM.get());}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(CrystallizerScreen.class,75,36,27,12,TYPE);}
    private static final class Category implements IRecipeCategory<CrystallizerRecipe>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(CrystallizerContent.ITEM.get()));}@Override public RecipeType<CrystallizerRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.crystallizer");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,CrystallizerRecipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(CrystallizerViews.fluid(recipe));builder.addSlot(RecipeIngredientRole.INPUT,30,24).addItemStacks(CrystallizerViews.items(recipe));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(CrystallizerViews.output(recipe));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(CrystallizerContent.ITEM.get()));}
        @Override public void draw(CrystallizerRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){CrystallizerViews.draw(recipe,graphics,0,0);}
    }
}
