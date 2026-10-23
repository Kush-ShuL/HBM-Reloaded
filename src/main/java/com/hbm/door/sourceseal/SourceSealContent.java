package com.hbm.door.sourceseal;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;

/** Sole existing basic-content seal_frame is reused, not duplicated into a new door engine. */
public final class SourceSealContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<SourceSealBlock> CONTROLLER=BLOCKS.register("seal_controller",SourceSealBlock::new);
    public static final DeferredBlock<SourceSealHatchBlock> HATCH=BLOCKS.register("seal_hatch",SourceSealHatchBlock::new);
    public static final DeferredItem<BlockItem> CONTROLLER_ITEM=ITEMS.registerSimpleBlockItem(CONTROLLER),HATCH_ITEM=ITEMS.registerSimpleBlockItem(HATCH);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SourceSealHatchEntity>> ENTITY=ENTITIES.register("seal_hatch",()->BlockEntityType.Builder.of(SourceSealHatchEntity::new,HATCH.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);NeoForge.EVENT_BUS.addListener(SourceSealActions::loaded);NeoForge.EVENT_BUS.addListener(SourceSealActions::tick);NeoForge.EVENT_BUS.addListener(SourceSealActions::unload);NeoForge.EVENT_BUS.addListener(SourceSealActions::stopped);}
    public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?List.of(new ItemStack(CONTROLLER_ITEM.get())):List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("seal_controller",CONTROLLER_ITEM.get(),"seal_hatch",HATCH_ITEM.get());}
    public static boolean frame(BlockState state){return state.is(CONTROLLER.get())||state.is(com.hbm.registry.HbmContent.block("seal_frame"));}
    private SourceSealContent(){}
}
