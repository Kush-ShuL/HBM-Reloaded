package com.hbm.block.cokestorage;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source coke variants have separate native block and item identities. */
public final class CokeStorageContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final List<DeferredBlock<CokeStorageBlock>> TYPES = new ArrayList<>();
    private static final List<DeferredItem<BlockItem>> ITEM_TYPES = new ArrayList<>();
    static {
        for (int type = 0; type < CokeStorageBlock.NAMES.size(); type++) {
            int fixedType = type;
            String id = "block_coke_" + CokeStorageBlock.NAMES.get(type);
            var block = BLOCKS.register(id, () -> new CokeStorageBlock(fixedType));
            TYPES.add(block);
            ITEM_TYPES.add(ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties())));
        }
    }

    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static CokeStorageBlock block(int type) { return TYPES.get(type).get(); }
    public static Item item(int type) { return ITEM_TYPES.get(type).get(); }
    public static boolean isCoke(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof CokeStorageBlock;
    }
    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("block") ? ITEM_TYPES.stream().map(item -> new ItemStack(item.get())).toList() : List.of();
    }
    private CokeStorageContent() {}
}
