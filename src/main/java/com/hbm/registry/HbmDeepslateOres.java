package com.hbm.registry;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.hbm.block.ore.DeepslateOreItem;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.OreSpec;
import com.hbm.block.ore.OutgassingOreBlock;
import com.hbm.worldgen.ore.OreWorldgen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class HbmDeepslateOres {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final Map<String, DeferredBlock<LegacyOreBlock>> BY_ORIGINAL = new LinkedHashMap<>();
    private static final Map<Block, String> ORIGINALS = new IdentityHashMap<>();
    private static final Map<String, DeferredItem<DeepslateOreItem>> ALL = new LinkedHashMap<>();
    private HbmDeepslateOres() { }
    public static void register(IEventBus bus, ModContainer container) {
        try (var stream = HbmDeepslateOres.class.getResourceAsStream("/hbm/deepslate-ores.json")) {
            if (stream == null) throw new IllegalStateException("Run tools/port_deepslate_ores.py");
            var content = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Content.class);
            for (Definition definition : content.blocks()) {
                var block = BLOCKS.<LegacyOreBlock>register(definition.id(), () -> {
                    var properties = definition.spec().properties().mapColor(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE);
                    LegacyOreBlock result = definition.spec().originalClass().equals("BlockOutgas")
                            ? new OutgassingOreBlock(definition.spec(), properties) : new LegacyOreBlock(definition.spec(), properties);
                    ORIGINALS.put(result, definition.source());
                    return result;
                });
                BY_ORIGINAL.put(definition.source(), block);
                ALL.put(definition.source(), ITEMS.register(definition.id(), () -> new DeepslateOreItem(block.get())));
            }
        } catch (java.io.IOException exception) { throw new IllegalStateException(exception); }
        BLOCKS.register(bus); ITEMS.register(bus); OreWorldgen.register(bus, container);

    }
    public static Block block(String originalSymbol) {
        if (originalSymbol.equals("ore_copper")) return Blocks.DEEPSLATE_COPPER_ORE;
        var block = BY_ORIGINAL.get(originalSymbol);
        if (block == null) throw new IllegalArgumentException("No original stone host for " + originalSymbol);
        return block.get();
    }
    public static boolean has(String originalSymbol) { return originalSymbol.equals("ore_copper") || BY_ORIGINAL.containsKey(originalSymbol); }
    public static boolean equivalent(BlockState state, String symbol) {
        return symbol.equals(ORIGINALS.get(state.getBlock())) || state.is(HbmContent.block(symbol));
    }
    public static Block forHost(BlockState previous, String symbol) { return ORIGINALS.containsKey(previous.getBlock()) ? block(symbol) : HbmContent.block(symbol); }
    public static List<ItemStack> creativeStacks(String tab) {
        if (!tab.equals("block")) return List.of();
        return java.util.stream.Stream.concat(ALL.values().stream().map(holder -> new ItemStack(holder.get())), java.util.stream.Stream.of(new ItemStack(Blocks.DEEPSLATE_COPPER_ORE))).toList();
    }
    private record Definition(String source, String id, OreSpec spec) { }
    private record Content(List<Definition> blocks) { }
}
