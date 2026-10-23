package com.hbm.machine.pistoninserter;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Deprecated does not mean absent: source registers the real block and item, deliberately without a creative tab. */
public final class PistonInserterContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<PistonInserterBlock> BLOCK=BLOCKS.register("piston_inserter",PistonInserterBlock::new);
    public static final DeferredItem<PistonInserterItem> ITEM=ITEMS.register("piston_inserter",()->new PistonInserterItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PistonInserterEntity>> ENTITY=ENTITIES.register("piston_inserter",()->BlockEntityType.Builder.of(PistonInserterEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("piston_inserter",ITEM.get());}
    private PistonInserterContent(){}
}
