package com.hbm.machine.poweredcondenser;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class PoweredCondenserContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<PoweredCondenserBlock> BLOCK=BLOCKS.register("machine_condenser_powered",PoweredCondenserBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("machine_condenser_powered",BLOCK);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PoweredCondenserEntity>> ENTITY=ENTITIES.register("powered_condenser",()->BlockEntityType.Builder.of(PoweredCondenserEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PoweredCondenserPort>> PORT=ENTITIES.register("powered_condenser_port",()->BlockEntityType.Builder.of(PoweredCondenserPort::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private PoweredCondenserContent(){}
}
