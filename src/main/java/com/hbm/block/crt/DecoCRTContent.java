package com.hbm.block.crt;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DecoCRTContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<DecoCRTBlock> BLOCK = BLOCKS.register("deco_crt", () -> new DecoCRTBlock());
    public static final DeferredItem<DecoCRTItem> ITEM = ITEMS.register("deco_crt", () -> new DecoCRTItem(BLOCK.get()));
    private DecoCRTContent() {}
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<ItemStack> variants() { return List.of(DecoCRTItem.stack(0), DecoCRTItem.stack(1), DecoCRTItem.stack(2), DecoCRTItem.stack(3)); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("block") ? variants() : List.of(); }
}
