package com.hbm.machine.shredder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.JsonOps;
import com.hbm.registry.HbmOreDictionary;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Executable source defaults plus original registerPost material inference, synchronized as native recipes. */
public final class ShredderRecipes {
    private record Key(Item item, int metadata) {}
    private record Installed(Map<ResourceLocation, RecipeHolder<ShredderRecipe>> holders, Map<Key, ItemStack> results) {}
    private static final Map<RecipeManager, Installed> INSTALLED = new WeakHashMap<>();
    private static final List<BiConsumer<Player, ItemStack>> OUTPUT_LISTENERS = new ArrayList<>();
    private static boolean registered;
    private ShredderRecipes() {}
    public static void register() {
        if (registered) return; registered = true;
        NeoForge.EVENT_BUS.addListener(ShredderRecipes::starting);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ShredderRecipes::sync);
    }
    private static void starting(ServerAboutToStartEvent event) { install(event.getServer().getRecipeManager(), event.getServer().registryAccess()); }
    private static void sync(OnDatapackSyncEvent event) {
        var manager = event.getPlayerList().getServer().getRecipeManager();
        if (event.getPlayer() == null || !INSTALLED.containsKey(manager)) install(manager, event.getPlayerList().getServer().registryAccess());
    }
    public static void addOutputListener(BiConsumer<Player, ItemStack> listener) { OUTPUT_LISTENERS.add(listener); }
    static void outputTaken(Player player, ItemStack stack) { OUTPUT_LISTENERS.forEach(listener -> listener.accept(player, stack)); }
    private static Key key(ItemStack stack) { return new Key(stack.getItem(), ShredderRecipe.metadata(stack)); }
    public static ItemStack result(Level level, ItemStack input) {
        input = com.hbm.ore.RawOreContent.processingStack(input);
        var installed = INSTALLED.get(level.getRecipeManager());
        if (installed != null && !input.isEmpty()) {
            ItemStack result = installed.results.get(key(input));
            if (result == null) result = installed.results.get(new Key(input.getItem(), 32767));
            if (result != null) return result.copy();
        }
        return registered("hbm:scrap", 1);
    }
    public static void reload(net.minecraft.server.MinecraftServer server) { install(server.getRecipeManager(), server.registryAccess()); }
    private static void install(RecipeManager manager, HolderLookup.Provider lookup) {
        Installed previous = INSTALLED.get(manager);
        Map<ResourceLocation, RecipeHolder<?>> merged = new LinkedHashMap<>();
        for (var holder : manager.getOrderedRecipes()) if (previous == null || previous.holders.get(holder.id()) != holder) merged.put(holder.id(), holder);
        Map<Key, ShredderRecipe> recipes = new LinkedHashMap<>();
        Map<Key, ResourceLocation> ids = new LinkedHashMap<>();
        try (var stream = ShredderRecipes.class.getResourceAsStream("/hbm/shredder.json")) {
            if (stream == null) throw new IllegalStateException("Run tools/port_shredder.py");
            var root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            for (var value : root.getAsJsonArray("defaults")) {
                JsonObject row = value.getAsJsonObject();
                ItemStack input = decode(row.getAsJsonObject("input"), lookup), result = decode(row.getAsJsonObject("result"), lookup);
                if (input.isEmpty() || result.isEmpty()) continue;
                int metadata = matchMetadata(row.getAsJsonObject("input"), input);
                put(recipes, ids, new ShredderRecipe(input.copyWithCount(1), metadata, result), ResourceLocation.parse(row.get("id").getAsString()));
            }
            for (var rule : root.getAsJsonArray("oreDefaultLoops")) {
                var parts = rule.getAsJsonArray(); ItemStack result = registered(parts.get(1).getAsString(), parts.get(2).getAsInt());
                for (var input : ore(parts.get(0).getAsString())) putGenerated(recipes, ids, input, result, "wood_defaults");
            }
            List<ShredderRecipe> custom = ShredderConfig.read(lookup, root.getAsJsonObject("legacyAliases"));
            if (custom != null) {
                recipes.clear(); ids.clear(); int index = 0;
                for (var recipe : custom) {
                    Key key = new Key(recipe.input().getItem(), recipe.metadata());
                    recipes.put(key, recipe); ids.put(key, ResourceLocation.fromNamespaceAndPath("hbm", "shredder/config/" + index++));
                }
            } else ShredderConfig.writeTemplate(recipes.values());
        } catch (java.io.IOException error) { throw new IllegalStateException("Cannot load original shredder defaults", error); }
        // Native data packs correspond to source readRecipe's unconditional put.
        // Insert before registerPost, whose setRecipe refuses to overwrite them.
        for (var holder : manager.getAllRecipesFor(ShredderContent.RECIPE_TYPE.get())) {
            if (previous != null && previous.holders.get(holder.id()) == holder) continue;
            ShredderRecipe recipe = holder.value(); Key key = new Key(recipe.input().getItem(), recipe.metadata());
            recipes.put(key, recipe); ids.put(key, holder.id());
        }
        for (String name : HbmOreDictionary.allNames()) {
            if (name.isEmpty() || name.contains("Any")) continue;
            List<ItemStack> matches = ore(name); if (matches.isEmpty()) continue;
            for (String prefix : List.of("ingot", "plate", "gem", "crystal", "ore")) if (name.startsWith(prefix) && name.length() > prefix.length()) {
                ItemStack dust = preferredDust(name.substring(prefix.length()));
                if (!dust.isEmpty() && !isScrap(dust)) { dust.setCount(prefix.equals("ore") ? 2 : 1); for (var input : matches) putGenerated(recipes, ids, input, dust, "ore_post"); }
            }
            if (name.startsWith("block") && name.length() > 5) {
                String suffix = name.substring(5); ItemStack dust = preferredDust(suffix);
                if (!dust.isEmpty() && !isScrap(dust)) { dust.setCount(ore("ingot" + suffix).isEmpty() && ore("gem" + suffix).isEmpty() ? 4 : 9); for (var input : matches) putGenerated(recipes, ids, input, dust, "ore_post"); }
            }
            // The original length checks include an exact eight-letter dustTiny name.
            ItemStack fallback = name.startsWith("dustTiny") && name.length() > 7 ? registered("hbm:dust_tiny", 1)
                    : name.startsWith("dust") && name.length() > 3 ? registered("hbm:dust", 1) : ItemStack.EMPTY;
            if (!fallback.isEmpty()) for (var input : matches) putGenerated(recipes, ids, input, fallback, "dust_post");
        }
        Map<ResourceLocation, RecipeHolder<ShredderRecipe>> generated = new LinkedHashMap<>();
        Map<Key, ItemStack> results = new LinkedHashMap<>();
        recipes.forEach((key, recipe) -> {
            ResourceLocation id = ids.get(key);
            var holder = new RecipeHolder<>(id, recipe);
            if (!merged.containsKey(id)) { merged.put(id, holder); generated.put(id, holder); }
            results.put(key, recipe.result().copy());
        });
        manager.replaceRecipes(merged.values());
        INSTALLED.put(manager, new Installed(generated, results));
    }
    private static void put(Map<Key, ShredderRecipe> recipes, Map<Key, ResourceLocation> ids, ShredderRecipe recipe, ResourceLocation id) {
        if (recipe.input().isEmpty() || recipe.result().isEmpty()) return;
        Key key = new Key(recipe.input().getItem(), recipe.metadata());
        if (!recipes.containsKey(key)) { recipes.put(key, recipe); ids.put(key, id); }
    }
    private static void putGenerated(Map<Key, ShredderRecipe> recipes, Map<Key, ResourceLocation> ids, ItemStack input, ItemStack result, String phase) {
        if (input.isEmpty() || result.isEmpty()) return;
        String item = BuiltInRegistries.ITEM.getKey(input.getItem()).toString().replace(':', '/');
        int metadata = ShredderRecipe.metadata(input);
        put(recipes, ids, new ShredderRecipe(input.copyWithCount(1), metadata, result.copy()), ResourceLocation.fromNamespaceAndPath("hbm", "shredder/" + phase + "/" + item + "/" + metadata));
    }
    private static List<ItemStack> ore(String name) { return HbmOreDictionary.items("hbm:legacy_ore_dict/" + name.toLowerCase(Locale.ROOT)).toList(); }
    private static ItemStack preferredDust(String name) {
        var matches = ore("dust" + name); if (matches.isEmpty()) return ItemStack.EMPTY;
        int best = Integer.MAX_VALUE; ItemStack chosen = matches.getFirst();
        List<? extends String> preferred = ShredderConfig.preferredOutputMods();
        for (ItemStack stack : matches) {
            int index = preferred.indexOf(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
            if (index >= 0 && index < best) { chosen = stack; best = index; }
        }
        return chosen.copy();
    }
    private static boolean isScrap(ItemStack stack) { return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("hbm:scrap"); }
    private static ItemStack registered(String id, int count) {
        var location = ResourceLocation.parse(id);
        return BuiltInRegistries.ITEM.containsKey(location) ? new ItemStack(BuiltInRegistries.ITEM.get(location), count) : ItemStack.EMPTY;
    }
    private static int matchMetadata(JsonObject spec, ItemStack stack) {
        // Explicit source wildcard remains a matcher field, not a native stack component.
        return spec.has("metadata") ? spec.get("metadata").getAsInt() : ShredderRecipe.metadata(stack);
    }
    private static ItemStack decode(JsonObject spec, HolderLookup.Provider lookup) {
        if (!BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(spec.get("id").getAsString()))) return ItemStack.EMPTY;
        // Flat IDs need no metadata. Decode native count/components instead of discarding them.
        ItemStack stack = ItemStack.CODEC.parse(lookup.createSerializationContext(JsonOps.INSTANCE), spec).getOrThrow();
        int metadata = matchMetadata(spec, stack);
        if (spec.has("metadata") && metadata != 32767 && (metadata != 0 || stack.has(HbmHazards.LEGACY_METADATA.get()))) {
            if (stack.isDamageableItem()) stack.setDamageValue(metadata); else stack.set(HbmHazards.LEGACY_METADATA.get(), metadata);
        }
        return stack;
    }
}
