package com.hbm.item.structurelooteditor;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Original hidden authoring block. No source GUI/container, creative tab or manufacture exists. */
public final class LootEditorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<LootEditorBlock> BLOCK=BLOCKS.register("wand_loot",LootEditorBlock::new);
    public static final DeferredItem<LootEditorItem> ITEM=ITEMS.register("wand_loot",()->new LootEditorItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LootEditorEntity>> ENTITY=ENTITIES.register("wand_loot",()->BlockEntityType.Builder.of(LootEditorEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);LootEditorPlacement.register();}
    private LootEditorContent(){}
}
