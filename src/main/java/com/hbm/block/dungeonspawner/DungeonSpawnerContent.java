package com.hbm.block.dungeonspawner;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class DungeonSpawnerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<DungeonSpawnerBlock> BLOCK=BLOCKS.register("dungeon_spawner",DungeonSpawnerBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("dungeon_spawner",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DungeonSpawnerEntity>> ENTITY=ENTITIES.register("dungeon_spawner",()->BlockEntityType.Builder.of(DungeonSpawnerEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    // Source has no creative tab, recipe, GUI, special item renderer or inventory.
    private DungeonSpawnerContent(){ }
}
