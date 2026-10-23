package com.hbm.block.sourceslab;

import com.hbm.block.masonry.LegacyMultiSlabBlock;
import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** The two real BlockMultiSlabMeta materials and the separately registered pink slab pair. */
public final class SourceSlabsContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<SourceStoneSlabBlock> STONE = BLOCKS.register("stones_slab", () -> new SourceStoneSlabBlock(false, () -> SourceSlabsContent.STONE.get(), () -> SourceSlabsContent.STONE_DOUBLE.get()));
    public static final DeferredBlock<SourceStoneSlabBlock> STONE_DOUBLE = BLOCKS.register("stones_double_slab", () -> new SourceStoneSlabBlock(true, () -> SourceSlabsContent.STONE.get(), () -> SourceSlabsContent.STONE_DOUBLE.get()));
    public static final DeferredBlock<SourcePinkSlabBlock> PINK = BLOCKS.register("pink_slab", () -> new SourcePinkSlabBlock(false));
    public static final DeferredBlock<SourcePinkSlabBlock> PINK_DOUBLE = BLOCKS.register("pink_double_slab", () -> new SourcePinkSlabBlock(true));
    public static final DeferredItem<SourceStoneSlabItem> STONE_ITEM = ITEMS.register("stones_slab", () -> new SourceStoneSlabItem(STONE.get()));
    public static final DeferredItem<SourceStoneSlabItem> STONE_DOUBLE_ITEM = ITEMS.register("stones_double_slab", () -> new SourceStoneSlabItem(STONE_DOUBLE.get()));
    public static final DeferredItem<BlockItem> PINK_ITEM = ITEMS.register("pink_slab", () -> new BlockItem(PINK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> PINK_DOUBLE_ITEM = ITEMS.register("pink_double_slab", () -> new BlockItem(PINK_DOUBLE.get(), new Item.Properties()));

    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) {
        // BlockMultiSlab only lists its single registry; both pink registrations have a null source tab.
        return tab.equals("block") ? List.of(STONE.get().stack(0, 1), STONE.get().stack(1, 1)) : List.of();
    }
    public static List<ItemStack> indexedItems() {
        return List.of(STONE.get().stack(0, 1), STONE.get().stack(1, 1),
            new ItemStack(STONE_DOUBLE_ITEM.get()), new ItemStack(PINK_ITEM.get()), new ItemStack(PINK_DOUBLE_ITEM.get()));
    }
    public static List<BlockItem> stoneItems() { return List.of(STONE_ITEM.get(), STONE_DOUBLE_ITEM.get()); }
    private SourceSlabsContent() {}
}
