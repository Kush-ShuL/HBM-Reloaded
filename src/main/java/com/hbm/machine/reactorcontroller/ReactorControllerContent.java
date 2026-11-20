package com.hbm.machine.reactorcontroller;

import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Still actually registered in X5846, but explicitly hidden from its creative tabs. */
public final class ReactorControllerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<ReactorControllerBlock> BLOCK=BLOCKS.register("machine_controller",ReactorControllerBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(ITEM.get()));}
    private ReactorControllerContent(){}
}
