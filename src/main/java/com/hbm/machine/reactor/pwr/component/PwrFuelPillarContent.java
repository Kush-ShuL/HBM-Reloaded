package com.hbm.machine.reactor.pwr.component;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Preserve the source block vs fuel-item namespace distinction with an explicit modern block-item alias. */
public final class PwrFuelPillarContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<PwrFuelPillarBlock> BLOCK=BLOCKS.register("pwr_fuel",PwrFuelPillarBlock::new);
    public static final DeferredItem<PwrFuelPillarItem> ITEM=ITEMS.register("pwr_fuel_block",()->new PwrFuelPillarItem(BLOCK.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}
    private PwrFuelPillarContent(){}
}
