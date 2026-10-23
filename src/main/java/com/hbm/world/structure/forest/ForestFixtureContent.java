package com.hbm.world.structure.forest;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** True original fixtures required by forest_post and the existing source templates. */
public final class ForestFixtureContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<ForestFixtureBlock> TAPE=BLOCKS.register("tape_recorder",()->new ForestFixtureBlock(false));
 public static final DeferredBlock<ForestFixtureBlock> POLES=BLOCKS.register("steel_poles",()->new ForestFixtureBlock(true));
 public static final DeferredBlock<PoleReceiverBlock> RECEIVER=BLOCKS.register("pole_satellite_receiver",PoleReceiverBlock::new);
 public static final DeferredItem<BlockItem> TAPE_ITEM=ITEMS.registerSimpleBlockItem(TAPE),POLES_ITEM=ITEMS.registerSimpleBlockItem(POLES),RECEIVER_ITEM=ITEMS.registerSimpleBlockItem(RECEIVER);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PoleReceiverBlockEntity>> RECEIVER_ENTITY=ENTITIES.register("deco_pole_satellite_receiver",()->BlockEntityType.Builder.of(PoleReceiverBlockEntity::new,RECEIVER.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(TAPE_ITEM.get()),new ItemStack(POLES_ITEM.get()),new ItemStack(RECEIVER_ITEM.get())):List.of();}
 private ForestFixtureContent(){}
}
