package com.hbm.client.ore;

import com.hbm.hazard.HbmHazards;
import com.hbm.ore.OreRecipeInputs;
import com.hbm.ore.RawOreContent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

/** Viewer-only projections of actual native recipes; never writes to RecipeManager. */
public final class RawOreSmeltingViews {
    private RawOreSmeltingViews() {}
    public record View(ResourceLocation id, List<ItemStack> inputs, ItemStack output,
                       int duration, float experience, boolean blasting) {}

    public static List<View> recipes() {
        var level = Minecraft.getInstance().level;
        if (level == null) return List.of();
        List<View> views = new ArrayList<>();
        for (var source : RawOreContent.sources()) {
            ItemStack original = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(source.originalItem())));
            if (source.legacyMetadata() != 0 || original.getItem() instanceof com.hbm.block.ore.LegacyOreItem ore && ore.spec().multi())
                original.set(HbmHazards.LEGACY_METADATA.get(), source.legacyMetadata());
            var input = new SingleRecipeInput(original);
            level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level).ifPresent(holder ->
                    views.add(view(source, holder.value(), false)));
            level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, input, level).ifPresent(holder ->
                    views.add(view(source, holder.value(), true)));
        }
        return List.copyOf(views);
    }

    private static View view(RawOreContent.Source source, AbstractCookingRecipe recipe, boolean blasting) {
        var level = Minecraft.getInstance().level;
        var id = ResourceLocation.fromNamespaceAndPath("hbm", "raw_ore_view/" + (blasting ? "blasting/" : "smelting/")
                + source.sourceSymbol().replace(':', '/') + "/" + source.legacyMetadata());
        return new View(id, OreRecipeInputs.examples(source, 1), recipe.getResultItem(level.registryAccess()).copy(),
                recipe.getCookingTime(), recipe.getExperience(), blasting);
    }

}
