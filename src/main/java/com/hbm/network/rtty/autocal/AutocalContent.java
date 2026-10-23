package com.hbm.network.rtty.autocal;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class AutocalContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");public static final DeferredBlock<AutocalBlock> BLOCK=BLOCKS.register("radio_autocal",AutocalBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.register("radio_autocal",()->new BlockItem(BLOCK.get(),new Item.Properties()));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AutocalEntity>> ENTITY=ENTITIES.register("radio_autocal",()->BlockEntityType.Builder.of(AutocalEntity::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){AutocalConfig.register();BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(AutocalControl::register);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private AutocalContent(){}
}
