package com.hbm.block.guidedisplay;

import java.util.List;
import java.util.Map;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual tile.book_guide is distinct from item.book_guide and its page system. */
public final class GuideDisplayContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<GuideDisplayBlock> BLOCK=BLOCKS.register("book_guide",GuideDisplayBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("book_guide",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static Runnable openWiki=()->{};
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("nuke")?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("book_guide",ITEM.get());}
    private GuideDisplayContent(){}
}
