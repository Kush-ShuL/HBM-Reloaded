package com.hbm.machine.sourcevents;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Four real source hosts, no fabricated GUI/HE/fluid container or alternate cloud backend. */
public final class SourceVentContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<SourceVentBlock> CHLORINE=BLOCKS.register("vent_chlorine",()->new SourceVentBlock(SourceVentBlock.Kind.CHLORINE));
    public static final DeferredBlock<SourceVentBlock> CLOUD=BLOCKS.register("vent_cloud",()->new SourceVentBlock(SourceVentBlock.Kind.CLOUD));
    public static final DeferredBlock<SourceVentBlock> PINK=BLOCKS.register("vent_pink_cloud",()->new SourceVentBlock(SourceVentBlock.Kind.PINK));
    public static final DeferredBlock<SourceVentBlock> SEAL=BLOCKS.register("vent_chlorine_seal",()->new SourceVentBlock(SourceVentBlock.Kind.SEAL));
    public static final DeferredItem<BlockItem> CHLORINE_ITEM=ITEMS.registerSimpleBlockItem("vent_chlorine",CHLORINE);
    public static final DeferredItem<BlockItem> CLOUD_ITEM=ITEMS.registerSimpleBlockItem("vent_cloud",CLOUD);
    public static final DeferredItem<BlockItem> PINK_ITEM=ITEMS.registerSimpleBlockItem("vent_pink_cloud",PINK);
    public static final DeferredItem<BlockItem> SEAL_ITEM=ITEMS.registerSimpleBlockItem("vent_chlorine_seal",SEAL);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SourceVentEntity>> VENT_ENTITY=ENTITIES.register("source_vent",()->BlockEntityType.Builder.of(SourceVentEntity::new,CHLORINE.get(),CLOUD.get(),PINK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ChlorineSealEntity>> SEAL_ENTITY=ENTITIES.register("chlorine_seal",()->BlockEntityType.Builder.of(ChlorineSealEntity::new,SEAL.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(CHLORINE_ITEM.get()),new ItemStack(CLOUD_ITEM.get()),new ItemStack(PINK_ITEM.get()),new ItemStack(SEAL_ITEM.get())):List.of();}
    private SourceVentContent(){}
}
