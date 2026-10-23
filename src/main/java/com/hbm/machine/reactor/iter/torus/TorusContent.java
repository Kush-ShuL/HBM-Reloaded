package com.hbm.machine.reactor.iter.torus;
import java.util.*;
import com.hbm.item.EnumMultiItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
/** Unique real source three blocks/items, two core entities and inventory/HE/privatefluid proxy. */
public final class TorusContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<TorusBlock> BLOCK=BLOCKS.register("fusion_torus",TorusBlock::new);public static final DeferredItem<TorusItem> ITEM=ITEMS.register("fusion_torus",TorusItem::new);
 public static final DeferredBlock<TorusStruct> STRUCT=BLOCKS.register("struct_torus_core",TorusStruct::new);public static final DeferredItem<BlockItem> STRUCT_ITEM=ITEMS.register("struct_torus_core",()->new BlockItem(STRUCT.get(),new Item.Properties()));
 public static final DeferredBlock<FusionComponent> COMPONENT=BLOCKS.register("fusion_component",FusionComponent::new);public static final DeferredItem<FusionComponentItem> COMPONENT_ITEM=ITEMS.register("fusion_component",FusionComponentItem::new);
 public static final List<String> DRIVE_NAMES=List.of("flash_empty","disk_empty","flash_broken","disk_broken","flash_flightsim","flash_particlesim","disk_flightdata","disk_flightdata_processed","disk_orbitdata","disk_orbitdata_processed","klaus");
 public static final DeferredItem<EnumMultiItem> DRIVE=ITEMS.register("drive",()->new EnumMultiItem("drive",64,true,DRIVE_NAMES,java.util.stream.IntStream.range(0,DRIVE_NAMES.size()).boxed().toList()));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TorusEntity>> ENTITY=ENTITIES.register("fusion_torus",()->BlockEntityType.Builder.of(TorusEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TorusPort>> PORT=ENTITIES.register("fusion_torus_port",()->BlockEntityType.Builder.of(TorusPort::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TorusStructEntity>> STRUCT_ENTITY=ENTITIES.register("struct_torus_core",()->BlockEntityType.Builder.of(TorusStructEntity::new,STRUCT.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<TorusMenu>> MENU=MENUS.register("fusion_torus",()->new MenuType<>((IContainerFactory<TorusMenu>)TorusMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(TorusSelection::register);}
 public static List<ItemStack> creativeStacks(String tab){if(tab.equals("parts"))return DRIVE.get().creativeStacks();if(!tab.equals("machine"))return List.of();var out=new ArrayList<ItemStack>();out.add(new ItemStack(STRUCT_ITEM.get()));for(int i=0;i<4;i++)out.add(FusionComponent.stack(i,1));out.add(new ItemStack(ITEM.get()));return out;}
 private TorusContent(){}
}
