package com.hbm.network.rtty.ror;
import java.util.List;
import com.hbm.item.BasicBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class RadioRorContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<RadioRorBlock> READER=BLOCKS.register("radio_torch_reader",()->new RadioRorBlock(true)),CONTROLLER=BLOCKS.register("radio_torch_controller",()->new RadioRorBlock(false));public static final DeferredItem<BasicBlockItem> READER_ITEM=item("reader",READER),CONTROLLER_ITEM=item("controller",CONTROLLER);public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RadioReaderEntity>> READER_ENTITY=ENTITIES.register("radio_torch_reader",()->BlockEntityType.Builder.of(RadioReaderEntity::new,READER.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RadioControllerEntity>> CONTROLLER_ENTITY=ENTITIES.register("radio_torch_controller",()->BlockEntityType.Builder.of(RadioControllerEntity::new,CONTROLLER.get()).build(null));
 private static DeferredItem<BasicBlockItem> item(String suffix,DeferredBlock<RadioRorBlock> block){String name="radio_torch_"+suffix;return ITEMS.register(name,()->new BasicBlockItem(block.get(),new Item.Properties(),"BlockGenericTooltip",name));}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(RadioRorControl::register);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(READER_ITEM.get()),new ItemStack(CONTROLLER_ITEM.get())):List.of();}
 private RadioRorContent(){}
}
