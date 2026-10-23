package com.hbm.block.polarizedglass;

import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Single actual block/item; existing EnumPartType.GLASS_POLARIZED remains the unique lens ingredient. */
public final class PolarizedGlassContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<PolarizedGlassBlock> GLASS=BLOCKS.register("glass_polarized",PolarizedGlassBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("glass_polarized",()->new BlockItem(GLASS.get(),new Item.Properties()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private PolarizedGlassContent(){}
}
