package com.hbm.item.lemegeton;

import java.util.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

/** The source uses a HashMap<AStack,...>, not source-list priority or a sorted tag scan. */
public final class LemegetonConversions {
    private record SourceKey(String name, int sourceHash) { @Override public int hashCode() { return sourceHash; } }
    public static RecipeHolder<LemegetonRecipe> find(Level level, LemegetonInput input) {
        if (input.item().isEmpty()) return null;
        var table = new HashMap<SourceKey, RecipeHolder<LemegetonRecipe>>();
        // Restoring original put order also preserves collision-chain order after ordinary HashMap resize.
        level.getRecipeManager().getAllRecipesFor(LemegetonContent.TYPE.get()).stream().sorted(Comparator.comparingInt(holder -> holder.value().order())).forEach(holder -> table.put(new SourceKey(holder.value().sourceKey(), holder.value().sourceHash()), holder));
        for (var entry : table.entrySet()) if (entry.getValue().value().matches(input, level)) return entry.getValue();
        return null;
    }
    private LemegetonConversions() { }
}
