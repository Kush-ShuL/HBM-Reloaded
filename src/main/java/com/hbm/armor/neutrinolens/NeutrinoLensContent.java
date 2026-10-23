package com.hbm.armor.neutrinolens;

import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeutrinoLensContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    public static final DeferredItem<ItemModLens> LENS = ITEMS.register("neutrino_lens", ItemModLens::new);
    public static final DeferredBlock<PinkLogBlock> PINK_LOG = BLOCKS.register("pink_log", PinkLogBlock::new);
    public static final DeferredItem<BlockItem> PINK_LOG_ITEM = ITEMS.register("pink_log", () -> new BlockItem(PINK_LOG.get(), new Item.Properties()));
    public static void register(IEventBus bus) { ITEMS.register(bus); BLOCKS.register(bus); }
    // Source pink_log is hidden in creative and NEI; do not expose it as an extra viewer item.
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(LENS.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private NeutrinoLensContent() { }
}
