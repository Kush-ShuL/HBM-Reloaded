package com.hbm.block.door;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SourceDoorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<SourceDoorBlock> METAL=BLOCKS.register("door_metal",()->new SourceDoorBlock(5,3));
    public static final DeferredBlock<SourceDoorBlock> OFFICE=BLOCKS.register("door_office",()->new SourceDoorBlock(10,6));
    public static final DeferredBlock<SourceDoorBlock> BUNKER=BLOCKS.register("door_bunker",()->new SourceDoorBlock(10,60));
    public static final DeferredBlock<SourceDoorBlock> RED=BLOCKS.register("door_red",()->new SourceDoorBlock(10,60));
    private static final List<DeferredBlock<SourceDoorBlock>> DOORS=List.of(METAL,OFFICE,BUNKER,RED);
    public static final List<DeferredItem<SourceDoorItem>> DOOR_ITEMS=DOORS.stream().map(block->ITEMS.register(block.getId().getPath(),()->new SourceDoorItem(block.get()))).toList();
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?DOOR_ITEMS.subList(0,3).stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private SourceDoorContent(){}
}
