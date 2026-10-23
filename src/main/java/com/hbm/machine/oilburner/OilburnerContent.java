package com.hbm.machine.oilburner;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class OilburnerContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<OilburnerBlock> BLOCK=BLOCKS.register("heater_oilburner",OilburnerBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.register("heater_oilburner",()->new OilburnerBlockItem(BLOCK.get(),new Item.Properties()));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<OilburnerBlockEntity>> ENTITY=ENTITIES.register("heater_oilburner",()->BlockEntityType.Builder.of(OilburnerBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<OilburnerDummy>> DUMMY=ENTITIES.register("heater_oilburner_dummy",()->BlockEntityType.Builder.of(OilburnerDummy::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<OilburnerFluidPort>> FLUID_PORT=ENTITIES.register("heater_oilburner_fluid_port",()->BlockEntityType.Builder.of(OilburnerFluidPort::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<OilburnerHeatPort>> HEAT_PORT=ENTITIES.register("heater_oilburner_heat_port",()->BlockEntityType.Builder.of(OilburnerHeatPort::new,BLOCK.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<OilburnerMenu>> MENU=MENUS.register("heater_oilburner",()->new MenuType<>((IContainerFactory<OilburnerMenu>)OilburnerMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(OilburnerControl::register);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private OilburnerContent(){}
}
