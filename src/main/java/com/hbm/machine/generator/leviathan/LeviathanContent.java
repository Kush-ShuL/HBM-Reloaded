package com.hbm.machine.generator.leviathan;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class LeviathanContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<LeviathanBlock> BLOCK=BLOCKS.register("machine_chungus",LeviathanBlock::new);public static final DeferredItem<LeviathanItem> ITEM=ITEMS.register("machine_chungus",LeviathanItem::new);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LeviathanEntity>> ENTITY=ENTITIES.register("machine_chungus",()->BlockEntityType.Builder.of(LeviathanEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LeviathanProxy>> PROXY=ENTITIES.register("machine_chungus_port",()->BlockEntityType.Builder.of(LeviathanProxy::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){LeviathanConfig.load();BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
 private LeviathanContent(){}
}
