package com.hbm.world.structure.logic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/** One strict source-ID dictionary; missing equipment is a dependency, never an air/fallback item or reduced pool. */
public final class LogicDependencies {
    private static final JsonObject DATA = load();
    private static JsonObject load() {
        try (var stream = LogicDependencies.class.getResourceAsStream("/hbm/mob-equipment-pools.json")) {
            if (stream == null) throw new IllegalStateException("Missing actual source mob equipment pool data");
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (java.io.IOException error) { throw new IllegalStateException("Cannot read source equipment pools", error); }
    }
    static JsonObject data() { return DATA; }
    public static String itemId(String symbol) {
        var ids = DATA.getAsJsonObject("itemIds");
        if (!ids.has(symbol)) throw new MissingDependencies(List.of("unmapped source item " + symbol));
        return ids.get(symbol).getAsString();
    }
    public static boolean hasItem(String id) {
        var key = ResourceLocation.tryParse(id);
        return key != null && BuiltInRegistries.ITEM.containsKey(key);
    }
    public static Item item(String symbol) { return item(symbol, itemId(symbol)); }
    static Item item(String symbol, String id) {
        if (!hasItem(id)) throw new MissingDependencies(List.of(symbol + " -> " + id));
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }
    public static List<String> pendingItems(String... symbols) {
        var pending = new TreeSet<String>();
        for (String symbol : symbols) {
            String id = itemId(symbol);
            if (!hasItem(id)) pending.add(symbol + " -> " + id);
        }
        return List.copyOf(pending);
    }
    public static void requireItems(String... symbols) {
        var missing = pendingItems(symbols);
        if (!missing.isEmpty()) throw new MissingDependencies(missing);
    }
    public static List<String> pendingPool(Map<?, List<WeightedRandomObject>> pools) {
        var missing = new TreeSet<String>();
        for (var pool : pools.values()) for (var entry : pool)
            if (entry.item instanceof WeightedRandomObject.SourceItem item && !item.ready())
                missing.add(item.symbol() + " -> " + item.id());
        return List.copyOf(missing);
    }
    public static void requirePool(Map<?, List<WeightedRandomObject>> pools) {
        var missing = pendingPool(pools);
        if (!missing.isEmpty()) throw new MissingDependencies(missing);
    }
    public static final class MissingDependencies extends IllegalStateException {
        public final List<String> dependencies;
        public MissingDependencies(Collection<String> dependencies) {
            super("Unported actual source dependencies: " + String.join(", ", dependencies));
            this.dependencies = List.copyOf(dependencies);
        }
    }
    private LogicDependencies() {}
}
