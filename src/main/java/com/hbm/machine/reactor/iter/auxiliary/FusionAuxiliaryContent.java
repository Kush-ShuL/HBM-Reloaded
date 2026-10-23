package com.hbm.machine.reactor.iter.auxiliary;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class FusionAuxiliaryContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<FusionAuxiliaryBlock> COLLECTOR=BLOCKS.register("fusion_collector",()->new FusionAuxiliaryBlock(true)),COUPLER=BLOCKS.register("fusion_coupler",()->new FusionAuxiliaryBlock(false));
 public static final DeferredItem<FusionAuxiliaryItem> COLLECTOR_ITEM=ITEMS.register("fusion_collector",()->new FusionAuxiliaryItem(true)),COUPLER_ITEM=ITEMS.register("fusion_coupler",()->new FusionAuxiliaryItem(false));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FusionCollectorEntity>> COLLECTOR_ENTITY=ENTITIES.register("fusion_collector",()->BlockEntityType.Builder.of(FusionCollectorEntity::new,COLLECTOR.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FusionCouplerEntity>> COUPLER_ENTITY=ENTITIES.register("fusion_coupler",()->BlockEntityType.Builder.of(FusionCouplerEntity::new,COUPLER.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(COLLECTOR_ITEM.get()),new ItemStack(COUPLER_ITEM.get())):List.of();}
 private FusionAuxiliaryContent(){}
}
