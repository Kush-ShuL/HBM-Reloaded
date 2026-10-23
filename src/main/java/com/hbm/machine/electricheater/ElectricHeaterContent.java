package com.hbm.machine.electricheater;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class ElectricHeaterContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<ElectricHeaterBlock> BLOCK=BLOCKS.register("heater_electric",ElectricHeaterBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.register("heater_electric",()->new ElectricHeaterBlockItem(BLOCK.get(),new Item.Properties()));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ElectricHeaterBlockEntity>> ENTITY=ENTITIES.register("heater_electric",()->BlockEntityType.Builder.of(ElectricHeaterBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ElectricHeaterProxy>> PROXY=ENTITIES.register("heater_electric_proxy",()->BlockEntityType.Builder.of(ElectricHeaterProxy::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private ElectricHeaterContent(){}
}
