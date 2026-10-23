package com.hbm.machine.lpw2;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class Lpw2Content {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<Lpw2Block> BLOCK=BLOCKS.register("machine_lpw2",Lpw2Block::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<Lpw2Entity>> ENTITY=ENTITIES.register("machine_lpw2",()->BlockEntityType.Builder.of(Lpw2Entity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(ITEM.get()));}
    public static Map<String,Item> indexedItems(){return Map.of("machine_lpw2",ITEM.get());}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    private Lpw2Content(){}
}
