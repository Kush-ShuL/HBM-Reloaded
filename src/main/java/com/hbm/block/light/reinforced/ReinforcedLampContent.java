package com.hbm.block.light.reinforced;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Both registered original variants, only the off host appears in the original block tab. */
public final class ReinforcedLampContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<ReinforcedLampBlock> OFF=BLOCKS.register("reinforced_lamp_off",()->new ReinforcedLampBlock(false));
    public static final DeferredBlock<ReinforcedLampBlock> ON=BLOCKS.register("reinforced_lamp_on",()->new ReinforcedLampBlock(true));
    public static final DeferredItem<ReinforcedLampItem> OFF_ITEM=ITEMS.register("reinforced_lamp_off",()->new ReinforcedLampItem(OFF.get()));
    public static final DeferredItem<ReinforcedLampItem> ON_ITEM=ITEMS.register("reinforced_lamp_on",()->new ReinforcedLampItem(ON.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(OFF_ITEM.get())):List.of();}
    private ReinforcedLampContent(){}
}
