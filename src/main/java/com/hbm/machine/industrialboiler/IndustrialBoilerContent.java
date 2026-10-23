package com.hbm.machine.industrialboiler;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class IndustrialBoilerContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<IndustrialBoilerBlock> BLOCK=BLOCKS.register("machine_industrial_boiler",IndustrialBoilerBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.register("machine_industrial_boiler",()->new IndustrialBoilerBlockItem(BLOCK.get(),new Item.Properties()));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<IndustrialBoilerBlockEntity>> ENTITY=ENTITIES.register("machine_industrial_boiler",()->BlockEntityType.Builder.of(IndustrialBoilerBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<IndustrialBoilerProxy>> PROXY=ENTITIES.register("machine_industrial_boiler_proxy",()->BlockEntityType.Builder.of(IndustrialBoilerProxy::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private IndustrialBoilerContent(){}
}
