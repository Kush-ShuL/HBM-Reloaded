package com.hbm.machine.conveyorpartitioner;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class ConveyorPartitionerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<ConveyorPartitionerBlock> BLOCK=BLOCKS.register("crane_partitioner",ConveyorPartitionerBlock::new);
    public static final DeferredItem<ConveyorPartitionerItem> ITEM=ITEMS.register("crane_partitioner",()->new ConveyorPartitionerItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ConveyorPartitionerEntity>> ENTITY=ENTITIES.register("crane_partitioner",()->BlockEntityType.Builder.of(ConveyorPartitionerEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("crane_partitioner",ITEM.get());}
    private ConveyorPartitionerContent(){}
}
