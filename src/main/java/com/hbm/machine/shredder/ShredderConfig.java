package com.hbm.machine.shredder;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.hazard.HbmHazards;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Original config/hbmRecipes/{_,}hbmShredder.json enabled-file and array format. */
public final class ShredderConfig {
    private static final String COMMENT = "Ingot/block/ore -> dust recipes are generated in post and can therefore not be changed with the config. Non-auto recipes do not use ore dict.";
    private static final Path DIRECTORY = FMLPaths.CONFIGDIR.get().resolve("hbmRecipes");
    private static final ModConfigSpec COMMON;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> PREFERRED_OUTPUT_MODS;
    static {
        var builder = new ModConfigSpec.Builder();
        PREFERRED_OUTPUT_MODS = builder.comment("碎纸机同名矿粉的模组优先级，按列表从前到后选择。")
                .defineListAllowEmpty("preferredOutputMod", List.of("hbm"), value -> value instanceof String);
        COMMON = builder.build();
    }
    private ShredderConfig() {}
    public static void register(ModContainer container) { container.registerConfig(ModConfig.Type.COMMON, COMMON, "hbm-shredder-common.toml"); }
    public static List<? extends String> preferredOutputMods() { return PREFERRED_OUTPUT_MODS.get(); }
    /** null selects source defaults; an enabled empty recipes array intentionally deletes them. */
    static List<ShredderRecipe> read(HolderLookup.Provider lookup, JsonObject aliases) {
        try {
            Files.createDirectories(DIRECTORY);
            Path instructions = DIRECTORY.resolve("REMOVE UNDERSCORE TO ENABLE RECIPE LOADING - RECIPES WILL RESET TO DEFAULT OTHERWISE");
            if (!Files.exists(instructions)) Files.createFile(instructions);
            Path enabled = DIRECTORY.resolve("hbmShredder.json");
            if (!Files.isRegularFile(enabled)) return null;
            try (var reader = Files.newBufferedReader(enabled)) {
                var root = JsonParser.parseReader(reader).getAsJsonObject();
                var result = new ArrayList<ShredderRecipe>();
                for (var value : root.getAsJsonArray("recipes")) {
                    if (value.isJsonNull()) continue;
                    var row = value.getAsJsonObject();
                    Decoded input = decode(row.getAsJsonArray("input"), lookup, aliases);
                    Decoded output = decode(row.getAsJsonArray("output"), lookup, aliases);
                    result.add(new ShredderRecipe(input.stack.copyWithCount(1), input.metadata, output.stack));
                }
                return result;
            }
        } catch (java.io.IOException exception) { throw new IllegalStateException("Cannot read original hbmShredder.json", exception); }
    }
    static void writeTemplate(Collection<ShredderRecipe> recipes) {
        try {
            var root = new JsonObject(); root.addProperty("comment", COMMENT); var array = new JsonArray();
            for (var recipe : recipes) {
                var row = new JsonObject(); row.add("input", encode(recipe.input(), recipe.metadata())); row.add("output", encode(recipe.result(), ShredderRecipe.metadata(recipe.result()))); array.add(row);
            }
            root.add("recipes", array);
            Files.writeString(DIRECTORY.resolve("_hbmShredder.json"), new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(root) + "\n");
        } catch (Exception exception) { LogUtils.getLogger().error("Error writing original shredder recipe template", exception); }
    }
    private record Decoded(ItemStack stack, int metadata) {}
    private static Decoded decode(JsonArray array, HolderLookup.Provider lookup, JsonObject aliases) {
        try {
            String rawId = array.get(0).getAsString();
            String id = aliases.has(rawId) ? aliases.get(rawId).getAsString() : rawId;
            int count = array.size() > 1 ? array.get(1).getAsInt() : 1;
            int metadata = array.size() > 2 ? array.get(2).getAsInt() : 0;
            var original = new CompoundTag(); original.putString("id", id); original.putByte("Count", (byte) 1); original.putShort("Damage", (short) (metadata == 32767 ? 0 : metadata));
            if (array.size() > 3) try { original.put("tag", TagParser.parseTag(array.get(3).getAsString())); } catch (com.mojang.brigadier.exceptions.CommandSyntaxException ignored) { /* Original addNBTFromString ignores malformed SNBT. */ }
            // Official ITEM_STACK data fixes migrate old vanilla metadata and
            // enchantment/display/etc tags into real 1.21.1 components. Unknown
            // HBM NBT remains minecraft:custom_data, as used by native batteries.
            var updated = DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic<>(NbtOps.INSTANCE, original), 99, SharedConstants.getCurrentVersion().getDataVersion().getVersion());
            ItemStack stack = ItemStack.parseOptional(lookup, (CompoundTag) updated.getValue());
            if (stack.isEmpty()) throw new IllegalArgumentException("Item not registered: " + rawId);
            stack.setCount(count);
            int matchMetadata;
            if (metadata == 32767) {
                if (stack.isDamageableItem()) stack.setDamageValue(metadata); else stack.set(HbmHazards.LEGACY_METADATA.get(), metadata);
                matchMetadata = 32767;
            }
            else if (id.startsWith("hbm:") && !stack.isDamageableItem()) {
                stack.remove(DataComponents.DAMAGE);
                if (metadata != 0 || stack.has(HbmHazards.LEGACY_METADATA.get())) stack.set(HbmHazards.LEGACY_METADATA.get(), metadata);
                matchMetadata = metadata;
            } else matchMetadata = ShredderRecipe.metadata(stack);
            return new Decoded(stack, matchMetadata);
        } catch (Exception exception) {
            LogUtils.getLogger().error("Error reading original shredder stack array {} - defaulting to NOTHING item", array, exception);
            var nothing = ResourceLocation.fromNamespaceAndPath("hbm", "nothing");
            ItemStack stack = BuiltInRegistries.ITEM.containsKey(nothing) ? new ItemStack(BuiltInRegistries.ITEM.get(nothing)) : ItemStack.EMPTY;
            return new Decoded(stack, 0);
        }
    }
    private static JsonArray encode(ItemStack stack, int metadata) {
        var result = new JsonArray(); result.add(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        boolean hasTag = stack.has(DataComponents.CUSTOM_DATA);
        if (stack.getCount() != 1 || metadata != 0 || hasTag) result.add(stack.getCount());
        if (metadata != 0 || hasTag) result.add(metadata);
        if (hasTag) result.add(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().toString());
        return result;
    }
}
