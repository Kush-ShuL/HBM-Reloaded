package com.hbm.world.structure.logic;

import com.hbm.world.structure.NativeTemplateStructures;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Three genuine source hosts; no random-piece or jigsaw replacement is registered here. */
public final class WandLogicContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, "hbm");
    public static final DeferredBlock<WandLogicBlock> WAND = BLOCKS.register("wand_logic", () -> new WandLogicBlock());
    public static final DeferredBlock<LogicBlock> VISIBLE = BLOCKS.register("logic_block", () -> new LogicBlock());
    public static final DeferredBlock<InvisibleLogicBlock> INVISIBLE = BLOCKS.register("logic_block_invis", () -> new InvisibleLogicBlock());
    public static final DeferredItem<WandLogicItem> WAND_ITEM = ITEMS.register("wand_logic", () -> new WandLogicItem(WAND.get()));
    public static final DeferredItem<BlockItem> VISIBLE_ITEM = ITEMS.register("logic_block", () -> new BlockItem(VISIBLE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> INVISIBLE_ITEM = ITEMS.register("logic_block_invis", () -> new BlockItem(INVISIBLE.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LogicAuthoring>> WAND_ENTITY = ENTITIES.register("wand_logic", () -> BlockEntityType.Builder.of(LogicAuthoring::new, WAND.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LogicRuntime>> RUNTIME_ENTITY = ENTITIES.register("logic_block", () -> BlockEntityType.Builder.of(LogicRuntime::new, VISIBLE.get(), INVISIBLE.get()).build(null));
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<LogicStructureProcessor>> SOURCE_TRANSFORM = PROCESSORS.register("wand_logic_transform", () -> () -> LogicStructureProcessor.CODEC);
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); PROCESSORS.register(bus);
        NativeTemplateStructures.addProcessor(LogicStructureProcessor.INSTANCE);
        // Source did not assign any of the three hosts a creative tab or crafting recipe.
    }
    private WandLogicContent() {}
}
