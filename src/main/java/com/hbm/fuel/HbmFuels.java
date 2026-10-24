package com.hbm.fuel;

import com.google.gson.Gson;
import com.hbm.HbmReloaded;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmItemTags;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

/** Source FuelHandler first-match rules and its item/metadata-only burn cache. */
@EventBusSubscriber(modid = HbmReloaded.MOD_ID)
public final class HbmFuels {
    private static final Spec SPEC = load();
    private static final Map<CacheKey, Integer> BURN_CACHE = new HashMap<>();

    private HbmFuels() {}

    @SubscribeEvent
    public static void burnTime(FurnaceFuelBurnTimeEvent event) {
        int original = getBurnTime(event.getItemStack());
        if (original <= 0) return;
        event.setBurnTime(Math.max(original, event.getBurnTime()));
        // FML combined registered fuel handlers by maximum, rather than stopping
        // after the first mod. NeoForge's setter cancels automatically.
        event.setCanceled(false);
    }

    /** Item equality is tested before raw metadata exactly as in X5846. */
    public static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        String item = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        int metadata = metadata(stack);
        for (Rule rule : SPEC.rules()) {
            if (rule.item().equals(item) && (rule.metadata() == null || rule.metadata() == metadata)) return rule.burnTime();
        }
        return 0;
    }

    /** The legacy cache deliberately ignores stack size and custom NBT. */
    public static int getBurnTimeFromCache(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        CacheKey key = new CacheKey(stack.getItem(), metadata(stack));
        return BURN_CACHE.computeIfAbsent(key, ignored -> stack.getBurnTime(RecipeType.SMELTING));
    }

    static boolean matches(ItemStack stack, String originalSymbol) {
        if (stack == null || stack.isEmpty()) return false;
        String item = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return SPEC.rules().stream().anyMatch(rule -> rule.legacySymbol().equals("ModItems." + originalSymbol) && rule.item().equals(item));
    }

    /** Current native tags, sorted by the sole shared source-selection priority. */
    public static List<String> tags(ItemStack stack) { return HbmItemTags.tags(stack); }
    public static boolean materialIs(String tag, String material) {
        String name=HbmItemTags.materialName(tag);
        return java.util.Arrays.asList(name.split("_")).contains(material);
    }
    public static boolean isCoalLikeTag(String tag) { return materialIs(tag,"coke") || materialIs(tag,"coal") || materialIs(tag,"lignite"); }
    public static boolean isLogTag(String tag) { return tag.equals("minecraft:logs") || HbmItemTags.category(tag).equals("logs") || tag.equals("hbm:pink_wood/logs"); }
    public static boolean isWoodTag(String tag) {
        String name=HbmItemTags.materialName(tag);
        return tag.equals("minecraft:planks") || name.startsWith("wooden_") || materialIs(tag,"wood") || materialIs(tag,"wooden");
    }
    public static boolean isSaplingTag(String tag) { return tag.equals("minecraft:saplings"); }
    public static boolean isOreTag(String tag) { return tag.equals("c:ores") || tag.startsWith("c:ores/") || tag.startsWith("hbm:ores_in_ground/"); }

    private static int metadata(ItemStack stack) {
        return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue());
    }

    private static Spec load() {
        try (var stream = HbmFuels.class.getResourceAsStream("/hbm/fuels.json")) {
            if (stream == null) throw new IllegalStateException("Missing fuels.json: run tools/port_fuels.py");
            return new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Spec.class);
        } catch (java.io.IOException error) {
            throw new ExceptionInInitializerError(error);
        }
    }

    private record Spec(List<Rule> rules) {}
    private record Rule(String item, Integer metadata, int burnTime, int priority, String legacySymbol) {}
    private record CacheKey(Item item, int metadata) {}
}
