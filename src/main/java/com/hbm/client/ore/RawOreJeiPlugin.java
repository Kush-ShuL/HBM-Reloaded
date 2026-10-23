package com.hbm.client.ore;

import com.hbm.ore.OreRecipeInputs;
import com.hbm.ore.RawOreContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@JeiPlugin
public final class RawOreJeiPlugin implements IModPlugin {
    private static final RecipeType<RawOreSmeltingViews.View> SMELTING = RecipeType.create("hbm", "raw_ore_smelting", RawOreSmeltingViews.View.class);
    private static final RecipeType<RawOreSmeltingViews.View> BLASTING = RecipeType.create("hbm", "raw_ore_blasting", RawOreSmeltingViews.View.class);
    private static final ResourceLocation ARROW = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm", "raw_ore_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration event) {
        event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper(), false), new Category(event.getJeiHelpers().getGuiHelper(), true));
    }
    @Override public void registerRecipes(IRecipeRegistration event) {
        var recipes = RawOreSmeltingViews.recipes();
        event.addRecipes(SMELTING, recipes.stream().filter(view -> !view.blasting()).toList());
        event.addRecipes(BLASTING, recipes.stream().filter(RawOreSmeltingViews.View::blasting).toList());
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event) {
        event.addRecipeCatalysts(SMELTING, Items.FURNACE, com.hbm.machine.electricfurnace.ElectricFurnaceContent.ITEM.get());
        event.addRecipeCatalysts(BLASTING, Items.BLAST_FURNACE);
    }
    private static final class Category implements IRecipeCategory<RawOreSmeltingViews.View> {
        private final boolean blasting;
        private final IDrawable background, icon;
        Category(IGuiHelper gui, boolean blasting) {
            this.blasting = blasting;
            background = gui.createBlankDrawable(110, 40);
            icon = gui.createDrawableItemStack(new ItemStack(blasting ? Items.BLAST_FURNACE : Items.FURNACE));
        }
        @Override public RecipeType<RawOreSmeltingViews.View> getRecipeType() { return blasting ? BLASTING : SMELTING; }
        @Override public Component getTitle() { return Component.translatable(blasting ? "gui.hbm.raw_ore.blasting" : "gui.hbm.raw_ore.smelting"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder, RawOreSmeltingViews.View recipe, IFocusGroup focus) {
            builder.addSlot(RecipeIngredientRole.INPUT, 5, 11).addItemStacks(recipe.inputs())
                    .addTooltipCallback((slot, tooltip) -> slot.getDisplayedItemStack().ifPresent(stack -> tooltip.addAll(OreRecipeInputs.tooltip(stack))));
            builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 11).addItemStack(recipe.output())
                    .addTooltipCallback((slot, tooltip) -> tooltip.add(Component.translatable("gui.hbm.raw_ore.source_output")));
        }
        @Override public void draw(RawOreSmeltingViews.View recipe, IRecipeSlotsView slots, GuiGraphics graphics, double x, double y) {
            graphics.blitSprite(ARROW, 43, 11, 24, 16);
        }
    }
}
