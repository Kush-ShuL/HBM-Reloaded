package com.hbm.network.rtty.counter;
import java.util.List;
import com.hbm.item.BasicBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
public final class RadioCounterContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<RadioCounterBlock> BLOCK=BLOCKS.register("radio_torch_counter",RadioCounterBlock::new);public static final DeferredItem<BasicBlockItem> ITEM=ITEMS.register("radio_torch_counter",()->new BasicBlockItem(BLOCK.get(),new Item.Properties(),"BlockGenericTooltip","radio_torch_counter"));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RadioCounterEntity>> ENTITY=ENTITIES.register("radio_torch_counter",()->BlockEntityType.Builder.of(RadioCounterEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<RadioCounterMenu>> MENU=MENUS.register("radio_torch_counter",()->IMenuTypeExtension.create(RadioCounterMenu::new));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(RadioCounterControl::register);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
 private RadioCounterContent(){}
}
