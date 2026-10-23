package com.hbm.machine.chemicalfactory;
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
/** No second chemical RecipeType/config/table: factory modules use the existing chemical plant recipe system. */
public final class ChemicalFactoryContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<ChemicalFactoryBlock> BLOCK=BLOCKS.register("machine_chemical_factory",ChemicalFactoryBlock::new);public static final DeferredItem<ChemicalFactoryItem> ITEM=ITEMS.register("machine_chemical_factory",()->new ChemicalFactoryItem(BLOCK.get()));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ChemicalFactoryEntity>> ENTITY=ENTITIES.register("chemical_factory",()->BlockEntityType.Builder.of(ChemicalFactoryEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ChemicalFactoryPort>> PORT=ENTITIES.register("chemical_factory_port",()->BlockEntityType.Builder.of(ChemicalFactoryPort::new,BLOCK.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<ChemicalFactoryMenu>> MENU=MENUS.register("chemical_factory",()->new MenuType<>((IContainerFactory<ChemicalFactoryMenu>)ChemicalFactoryMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(ChemicalFactorySelection::register);bus.addListener(ChemicalFactoryContent::capabilities);}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->machine.getContainerSize()==0?null:side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
 private ChemicalFactoryContent(){}
}
