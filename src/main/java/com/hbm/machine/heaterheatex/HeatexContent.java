package com.hbm.machine.heaterheatex;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class HeatexContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<HeatexBlock> BLOCK=BLOCKS.register("heater_heatex",HeatexBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.register("heater_heatex",()->new HeatexBlockItem(BLOCK.get(),new Item.Properties()));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HeatexBlockEntity>> ENTITY=ENTITIES.register("heater_heatex",()->BlockEntityType.Builder.of(HeatexBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HeatexProxy>> PROXY=ENTITIES.register("heater_heatex_proxy",()->BlockEntityType.Builder.of(HeatexProxy::new,BLOCK.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<HeatexMenu>> MENU=MENUS.register("heater_heatex",()->new MenuType<>((IContainerFactory<HeatexMenu>)HeatexMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(HeatexControl::register);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private HeatexContent(){}
}
