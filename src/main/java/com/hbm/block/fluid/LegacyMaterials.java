package com.hbm.block.fluid;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import com.google.gson.Gson;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

/** Official old block material data, plus original ModBlocks material declarations. */
public final class LegacyMaterials {
    private static final Map<String, LegacyMaterial> MATERIALS = load();
    private static final java.util.concurrent.ConcurrentHashMap<BlockState,LegacyMaterial> CACHE=new java.util.concurrent.ConcurrentHashMap<>();
    private static final Map<String, String> MODERN_ALIASES = Map.ofEntries(
            Map.entry("grass_block", "grass"), Map.entry("red_sand", "sand"),
            Map.entry("short_grass", "tallgrass"), Map.entry("fern", "tallgrass"),
            Map.entry("tall_grass", "double_plant"), Map.entry("large_fern", "double_plant"),
            Map.entry("dead_bush", "deadbush"), Map.entry("coarse_dirt", "dirt"),
            Map.entry("podzol", "dirt"), Map.entry("granite", "stone"),
            Map.entry("polished_granite", "stone"), Map.entry("diorite", "stone"),
            Map.entry("polished_diorite", "stone"), Map.entry("andesite", "stone"),
            Map.entry("polished_andesite", "stone"), Map.entry("deepslate", "stone"),
            Map.entry("cobbled_deepslate", "stone"), Map.entry("polished_deepslate", "stone"),
            Map.entry("deepslate_bricks", "stone"), Map.entry("cracked_deepslate_bricks", "stone"),
            Map.entry("deepslate_tiles", "stone"), Map.entry("cracked_deepslate_tiles", "stone"),
            Map.entry("chiseled_deepslate", "stone"), Map.entry("tuff", "stone"),
            Map.entry("calcite", "stone"), Map.entry("deepslate_iron_ore", "iron_ore"),
            Map.entry("deepslate_gold_ore", "gold_ore"), Map.entry("deepslate_coal_ore", "coal_ore"),
            Map.entry("deepslate_lapis_ore", "lapis_ore"), Map.entry("deepslate_diamond_ore", "diamond_ore"),
            Map.entry("deepslate_redstone_ore", "redstone_ore"), Map.entry("deepslate_emerald_ore", "emerald_ore"),
            Map.entry("copper_ore", "iron_ore"), Map.entry("deepslate_copper_ore", "iron_ore"));
    private LegacyMaterials() { }
    private static Map<String, LegacyMaterial> load() {
        try (var stream = LegacyMaterials.class.getResourceAsStream("/hbm/legacy-block-materials.json")) {
            if (stream == null) throw new IllegalStateException("Missing original material table");
            var type = new com.google.gson.reflect.TypeToken<Map<String, LegacyMaterial>>() {}.getType();
            return new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), type);
        } catch (java.io.IOException error) { throw new IllegalStateException(error); }
    }
    public static LegacyMaterial of(BlockState state) {
        return CACHE.computeIfAbsent(state,LegacyMaterials::resolve);
    }

    public static void clearCache(){CACHE.clear();}

    private static LegacyMaterial resolve(BlockState state) {
        if (state.getBlock() instanceof com.hbm.block.light.SourceBeamBlock) return MATERIALS.get("minecraft:air");
        if (state.getBlock() instanceof LegacyFluidBlock block) return block.material();
        String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        LegacyMaterial old = MATERIALS.get(id);
        // New deepslate HBM blocks retain their original ore's material contract.
        if (old == null && state.getBlock() instanceof com.hbm.block.ore.LegacyOreBlock ore)
            old = MATERIALS.get("hbm:" + ore.spec().id());
        if (old == null && id.startsWith("minecraft:")) {
            String path = id.substring(10), alias = MODERN_ALIASES.get(path);
            if (alias == null) alias = familyAlias(state, path);
            if (alias != null) old = MATERIALS.get("minecraft:" + alias);
        }
        if (old != null) {
            if (old.name().equals("water")) return LegacyMaterial.water;
            if (old.name().equals("lava")) return LegacyMaterial.lava;
            if (old.name().equals("portal")) return LegacyMaterial.portal;
            return old;
        }
        // No old semantics exist for blocks introduced after 1.7.10 or by another mod.
        return new LegacyMaterial(id, state.blocksMotion(), state.blocksMotion(), state.liquid(), state.canBeReplaced(), false);
    }

    private static String familyAlias(BlockState state, String path) {
        if (path.endsWith("_planks")) return "planks";
        if (state.is(BlockTags.LOGS)) return "log";
        if (state.is(BlockTags.LEAVES)) return "leaves";
        if (path.endsWith("_wool")) return "wool";
        if (path.endsWith("_carpet")) return "carpet";
        if (path.endsWith("_stained_glass_pane")) return "stained_glass_pane";
        if (path.endsWith("_stained_glass")) return "stained_glass";
        if (path.endsWith("_terracotta")) return "stained_hardened_clay";
        if (path.equals("terracotta")) return "hardened_clay";
        if (path.endsWith("_door")) return path.equals("iron_door") ? "iron_door" : "wooden_door";
        if (path.endsWith("_wall_sign")) return "wall_sign";
        if (path.endsWith("_sign")) return "standing_sign";
        return path.equals("sugar_cane") ? "reeds" : null;
    }

    @net.neoforged.fml.common.EventBusSubscriber(modid="hbm")
    public static final class Reload {
        @net.neoforged.bus.api.SubscribeEvent
        public static void tags(net.neoforged.neoforge.event.TagsUpdatedEvent event){clearCache();}
    }
}
