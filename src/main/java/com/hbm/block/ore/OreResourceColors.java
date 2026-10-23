package com.hbm.block.ore;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonParser;
import com.hbm.hazard.HbmHazards;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;

/** Original item-image averages (including transparent RGB), then integer amplification. */
public final class OreResourceColors implements ResourceManagerReloadListener {
    public static final OreResourceColors INSTANCE = new OreResourceColors();
    private static Map<String, Integer> items = Map.of(), blocks = Map.of();
    private OreResourceColors() { }
    @Override public void onResourceManagerReload(ResourceManager resources) {
        try (var reader = new InputStreamReader(resources.open(ResourceLocation.fromNamespaceAndPath("hbm", "legacy_ore/colors.json")), StandardCharsets.UTF_8)) {
            var root = JsonParser.parseReader(reader).getAsJsonObject();
            Map<String, Integer> loadedItems = new HashMap<>(), loadedBlocks = new HashMap<>();
            root.getAsJsonObject("items").entrySet().forEach(entry -> loadedItems.put(entry.getKey(), entry.getValue().getAsInt()));
            root.getAsJsonObject("blocks").entrySet().forEach(entry -> loadedBlocks.put(entry.getKey(), entry.getValue().getAsInt()));
            items = Map.copyOf(loadedItems); blocks = Map.copyOf(loadedBlocks);
        } catch (IOException error) { throw new IllegalStateException("Cannot load exact legacy resource colors", error); }
    }
    public static int color(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if (stack.getItem() instanceof BlockItem block) {
            Integer original = blocks.get(id);
            return original == null ? block.getBlock().defaultBlockState().getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).col : original;
        }
        int metadata = stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue());
        Integer color = items.get(id + "#" + metadata);
        return color == null ? items.getOrDefault(id, 0xFFFFFF) : color;
    }
}
