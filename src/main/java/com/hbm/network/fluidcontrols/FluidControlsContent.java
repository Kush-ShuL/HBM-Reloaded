package com.hbm.network.fluidcontrols;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Actual redstone valve, old hidden pump and three-cell flow-control pump. No standard fluid or HE capability. */
public final class FluidControlsContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<FluidSwitchBlock> SWITCH=BLOCKS.register("fluid_switch",FluidSwitchBlock::new);
 public static final DeferredBlock<FluidPumpBlock> OLD=BLOCKS.register("fluid_pump",FluidPumpBlock::new);
 public static final DeferredBlock<FlowPumpBlock> FLOW=BLOCKS.register("flow_control_pump",FlowPumpBlock::new);
 public static final DeferredItem<BlockItem> SWITCH_ITEM=ITEMS.registerSimpleBlockItem("fluid_switch",SWITCH),OLD_ITEM=ITEMS.registerSimpleBlockItem("fluid_pump",OLD),FLOW_ITEM=ITEMS.registerSimpleBlockItem("flow_control_pump",FLOW);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FluidSwitchEntity>> SWITCH_ENTITY=ENTITIES.register("fluid_switch",()->BlockEntityType.Builder.of(FluidSwitchEntity::new,SWITCH.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PumpEntity>> OLD_ENTITY=ENTITIES.register("fluid_pump",()->BlockEntityType.Builder.of((p,s)->new PumpEntity(true,p,s),OLD.get()).build(null)),FLOW_ENTITY=ENTITIES.register("flow_control_pump",()->BlockEntityType.Builder.of((p,s)->new PumpEntity(false,p,s),FLOW.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PumpPort>> PORT=ENTITIES.register("flow_control_pump_port",()->BlockEntityType.Builder.of(PumpPort::new,FLOW.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(FluidControlsPackets::register);}
 public static Map<String,Item> indexedItems(){return Map.of("fluid_switch",SWITCH_ITEM.get(),"fluid_pump",OLD_ITEM.get(),"flow_control_pump",FLOW_ITEM.get());}
 public static List<ItemStack> allStacks(){return indexedItems().values().stream().map(ItemStack::new).toList();}
 public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?List.of(new ItemStack(SWITCH_ITEM.get()),new ItemStack(FLOW_ITEM.get())):List.of();}
 private FluidControlsContent(){}
}
