package com.hbm.block.lithiumstorage;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** One actual source block/normal ItemBlock, no second hazard or inventory subsystem. */
public final class LithiumStorageContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<LithiumStorageBlock> BLOCK_LITHIUM=BLOCKS.register("block_lithium",LithiumStorageBlock::new);
    public static final DeferredItem<LithiumStorageItem> BLOCK_LITHIUM_ITEM=ITEMS.register("block_lithium",()->new LithiumStorageItem(BLOCK_LITHIUM.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(BLOCK_LITHIUM_ITEM.get())):List.of();}
    private LithiumStorageContent(){}
}
