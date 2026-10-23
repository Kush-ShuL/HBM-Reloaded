package com.hbm.registry;

import com.google.gson.Gson;
import com.hbm.block.LegacyStairsBlock;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class HbmStairs {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private HbmStairs() {}

    public static void register(IEventBus bus) {
        try (var stream = HbmStairs.class.getResourceAsStream("/hbm/stairs.json")) {
            if (stream == null) throw new IllegalStateException("Run tools/port_stairs.py");
            var content = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Content.class);
            for (Spec spec : content.blocks()) {
                var block = BLOCKS.register(spec.id(), () -> {
                    var base = HbmContent.block(spec.base());
                    var properties = BlockBehaviour.Properties.ofFullCopy(base).noOcclusion().lightLevel(state -> 0)
                            .isValidSpawn((state, level, pos, type) -> state.isFaceSturdy(level, pos, Direction.UP));
                    return new LegacyStairsBlock(base.defaultBlockState(), properties);
                });
                ENTRIES.add(new Entry(ITEMS.registerSimpleBlockItem(spec.id(), block), spec.tab()));
            }
        } catch (java.io.IOException error) {
            throw new IllegalStateException("Cannot load original stairs", error);
        }
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    public static List<ItemStack> creativeStacks(String tab) {
        return ENTRIES.stream().filter(row -> tab.equals(row.tab())).map(row -> new ItemStack(row.item().get())).toList();
    }

    private record Content(List<Spec> blocks) {}
    private record Spec(String id, String symbol, String base, String tab) {}
    private record Entry(DeferredItem<?> item, String tab) {}
}
