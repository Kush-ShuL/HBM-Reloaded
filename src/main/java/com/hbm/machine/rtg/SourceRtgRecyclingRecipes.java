package com.hbm.machine.rtg;

import com.hbm.hazard.HbmHazards;
import com.hbm.machine.alloy.AlloyStackIngredient;
import com.hbm.registry.HbmOreDictionary;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** MineralRecipes225: actual first registered nickel ingot, never a fabricated HBM nickel. */
public final class SourceRtgRecyclingRecipes {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("hbm", "source_rtg_recycling/nickel");
    private static final String TAG = "hbm:legacy_ore_dict/ingotnickel";
    private static final Map<RecipeManager, RecipeHolder<?>> INSTALLED = new WeakHashMap<>();
    private static boolean registered;
    private SourceRtgRecyclingRecipes() { }
    public static void register() {
        if (registered) return;
        registered = true;
        NeoForge.EVENT_BUS.addListener(SourceRtgRecyclingRecipes::starting);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, SourceRtgRecyclingRecipes::sync);
    }
    private static void starting(ServerAboutToStartEvent event) { install(event.getServer().getRecipeManager()); }
    private static void sync(OnDatapackSyncEvent event) {
        var manager = event.getPlayerList().getServer().getRecipeManager();
        if (event.getPlayer() == null || !INSTALLED.containsKey(manager)) install(manager);
    }
    private static void install(RecipeManager manager) {
        var previous = INSTALLED.get(manager);
        var merged = new LinkedHashMap<ResourceLocation, RecipeHolder<?>>();
        for (var holder : manager.getOrderedRecipes()) if (holder != previous) merged.put(holder.id(), holder);
        RecipeHolder<?> generated = null;
        if (!merged.containsKey(ID)) {
            // The optional native c:ingots/nickel tag supplies real external members
            // through the existing dictionary. An absent tag cannot create a recipe.
            var first = HbmOreDictionary.items(TAG).findFirst();
            if (first.isPresent()) {
                HbmOreDictionary.registerName("ingotNickel");
                ItemStack output = first.get().copyWithCount(2);
                ItemStack pellet = new ItemStack(RtgContent.DEPLETED.get());
                pellet.set(HbmHazards.LEGACY_METADATA.get(), 5); // Actual DepletedRTGMaterial.NICKEL ordinal.
                NonNullList<Ingredient> inputs = NonNullList.create();
                inputs.add(new AlloyStackIngredient(pellet).toVanilla());
                generated = new RecipeHolder<>(ID, new ShapelessRecipe("", CraftingBookCategory.MISC, output, inputs));
                merged.put(ID, generated);
            }
            // Native reload can remove an optional tag after the raw name was
            // registered. No candidate means no output recipe, never a fake ingot
            // or an original get(0) failure while loading the new datapack.
        }
        if (generated != null || previous != null) manager.replaceRecipes(merged.values());
        INSTALLED.put(manager, generated);
    }
}
