package com.hbm.item.autogen;

import com.hbm.material.HbmMaterials;
import com.hbm.material.MaterialDistributionConfig;
import com.hbm.registry.HbmItemTags;
import com.hbm.machine.alloy.AlloyStackIngredient;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** The two actual OreDictManager654..670 loops, including configured materials and first real outputs. */
public final class SourceMaterialCraftingRecipes {
    private static final Map<RecipeManager, Map<ResourceLocation, RecipeHolder<?>>> INSTALLED = new WeakHashMap<>();
    private static boolean registered;
    private SourceMaterialCraftingRecipes() { }
    public static void register() {
        if (registered) return;
        registered = true;
        NeoForge.EVENT_BUS.addListener(SourceMaterialCraftingRecipes::starting);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, SourceMaterialCraftingRecipes::sync);
    }
    private static void starting(ServerAboutToStartEvent event) { install(event.getServer().getRecipeManager()); }
    private static void sync(OnDatapackSyncEvent event) {
        var manager = event.getPlayerList().getServer().getRecipeManager();
        if (event.getPlayer() == null || !INSTALLED.containsKey(manager)) install(manager);
    }
    private static void install(RecipeManager manager) {
        var previous = INSTALLED.getOrDefault(manager, Map.of());
        var merged = new LinkedHashMap<ResourceLocation, RecipeHolder<?>>();
        for (var holder : manager.getOrderedRecipes()) if (previous.get(holder.id()) != holder) merged.put(holder.id(), holder);
        var generated = new LinkedHashMap<ResourceLocation, RecipeHolder<?>>();
        var fragmentShape = HbmMaterials.shape("FRAGMENT");
        var nuggetShape = HbmMaterials.shape("NUGGET");
        for (var material : HbmMaterials.ordered()) {
            String name = material.names().getFirst();
            if (material.autogen().contains(fragmentShape)) {
                ItemStack fragment = material.make(AutogenContent.item("bedrock_ore_fragment"));
                ItemStack output = ItemStack.EMPTY;
                int rows = 3;
                // Literal original fallback priority. Never select a static default material/item.
                for (String shape : new String[] {"DUST", "GEM", "CRYSTAL", "INGOT", "BILLET", "ORE"}) {
                    output = first(shape, name);
                    if (!output.isEmpty()) { if (shape.equals("BILLET")) rows = 2; break; }
                }
                if (output.isEmpty()) output = MaterialDistributionConfig.legacyStack("hbm:nothing", 0);
                add(merged, generated, "fragments/" + material.id(), fragment, output, rows);
            }
            if (material.autogen().contains(nuggetShape)) {
                ItemStack ingot = first("INGOT", name);
                if (!ingot.isEmpty()) {
                    ItemStack nugget = material.make(AutogenContent.item("nugget_raw"));
                    add(merged, generated, "nugget_compress/" + material.id(), nugget, ingot, 3);
                    add(merged, generated, "nugget_decompress/" + material.id(), ingot, nugget.copyWithCount(9), 1);
                }
            }
        }
        if (!generated.isEmpty() || !previous.isEmpty()) manager.replaceRecipes(merged.values());
        INSTALLED.put(manager, generated);
    }
    private static ItemStack first(String shape, String material) {
        return HbmItemTags.items(HbmItemTags.material(shape, material)).findFirst().map(ItemStack::copy).orElse(ItemStack.EMPTY);
    }
    private static void add(Map<ResourceLocation, RecipeHolder<?>> merged, Map<ResourceLocation, RecipeHolder<?>> generated,
                            String path, ItemStack input, ItemStack output, int rows) {
        var id = ResourceLocation.fromNamespaceAndPath("hbm", "source_material_crafting/" + path);
        if (merged.containsKey(id)) return;
        Ingredient ingredient = new AlloyStackIngredient(input.copyWithCount(1)).toVanilla();
        List<String> pattern = rows == 1 ? List.of("#") : rows == 2 ? List.of("###", "###") : List.of("###", "###", "###");
        var recipe = new ShapedRecipe("", CraftingBookCategory.MISC, ShapedRecipePattern.of(Map.of('#', ingredient), pattern), output.copy());
        var holder = new RecipeHolder<>(id, recipe);
        generated.put(id, holder);
        merged.put(id, holder);
    }
}
