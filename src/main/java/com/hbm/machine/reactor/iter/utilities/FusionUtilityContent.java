package com.hbm.machine.reactor.iter.utilities;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class FusionUtilityContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<FusionUtilityBlock> BOILER=BLOCKS.register("fusion_boiler",()->new FusionUtilityBlock(false)),CREATIVE=BLOCKS.register("fusion_klystron_creative",()->new FusionUtilityBlock(true));
 public static final DeferredItem<FusionUtilityItem> BOILER_ITEM=ITEMS.register("fusion_boiler",()->new FusionUtilityItem(false)),CREATIVE_ITEM=ITEMS.register("fusion_klystron_creative",()->new FusionUtilityItem(true));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FusionBoilerEntity>> BOILER_ENTITY=ENTITIES.register("fusion_boiler",()->BlockEntityType.Builder.of(FusionBoilerEntity::new,BOILER.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FusionBoilerPort>> BOILER_PORT=ENTITIES.register("fusion_boiler_port",()->BlockEntityType.Builder.of(FusionBoilerPort::new,BOILER.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CreativeKlystronEntity>> CREATIVE_ENTITY=ENTITIES.register("fusion_klystron_creative",()->BlockEntityType.Builder.of(CreativeKlystronEntity::new,CREATIVE.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(BOILER_ITEM.get()),new ItemStack(CREATIVE_ITEM.get())):List.of();}
 private FusionUtilityContent(){}
}
