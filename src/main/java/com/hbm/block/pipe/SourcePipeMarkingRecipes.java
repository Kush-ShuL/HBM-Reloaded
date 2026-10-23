package com.hbm.block.pipe;

import com.hbm.fluid.HbmFluids;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.fluid.FluidIdentifierItem;
import com.hbm.item.fluid.FluidItemContent;
import com.hbm.machine.alloy.AlloyStackIngredient;
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

/** CraftingManager648/650/655/658, using the actual native recipes and live source fluid registry. */
public final class SourcePipeMarkingRecipes {
    private static final Map<RecipeManager, Map<ResourceLocation, RecipeHolder<ShapelessRecipe>>> INSTALLED = new WeakHashMap<>();
    private static boolean registered;
    private SourcePipeMarkingRecipes() { }

    public static void register() {
        if (registered) return;
        registered = true;
        NeoForge.EVENT_BUS.addListener(SourcePipeMarkingRecipes::serverStarting);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, SourcePipeMarkingRecipes::datapackSync);
    }

    private static void serverStarting(ServerAboutToStartEvent event) { install(event.getServer().getRecipeManager()); }
    private static void datapackSync(OnDatapackSyncEvent event) {
        RecipeManager manager = event.getPlayerList().getServer().getRecipeManager();
        if (event.getPlayer() == null || !INSTALLED.containsKey(manager)) install(manager);
    }

    private static void install(RecipeManager manager) {
        var previous = INSTALLED.getOrDefault(manager, Map.of());
        Map<ResourceLocation, RecipeHolder<?>> merged = new LinkedHashMap<>();
        for (RecipeHolder<?> holder : manager.getOrderedRecipes()) {
            if (previous.get(holder.id()) != holder) merged.put(holder.id(), holder);
        }
        Map<ResourceLocation, RecipeHolder<ShapelessRecipe>> generated = new LinkedHashMap<>();
        // Deliberately the literal original i bound, including user-defined types.
        // Do not filter hasNoID or substitute nice-order indices/registered numeric IDs.
        for (int fluid = 1; fluid < HbmFluids.getAll().length; fluid++) {
            ItemStack identifier = new ItemStack(FluidItemContent.IDENTIFIER.get());
            identifier.set(HbmHazards.LEGACY_METADATA.get(), fluid);
            identifier.set(FluidItemContent.FLUID_TYPE.get(), fluid);
            FluidIdentifierItem.setType(identifier, HbmFluids.fromID(fluid), true);
            Ingredient marker = new AlloyStackIngredient(identifier).toVanilla();
            for (boolean typed : new boolean[] {false, true}) {
                ItemStack source = new ItemStack(typed ? PipeContent.TYPED_ITEM.get() : PipeContent.ITEM.get());
                source.set(HbmHazards.LEGACY_METADATA.get(), typed ? 32767 : 0);
                Ingredient pipe = new AlloyStackIngredient(source).toVanilla();
                for (int count : new int[] {1, 8}) {
                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath("hbm", "source_pipe_marking/" + (typed ? "typed_" : "untyped_") + count + "/" + fluid);
                    if (merged.containsKey(id)) continue;
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    for (int index = 0; index < count; index++) ingredients.add(pipe);
                    ingredients.add(marker);
                    ItemStack output = new ItemStack(PipeContent.TYPED_ITEM.get(), count);
                    output.set(HbmHazards.LEGACY_METADATA.get(), fluid);
                    output.set(FluidItemContent.FLUID_TYPE.get(), fluid);
                    var recipe = new ShapelessRecipe("", CraftingBookCategory.MISC, output, ingredients);
                    var holder = new RecipeHolder<>(id, recipe);
                    generated.put(id, holder);
                    merged.put(id, holder);
                }
            }
        }
        if (!generated.isEmpty() || !previous.isEmpty()) manager.replaceRecipes(merged.values());
        INSTALLED.put(manager, generated);
    }
}
