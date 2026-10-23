package com.hbm.block.toaster;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The actual three source decorative toaster subtypes; no BE, invented machine or new manufacture. */
public final class ToasterContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<ToasterBlock> BLOCK = BLOCKS.register("deco_toaster", ToasterBlock::new);
    public static final DeferredItem<ToasterItem> ITEM = ITEMS.register("deco_toaster", () -> new ToasterItem(BLOCK.get()));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("block") ? List.of(ToasterItem.stack(0), ToasterItem.stack(1), ToasterItem.stack(2)) : List.of(); }
    private ToasterContent() {}
}
