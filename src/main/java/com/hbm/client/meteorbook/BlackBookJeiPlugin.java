package com.hbm.client.meteorbook;

import com.hbm.item.meteorbook.*;
import java.util.Comparator;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Source BookRecipeHandler's real ordered recipes and exact NEI coordinates. */
@JeiPlugin
public final class BlackBookJeiPlugin implements IModPlugin {
    public static final RecipeType<MagicBookRecipe> TYPE = RecipeType.create("hbm", "book_of_boxcars", MagicBookRecipe.class);
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm", "black_book_jei"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration) { registration.addExtraIngredients(VanillaTypes.ITEM_STACK, MeteorBookContent.printingStamps()); }
    @Override public void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime runtime) {
        // Original NEIConfig only exposes the Black Book itself for polaroid ID 11; recipes still exist.
        if (com.hbm.LegacyStartup.polaroidId != 11) runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, java.util.List.of(new ItemStack(MeteorBookContent.BOOK.get())));
    }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) { registration.addRecipeCategories(new Category(registration.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level != null) registration.addRecipes(TYPE, level.getRecipeManager().getAllRecipesFor(MeteorBookContent.TYPE.get()).stream().map(holder -> holder.value()).sorted(Comparator.comparingInt(MagicBookRecipe::order)).toList());
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) { registration.addRecipeCatalysts(TYPE, MeteorBookContent.BOOK.get()); }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) { registration.addRecipeTransferHandler(BlackBookMenu.class, MeteorBookContent.MENU.get(), TYPE, 1, 4, 5, 36); }
    private static final class Category implements IRecipeCategory<MagicBookRecipe> {
        private final IDrawable background, icon;
        Category(mezz.jei.api.helpers.IGuiHelper gui) {
            background = gui.createDrawable(BlackBookScreen.TEXTURE, 5, 11, 166, 65);
            icon = gui.createDrawableItemStack(new ItemStack(MeteorBookContent.BOOK.get()));
        }
        @Override public RecipeType<MagicBookRecipe> getRecipeType() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.black_book"); }
        @Override public IDrawable getBackground() { return background; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder, MagicBookRecipe recipe, IFocusGroup focuses) {
            for (int i = 0; i < recipe.inputs().size(); i++) builder.addSlot(RecipeIngredientRole.INPUT, 25 + i % 2 * 36, 6 + i / 2 * 36).addItemStacks(recipe.inputs().get(i).examples());
            builder.addSlot(RecipeIngredientRole.OUTPUT, 119, 24).addItemStack(recipe.result());
        }
    }
}
