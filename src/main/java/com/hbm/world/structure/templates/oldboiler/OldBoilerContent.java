package com.hbm.world.structure.templates.oldboiler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** One actual hidden block and its real registered ItemBlock; no source on variant or block entity exists. */
public final class OldBoilerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<OldBoilerBlock> BLOCK=BLOCKS.register("machine_boiler_off",OldBoilerBlock::new);
    public static final DeferredItem<OldBoilerItem> ITEM=ITEMS.register("machine_boiler_off",()->new OldBoilerItem(BLOCK.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    private OldBoilerContent(){}
}
