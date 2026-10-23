package com.hbm.door.slidingblast;

import com.hbm.door.generic.*;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual sliding-blast generic-door declaration; reuses its one core/proxy/lock/redstone/packet backend. */
public final class SlidingBlastDoorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<GenericDoorBlock> BLOCK=BLOCKS.register("sliding_blast_door",()->new GenericDoorBlock(GenericDoorDeclaration.SLIDE_DOOR));
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("sliding_blast_door",BLOCK);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("sliding_blast_door",ITEM.get());}
    private SlidingBlastDoorContent(){}
}
