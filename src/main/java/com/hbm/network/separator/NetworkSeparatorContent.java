package com.hbm.network.separator;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class NetworkSeparatorContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<NetworkSeparatorBlock> BLOCK=BLOCKS.register("network_separator",NetworkSeparatorBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.register("network_separator",()->new BlockItem(BLOCK.get(),new Item.Properties()));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<NetworkSeparatorEntity>> ENTITY=ENTITIES.register("network_separator",()->BlockEntityType.Builder.of(NetworkSeparatorEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<NetworkSeparatorPort>> PORT=ENTITIES.register("network_separator_port",()->BlockEntityType.Builder.of(NetworkSeparatorPort::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(NetworkSeparatorControl::register);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private NetworkSeparatorContent(){}
}
