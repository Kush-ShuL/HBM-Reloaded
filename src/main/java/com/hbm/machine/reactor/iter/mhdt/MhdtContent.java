package com.hbm.machine.reactor.iter.mhdt;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class MhdtContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<MhdtBlock> BLOCK=BLOCKS.register("fusion_mhdt",MhdtBlock::new);public static final DeferredItem<MhdtItem> ITEM=ITEMS.register("fusion_mhdt",MhdtItem::new);public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MhdtEntity>> ENTITY=ENTITIES.register("fusion_mhdt",()->BlockEntityType.Builder.of(MhdtEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MhdtPort>> PORT=ENTITIES.register("fusion_mhdt_port",()->BlockEntityType.Builder.of(MhdtPort::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
 private MhdtContent(){}
}
