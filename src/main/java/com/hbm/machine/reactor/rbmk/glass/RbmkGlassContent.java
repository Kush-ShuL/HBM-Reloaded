package com.hbm.machine.reactor.rbmk.glass;

import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class RbmkGlassContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<RbmkGlassBlock> BORON = BLOCKS.register("glass_boron", () -> new RbmkGlassBlock());
    public static final DeferredBlock<RbmkGlassBlock> LEAD = BLOCKS.register("glass_lead", () -> new RbmkGlassBlock());
    public static final DeferredItem<BlockItem> BORON_ITEM = ITEMS.registerSimpleBlockItem(BORON);
    public static final DeferredItem<BlockItem> LEAD_ITEM = ITEMS.registerSimpleBlockItem(LEAD);
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("machine") ? List.of(new ItemStack(BORON_ITEM.get()), new ItemStack(LEAD_ITEM.get())) : List.of(); }
    private RbmkGlassContent() { }
}
