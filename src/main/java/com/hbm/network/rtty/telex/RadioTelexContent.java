package com.hbm.network.rtty.telex;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class RadioTelexContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");public static final DeferredBlock<RadioTelexBlock> BLOCK=BLOCKS.register("radio_telex",RadioTelexBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.register("radio_telex",()->new BlockItem(BLOCK.get(),new Item.Properties()));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RadioTelexEntity>> ENTITY=ENTITIES.register("radio_telex",()->BlockEntityType.Builder.of(RadioTelexEntity::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(RadioTelexControl::register);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private RadioTelexContent(){}
}
