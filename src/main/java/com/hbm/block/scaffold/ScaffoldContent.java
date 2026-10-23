package com.hbm.block.scaffold;

import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class ScaffoldContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<SteelScaffoldBlock> BLOCK = BLOCKS.register("steel_scaffold", SteelScaffoldBlock::new);
    public static final DeferredItem<SteelScaffoldItem> ITEM = ITEMS.register("steel_scaffold", () -> new SteelScaffoldItem(BLOCK.get()));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<ItemStack> variants() { return IntStream.range(0, 4).mapToObj(color -> BLOCK.get().stack(color, 1)).toList(); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("block") ? variants() : List.of(); }
    private ScaffoldContent() {}
}
