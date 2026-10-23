package com.hbm.client.alloy;
import com.hbm.client.gui.AlloyFurnaceScreen;
import com.hbm.machine.alloy.*;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class AlloyJeiPlugin implements IModPlugin {
    public static final RecipeType<AlloyRecipe> TYPE=RecipeType.create("hbm","alloy",AlloyRecipe.class);
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm","alloy_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration event) { event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration event) { event.addRecipes(TYPE,AlloyViews.recipes()); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event) { event.addRecipeCatalysts(TYPE,AlloyContent.ITEM.get()); }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event) { event.addRecipeClickArea(AlloyFurnaceScreen.class,101,36,24,18,TYPE); }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration event) { event.addRecipeTransferHandler(new IRecipeTransferInfo<AlloyMenu,AlloyRecipe>() {
        @Override public Class<AlloyMenu> getContainerClass() { return AlloyMenu.class; }
        @Override public Optional<MenuType<AlloyMenu>> getMenuType() { return Optional.of(AlloyContent.MENU.get()); }
        @Override public RecipeType<AlloyRecipe> getRecipeType() { return TYPE; }
        @Override public boolean canHandle(AlloyMenu menu,AlloyRecipe recipe) { return true; }
        @Override public List<Slot> getRecipeSlots(AlloyMenu menu,AlloyRecipe recipe) { return List.of(menu.getSlot(0),menu.getSlot(1)); }
        @Override public List<Slot> getInventorySlots(AlloyMenu menu,AlloyRecipe recipe) { return menu.slots.subList(4,40); }
    }); }
    @Override public void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime runtime) { runtime.getIngredientManager().removeIngredientsAtRuntime(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,List.of(new ItemStack(AlloyContent.ON_ITEM.get()))); }
    private static final class Category implements IRecipeCategory<AlloyRecipe> {
        private final IDrawable background,icon;
        Category(IGuiHelper helper) { background=helper.createBlankDrawable(166,65); icon=helper.createDrawableItemStack(new ItemStack(AlloyContent.ITEM.get())); }
        @Override public RecipeType<AlloyRecipe> getRecipeType() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.category.alloy"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder,AlloyRecipe recipe,IFocusGroup focuses) {
            builder.addSlot(RecipeIngredientRole.INPUT,75,7).addItemStacks(com.hbm.ore.OreRecipeInputs.examples(recipe.first())).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack)))); builder.addSlot(RecipeIngredientRole.INPUT,75,43).addItemStacks(com.hbm.ore.OreRecipeInputs.examples(recipe.second())).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack))));
            builder.addSlot(RecipeIngredientRole.OUTPUT,129,25).addItemStack(recipe.result()); builder.addSlot(RecipeIngredientRole.CATALYST,3,25).addItemStacks(AlloyViews.fuels());
        }
        @Override public void draw(AlloyRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y) { AlloyViews.draw(graphics,0,0); }
    }
}
