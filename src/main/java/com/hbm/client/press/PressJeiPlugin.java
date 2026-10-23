package com.hbm.client.press;

import com.hbm.client.gui.PressScreen;
import com.hbm.item.StampItem;
import com.hbm.machine.press.PressContent;
import com.hbm.machine.press.PressMenu;
import com.hbm.machine.press.PressRecipe;
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

/** Optional independent JEI plugin, preserving the original NEI press layout and progress sprite. */
@JeiPlugin
public final class PressJeiPlugin implements IModPlugin {
    public static final RecipeType<PressRecipe> TYPE=RecipeType.create("hbm","press",PressRecipe.class);
    public static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei_press.png");
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm","press_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) { registration.addRecipeCategories(new Category(registration.getJeiHelpers().getGuiHelper())); }
    // The synchronized native manager contains both datapack recipes and configured-material WIRE holders.
    @Override public void registerRecipes(IRecipeRegistration registration) { var level=Minecraft.getInstance().level; if(level!=null) registration.addRecipes(TYPE,level.getRecipeManager().getAllRecipesFor(PressContent.RECIPE_TYPE.get()).stream().map(holder->holder.value()).toList()); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) { registration.addRecipeCatalysts(TYPE,PressContent.ITEM.get()); }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) { registration.addRecipeClickArea(PressScreen.class,98,23,24,18,TYPE); }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) { registration.addRecipeTransferHandler(new IRecipeTransferInfo<PressMenu,PressRecipe>() {
        @Override public Class<PressMenu> getContainerClass() { return PressMenu.class; }
        @Override public Optional<MenuType<PressMenu>> getMenuType() { return Optional.of(PressContent.MENU.get()); }
        @Override public RecipeType<PressRecipe> getRecipeType() { return TYPE; }
        @Override public boolean canHandle(PressMenu menu,PressRecipe recipe) { return true; }
        @Override public List<Slot> getRecipeSlots(PressMenu menu,PressRecipe recipe) { return List.of(menu.getSlot(2),menu.getSlot(1)); }
        @Override public List<Slot> getInventorySlots(PressMenu menu,PressRecipe recipe) { return menu.slots.subList(4,49); }
    }); }
    private static final class Category implements IRecipeCategory<PressRecipe> {
        private final IDrawable background,icon,progress;
        Category(IGuiHelper gui) {
            background=gui.createDrawable(TEXTURE,5,11,166,65);
            icon=gui.createDrawableItemStack(new ItemStack(PressContent.ITEM.get()));
            progress=gui.createAnimatedDrawable(gui.createDrawable(TEXTURE,0,86,18,18),20,IDrawableAnimated.StartDirection.TOP,false);
        }
        @Override public RecipeType<PressRecipe> getRecipeType() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("container.press"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder,PressRecipe recipe,IFocusGroup focuses) {
            builder.addSlot(RecipeIngredientRole.INPUT,48,42).addIngredients(recipe.input());
            builder.addSlot(RecipeIngredientRole.INPUT,48,6).addItemStacks(StampItem.stamps(recipe.stamp()));
            builder.addSlot(RecipeIngredientRole.OUTPUT,111,24).addItemStack(recipe.result());
        }
        @Override public void draw(PressRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y) { progress.draw(graphics,47,24); }
    }
}
