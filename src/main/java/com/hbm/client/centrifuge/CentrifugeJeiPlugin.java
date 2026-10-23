package com.hbm.client.centrifuge;

import com.hbm.client.gui.CentrifugeScreen;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.machine.centrifuge.*;
import java.util.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class CentrifugeJeiPlugin implements IModPlugin {
    public static final RecipeType<CentrifugeRecipe> TYPE=RecipeType.create("hbm","centrifuge",CentrifugeRecipe.class);
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm","centrifuge_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) { registration.addRecipeCategories(new Category(registration.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration registration) { var level=Minecraft.getInstance().level; if(level!=null) registration.addRecipes(TYPE,level.getRecipeManager().getAllRecipesFor(CentrifugeContent.TYPE.get()).stream().map(holder->holder.value()).toList()); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) { registration.addRecipeCatalysts(TYPE,CentrifugeContent.ITEM.get()); }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) { registration.addRecipeClickArea(CentrifugeScreen.class,63,7,80,38,TYPE); }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) { registration.addRecipeTransferHandler(new IRecipeTransferInfo<CentrifugeMenu,CentrifugeRecipe>() {
        @Override public Class<CentrifugeMenu> getContainerClass() { return CentrifugeMenu.class; }
        @Override public Optional<MenuType<CentrifugeMenu>> getMenuType() { return Optional.of(CentrifugeContent.MENU.get()); }
        @Override public RecipeType<CentrifugeRecipe> getRecipeType() { return TYPE; }
        @Override public boolean canHandle(CentrifugeMenu menu,CentrifugeRecipe recipe) { return true; }
        @Override public List<Slot> getRecipeSlots(CentrifugeMenu menu,CentrifugeRecipe recipe) { return List.of(menu.getSlot(0)); }
        @Override public List<Slot> getInventorySlots(CentrifugeMenu menu,CentrifugeRecipe recipe) { return menu.slots.subList(8,44); }
    }); }
    private static final class Category implements IRecipeCategory<CentrifugeRecipe> {
        private final IDrawable background,icon;
        Category(IGuiHelper helper) { background=helper.createBlankDrawable(166,65); icon=helper.createDrawableItemStack(new ItemStack(CentrifugeContent.ITEM.get())); }
        @Override public RecipeType<CentrifugeRecipe> getRecipeType() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("container.centrifuge"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder,CentrifugeRecipe recipe,IFocusGroup focuses) {
            builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(com.hbm.ore.OreRecipeInputs.examples(recipe.input())).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack))));
            var drawing=CentrifugeViews.drawing(recipe); int[][] points=LegacyRecipeDisplays.outputCoordinates(CentrifugeViews.CATEGORY,drawing);
            for(int i=0;i<recipe.outputs().size();i++) if(!recipe.outputs().get(i).isEmpty()) builder.addSlot(RecipeIngredientRole.OUTPUT,points[i][0],points[i][1]).addItemStack(recipe.outputs().get(i));
            builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(CentrifugeContent.ITEM.get()));
        }
        @Override public void draw(CentrifugeRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y) { LegacyDisplayDrawing.background(graphics,CentrifugeViews.CATEGORY,CentrifugeViews.drawing(recipe),0,0); }
    }
}
