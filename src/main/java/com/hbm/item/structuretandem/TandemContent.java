package com.hbm.item.structuretandem;

import com.hbm.world.structure.NativeTemplateStructures;
import com.hbm.world.structure.tandem.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** One real authoring host, no creative tab, invented recipe or generic Jigsaw substitute. */
public final class TandemContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<StructureProcessorType<?>> PROCESSORS=DeferredRegister.create(Registries.STRUCTURE_PROCESSOR,"hbm");
    public static final DeferredBlock<TandemBlock> BLOCK=BLOCKS.register("wand_tandem",TandemBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("wand_tandem",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TandemEntity>> ENTITY=ENTITIES.register("wand_tandem",()->BlockEntityType.Builder.of(TandemEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<TandemStructureProcessor>> PROCESSOR=PROCESSORS.register("tandem_arm",()->()->TandemStructureProcessor.CODEC);
    public static void register(IEventBus bus){
        BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);PROCESSORS.register(bus);
        bus.addListener(TandemPackets::register);NativeTemplateStructures.addProcessor(TandemStructureProcessor.INSTANCE);TandemPlacement.register();
    }
    private TandemContent(){}
}
