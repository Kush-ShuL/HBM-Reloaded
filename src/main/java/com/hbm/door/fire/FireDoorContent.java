package com.hbm.door.fire;

import com.hbm.door.generic.*;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual fourth generic-door declaration; reuses its one core/proxy/lock/redstone/packet backend. */
public final class FireDoorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<GenericDoorBlock> BLOCK=BLOCKS.register("fire_door",()->new GenericDoorBlock(GenericDoorDeclaration.FIRE_DOOR));
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("fire_door",BLOCK);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("fire_door",ITEM.get());}
    private FireDoorContent(){}
}
