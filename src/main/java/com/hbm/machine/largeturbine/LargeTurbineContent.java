package com.hbm.machine.largeturbine;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class LargeTurbineContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<LargeTurbineBlock> BLOCK=BLOCKS.register("machine_large_turbine",LargeTurbineBlock::new);public static final DeferredItem<LargeTurbineItem> ITEM=ITEMS.register("machine_large_turbine",()->new LargeTurbineItem(BLOCK.get()));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LargeTurbineEntity>> ENTITY=ENTITIES.register("large_turbine_legacy",()->BlockEntityType.Builder.of(LargeTurbineEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LargeTurbinePort>> PORT=ENTITIES.register("large_turbine_legacy_port",()->BlockEntityType.Builder.of(LargeTurbinePort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<LargeTurbineMenu>> MENU=MENUS.register("large_turbine_legacy",()->new MenuType<>((IContainerFactory<LargeTurbineMenu>)LargeTurbineMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener((RegisterCapabilitiesEvent event)->event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side)));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private LargeTurbineContent(){}
}
