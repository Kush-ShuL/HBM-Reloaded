package com.hbm.block.cokestorage;

import com.hbm.block.ore.LegacyOreItem;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Original single ItemBlockBase identity, three metadata variants and source blockTab order. */
public final class CokeStorageContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<CokeStorageBlock> BLOCK_COKE=BLOCKS.register("block_coke",CokeStorageBlock::new);
    public static final DeferredItem<LegacyOreItem> BLOCK_COKE_ITEM=ITEMS.register("block_coke",()->new LegacyOreItem(BLOCK_COKE.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?BLOCK_COKE_ITEM.get().creativeStacks():List.of();}
    private CokeStorageContent(){}
}
