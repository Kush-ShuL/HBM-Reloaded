package com.hbm.network.rtty.logic;
import java.util.List;
import com.hbm.item.BasicBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class RadioLogicContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<RadioLogicBlock> BLOCK=BLOCKS.register("radio_torch_logic",RadioLogicBlock::new);public static final DeferredItem<BasicBlockItem> ITEM=ITEMS.register("radio_torch_logic",()->new BasicBlockItem(BLOCK.get(),new Item.Properties(),"BlockGenericTooltip","radio_torch_logic"));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RadioLogicEntity>> ENTITY=ENTITIES.register("radio_torch_logic",()->BlockEntityType.Builder.of(RadioLogicEntity::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(RadioLogicControl::register);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
 private RadioLogicContent(){}
}
