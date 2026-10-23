package com.hbm.client.woodburner;
import com.hbm.client.gui.WoodBurnerScreen;
import com.hbm.machine.woodburner.WoodBurnerContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
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
@JeiPlugin
public final class WoodBurnerJeiPlugin implements IModPlugin {
    static final RecipeType<WoodBurnerFuelViews.Fuel> TYPE=RecipeType.create("hbm","wood_burner_fuel",WoodBurnerFuelViews.Fuel.class);
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm","wood_burner_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration event) { event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration event) { event.addRecipes(TYPE,WoodBurnerFuelViews.recipes(event.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK))); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event) { event.addRecipeCatalysts(TYPE,WoodBurnerContent.ITEM.get()); }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event) { event.addRecipeClickArea(WoodBurnerScreen.class,16,17,8,54,TYPE); }
    private static final class Category implements IRecipeCategory<WoodBurnerFuelViews.Fuel> {
        final IDrawable background,icon;
        Category(IGuiHelper helper) { background=helper.createBlankDrawable(166,78); icon=helper.createDrawableItemStack(new ItemStack(WoodBurnerContent.ITEM.get())); }
        @Override public RecipeType<WoodBurnerFuelViews.Fuel> getRecipeType() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("container.machineWoodBurner"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder,WoodBurnerFuelViews.Fuel fuel,IFocusGroup focuses) { builder.addSlot(RecipeIngredientRole.INPUT,fuel.liquid()?75:21,7).addItemStack(fuel.input()); if(!fuel.ash().isEmpty()) builder.addSlot(RecipeIngredientRole.OUTPUT,21,43).addItemStack(fuel.ash()).addTooltipCallback((view,tooltip)->tooltip.addAll(fuel.ashInfo())); }
        @Override public void draw(WoodBurnerFuelViews.Fuel fuel,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y) { WoodBurnerFuelViews.draw(graphics,fuel,0,0); }
    }
}
