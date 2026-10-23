package com.hbm.registry;

import com.google.gson.JsonParser;
import com.hbm.hazard.HbmHazards;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import com.hbm.recipe.LegacyOreIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Metadata-specific old ore memberships cannot be represented by vanilla item tags. */
public final class HbmOreDictionary {
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, "hbm");
    public static final DeferredHolder<IngredientType<?>, IngredientType<LegacyOreIngredient>> INGREDIENT =
            INGREDIENT_TYPES.register("legacy_ore_dict", () -> new IngredientType<>(LegacyOreIngredient.CODEC));
    private static final Map<Key, List<String>> MEMBERSHIPS = new HashMap<>();
    private static final Map<String, Integer> ORDER = new HashMap<>();
    private static final Map<String, List<String>> RAW_NAMES = new java.util.LinkedHashMap<>();
    private static final List<Provider> PROVIDERS = new CopyOnWriteArrayList<>();

    static {
        try (var stream = HbmOreDictionary.class.getResourceAsStream("/hbm/ore-dictionary.json")) {
            if (stream == null) throw new IllegalStateException("Missing ore-dictionary.json: run tools/port_material_tags.py");
            var root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            int ordinal = 0;
            for (var tag : root.getAsJsonArray("order")) ORDER.putIfAbsent(tag.getAsString(), ordinal++);
            if (root.has("names")) for (var value : root.getAsJsonArray("names")) {
                var row = value.getAsJsonObject();
                RAW_NAMES.computeIfAbsent(row.get("tag").getAsString(), ignored -> new ArrayList<>()).add(row.get("raw").getAsString());
            }
            for (var value : root.getAsJsonArray("memberships")) {
                var row = value.getAsJsonObject();
                String tag = row.get("tag").getAsString();
                for (var member : row.getAsJsonArray("members")) {
                    var spec = member.getAsJsonObject();
                    var components = spec.getAsJsonObject("components");
                    int metadata = components != null && components.has("hbm:legacy_metadata") ? components.get("hbm:legacy_metadata").getAsInt()
                            : components != null && components.has("minecraft:damage") ? components.get("minecraft:damage").getAsInt() : 0;
                    MEMBERSHIPS.computeIfAbsent(new Key(spec.get("id").getAsString(), metadata), ignored -> new ArrayList<>()).add(tag);
                }
            }
        } catch (java.io.IOException error) {
            throw new ExceptionInInitializerError(error);
        }
    }

    private HbmOreDictionary() {}

    public static void register(IEventBus bus) { INGREDIENT_TYPES.register(bus); }

    public static void addProvider(Function<ItemStack, List<String>> tags) {
        addProvider(tags, ignored -> Stream.empty());
    }

    public static void addProvider(Function<ItemStack, List<String>> tags, Function<String, Stream<ItemStack>> items) {
        PROVIDERS.add(new Provider(tags, items));
    }

    /** Keep original case for Mats prefix/material matching; tag IDs remain exactly lowercase. */
    public static synchronized String registerName(String rawName) {
        String tag = "hbm:legacy_ore_dict/" + rawName.toLowerCase(java.util.Locale.ROOT);
        ResourceLocation.parse(tag);
        var names = RAW_NAMES.computeIfAbsent(tag, ignored -> new ArrayList<>());
        if (!names.contains(rawName)) names.add(rawName);
        ORDER.putIfAbsent(tag, ORDER.size());
        return tag;
    }

    public static List<String> names(ItemStack stack) {
        return tags(stack).stream().flatMap(tag -> RAW_NAMES.getOrDefault(tag, List.of()).stream()).distinct().toList();
    }

    public static List<String> allNames() { return RAW_NAMES.values().stream().flatMap(List::stream).distinct().toList(); }

    /** Preserve per-stack membership without tagging every subtype of the base item. */
    public static List<String> tags(ItemStack stack) {
        var result = new LinkedHashSet<String>();
        stack.getTags().forEach(tag -> result.add(tag.location().toString()));
        String item = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        int metadata = stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue());
        result.addAll(MEMBERSHIPS.getOrDefault(new Key(item, metadata), List.of()));
        if (metadata != 32767) result.addAll(MEMBERSHIPS.getOrDefault(new Key(item, 32767), List.of()));
        for (var provider : PROVIDERS) result.addAll(provider.tags().apply(stack));
        return result.stream().sorted(Comparator.comparingInt(tag -> ORDER.getOrDefault(tag, Integer.MAX_VALUE))).toList();
    }

    /** Actual component stacks are available to recipe viewers without broad subtype tags. */
    public static Stream<ItemStack> items(String tag) {
        var tagged = BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, ResourceLocation.parse(tag)))
                .stream().flatMap(set -> set.stream()).map(holder -> new ItemStack(holder.value()));
        var metadata = MEMBERSHIPS.entrySet().stream().filter(entry -> entry.getValue().contains(tag))
                .filter(entry -> BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(entry.getKey().item())))
                .map(entry -> {
                    ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(entry.getKey().item())));
                    if (stack.isDamageableItem()) stack.setDamageValue(entry.getKey().metadata());
                    else if (!(stack.getItem() instanceof com.hbm.item.FlatSourceItem)) stack.set(HbmHazards.LEGACY_METADATA.get(), entry.getKey().metadata());
                    return stack;
                });
        var dynamic = PROVIDERS.stream().flatMap(provider -> provider.items().apply(tag));
        var distinct = net.minecraft.world.item.ItemStackLinkedSet.createTypeAndComponentsSet();
        return Stream.concat(Stream.concat(tagged, metadata), dynamic).filter(distinct::add);
    }

    private record Key(String item, int metadata) {}
    private record Provider(Function<ItemStack, List<String>> tags, Function<String, Stream<ItemStack>> items) {}
}
