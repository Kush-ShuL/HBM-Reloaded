package com.hbm.client.shredder;

import com.hbm.machine.shredder.ShredderContent;
import com.hbm.machine.shredder.ShredderMenu;
import com.hbm.machine.shredder.ShredderRecipe;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class ShredderJeiPlugin implements IModPlugin {
    public static final RecipeType<ShredderRecipe> TYPE = RecipeType.create("hbm", "shredder", ShredderRecipe.class);
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("hbm", "textures/gui/nei/gui_nei_shredder.png");
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm", "shredder_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration event) { event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration event) { var level = Minecraft.getInstance().level; if (level != null) event.addRecipes(TYPE, level.getRecipeManager().getAllRecipesFor(ShredderContent.RECIPE_TYPE.get()).stream().map(holder -> holder.value()).toList()); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event) { event.addRecipeCatalysts(TYPE, ShredderContent.ITEM.get()); }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event) { event.addRecipeClickArea(ShredderScreen.class, 60, 78, 34, 18, TYPE); }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration event) { event.addRecipeTransferHandler(new IRecipeTransferInfo<ShredderMenu, ShredderRecipe>() {
        @Override public Class<ShredderMenu> getContainerClass() { return ShredderMenu.class; }
        @Override public Optional<MenuType<ShredderMenu>> getMenuType() { return Optional.of(ShredderContent.MENU.get()); }
        @Override public RecipeType<ShredderRecipe> getRecipeType() { return TYPE; }
        @Override public boolean canHandle(ShredderMenu menu, ShredderRecipe recipe) { return true; }
        @Override public List<Slot> getRecipeSlots(ShredderMenu menu, ShredderRecipe recipe) { return List.of(menu.getSlot(0)); }
        @Override public List<Slot> getInventorySlots(ShredderMenu menu, ShredderRecipe recipe) { return menu.slots.subList(30, 66); }
    }); }
    private static final class Category implements IRecipeCategory<ShredderRecipe> {
        private final IDrawable background, icon, power, arrow;
        Category(IGuiHelper gui) {
            background = gui.createDrawable(TEXTURE, 5, 11, 166, 65); icon = gui.createDrawableItemStack(new ItemStack(ShredderContent.ITEM.get()));
            power = gui.createAnimatedDrawable(gui.createDrawable(TEXTURE, 36, 86, 16, 52), 480, IDrawableAnimated.StartDirection.BOTTOM, false);
            arrow = gui.createAnimatedDrawable(gui.createDrawable(TEXTURE, 100, 118, 24, 16), 48, IDrawableAnimated.StartDirection.LEFT, false);
        }
        @Override public RecipeType<ShredderRecipe> getRecipeType() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.category.shredder"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder layout, ShredderRecipe recipe, IFocusGroup focus) {
            layout.addSlot(RecipeIngredientRole.INPUT, 39, 24).addItemStacks(com.hbm.ore.OreRecipeInputs.examples(recipe.input(),recipe.metadata())).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack))));
            layout.addSlot(RecipeIngredientRole.OUTPUT, 129, 24).addItemStack(recipe.result());
            layout.addSlot(RecipeIngredientRole.CATALYST, 84, 6).addItemStacks(ShredderContent.blades());
            layout.addSlot(RecipeIngredientRole.CATALYST, 84, 42).addItemStacks(ShredderContent.blades());
        }
        @Override public void draw(ShredderRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double x, double y) { power.draw(graphics, 3, 6); arrow.draw(graphics, 80, 23); }
    }
}
