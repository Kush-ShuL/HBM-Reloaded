package com.hbm.block.bottlecap;

import com.hbm.block.ore.LegacyOreItem;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** One real source block/item identity, original six metadata variants and blockTab order. */
public final class BottleCapContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<BottleCapBlock> BLOCK=BLOCKS.register("block_cap",BottleCapBlock::new);
    public static final DeferredItem<LegacyOreItem> ITEM=ITEMS.register("block_cap",()->new LegacyOreItem(BLOCK.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?ITEM.get().creativeStacks():List.of();}
    private BottleCapContent(){}
}
