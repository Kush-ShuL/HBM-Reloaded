package com.hbm.item.autogen;

import com.hbm.material.MaterialStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** X5846 ScrapsCraftingHandler: split one occupied scrap stack, not its item count. */
public final class ScrapsCraftingRecipe extends CustomRecipe {
    public ScrapsCraftingRecipe(CraftingBookCategory category) { super(category); }

    private static MaterialStack material(CraftingInput input) {
        MaterialStack found = null;
        for (int index = 0; index < input.size(); index++) {
            ItemStack stack = input.getItem(index);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof ScrapsItem) || found != null) return null;
            found = ScrapsItem.getMaterials(stack);
            if (found == null || found.amount < 2) return null;
        }
        return found;
    }

    @Override public boolean matches(CraftingInput input, Level level) { return material(input) != null; }

    @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) {
        MaterialStack source = material(input);
        if (source == null) return ItemStack.EMPTY;
        // The source deliberately uses create(mat), not a copy: odd q is truncated,
        // output count is two and the original liquid/other custom-data is not copied.
        ItemStack result = ScrapsItem.create(new MaterialStack(source.material, source.amount / 2));
        result.setCount(2);
        return result;
    }

    @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return AutogenContent.item("scraps").stack(com.hbm.material.HbmMaterials.byId(0),1); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public RecipeSerializer<?> getSerializer() { return AutogenContent.SCRAPS_SPLIT.get(); }
}
