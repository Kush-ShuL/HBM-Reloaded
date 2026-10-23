package com.hbm.missile.custom;

import com.google.gson.*;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Actual source component initialization order and copy() semantics, extracted from ModItems. */
public final class CustomMissileParts {
    public enum PartType { CHIP, WARHEAD, FUSELAGE, FINS, THRUSTER }
    public enum PartSize { ANY, NONE, SIZE_10, SIZE_15, SIZE_20 }
    public enum FuelType { KEROSENE, SOLID, HYDROGEN, XENON, BALEFIRE }
    public enum WarheadType { HE, INC, BUSTER, CLUSTER, NUCLEAR, TX, N2, BALEFIRE, SCHRAB, TAINT, CLOUD, TURBINE,
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7, CUSTOM8, CUSTOM9;
        public java.util.function.Consumer<com.hbm.entity.missile.CustomMissileEntity> impactCustom, updateCustom;
        public String labelCustom;
    }
    public record Definition(String symbol, ResourceLocation id, PartType type, PartSize top, PartSize bottom,
                             List<Object> attributes, float health, String rarity, String title, String author, String witty,
                             ResourceLocation icon, String sourceName) {
        public float number(int index) { return (Float) attributes.get(index); }
        public String enumName(int index) { return (String) attributes.get(index); }
    }
    public static final List<Definition> DEFINITIONS = load();
    private static List<Definition> load() {
        try (var stream = CustomMissileParts.class.getResourceAsStream("/hbm/custom-missile-parts.json")) {
            if (stream == null) throw new IllegalStateException("Missing actual custom missile component definitions");
            var rows = new ArrayList<Definition>();
            for (var value : JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonArray()) {
                var row = value.getAsJsonObject(); var attributes = new ArrayList<Object>();
                for (var attribute : row.getAsJsonArray("attributes")) attributes.add(attribute.getAsJsonPrimitive().isNumber() ? attribute.getAsFloat() : attribute.getAsString());
                rows.add(new Definition(row.get("symbol").getAsString(), ResourceLocation.parse(row.get("id").getAsString()),
                        PartType.valueOf(row.get("type").getAsString()), PartSize.valueOf(row.get("top").getAsString()), PartSize.valueOf(row.get("bottom").getAsString()),
                        List.copyOf(attributes), row.get("health").getAsFloat(), optional(row, "rarity"), optional(row, "title"), optional(row, "author"), optional(row, "witty"),
                        ResourceLocation.parse(row.get("icon").getAsString()), row.get("sourceName").getAsString()));
            }
            return List.copyOf(rows);
        } catch (java.io.IOException error) { throw new IllegalStateException("Cannot load original missile parts", error); }
    }
    private static String optional(JsonObject row, String key) { return row.has(key) && !row.get(key).isJsonNull() ? row.get(key).getAsString() : null; }
    public static Definition part(ItemStack stack) { return stack.getItem() instanceof CustomMissilePartItem part ? part.definition : null; }
    private CustomMissileParts() {}
}
