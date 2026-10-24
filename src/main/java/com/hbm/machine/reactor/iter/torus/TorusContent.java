package com.hbm.machine.reactor.iter.torus;
import java.util.*;
import com.hbm.satellite.SatelliteDriveType;
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
 public static final DeferredBlock<FusionComponent> COMPONENT=BLOCKS.register("fusion_component",FusionComponent::new);private static final List<DeferredItem<FusionComponentItem>> COMPONENT_ITEMS=registerComponents();
 private static List<DeferredItem<FusionComponentItem>> registerComponents(){var items=new ArrayList<DeferredItem<FusionComponentItem>>();var names=List.of("bscco","bscco_welded","blanket","motor");for(int material=0;material<4;material++){int fixed=material;items.add(ITEMS.register("fusion_component_"+names.get(material),()->new FusionComponentItem(fixed)));}return List.copyOf(items);}
 public static FusionComponentItem componentItem(int material){return COMPONENT_ITEMS.get(material).get();}
 public static List<ItemStack> componentStacks(){return COMPONENT_ITEMS.stream().map(item->new ItemStack(item.get())).toList();}
 private static final List<DeferredItem<DriveItem>> DRIVES=registerDrives();
 private static List<DeferredItem<DriveItem>> registerDrives(){var drives=new ArrayList<DeferredItem<DriveItem>>();for(var type:SatelliteDriveType.values())drives.add(ITEMS.register("drive_"+type.name().toLowerCase(Locale.ROOT),()->new DriveItem(type)));return List.copyOf(drives);}
 public static ItemStack driveStack(SatelliteDriveType type){return new ItemStack(DRIVES.get(type.ordinal()).get());}
 public static List<ItemStack> driveStacks(){return DRIVES.stream().map(item->new ItemStack(item.get())).toList();}
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TorusEntity>> ENTITY=ENTITIES.register("fusion_torus",()->BlockEntityType.Builder.of(TorusEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TorusPort>> PORT=ENTITIES.register("fusion_torus_port",()->BlockEntityType.Builder.of(TorusPort::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TorusStructEntity>> STRUCT_ENTITY=ENTITIES.register("struct_torus_core",()->BlockEntityType.Builder.of(TorusStructEntity::new,STRUCT.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<TorusMenu>> MENU=MENUS.register("fusion_torus",()->new MenuType<>((IContainerFactory<TorusMenu>)TorusMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(TorusSelection::register);}
 public static List<ItemStack> creativeStacks(String tab){if(tab.equals("parts"))return driveStacks();if(!tab.equals("machine"))return List.of();var out=new ArrayList<ItemStack>();out.add(new ItemStack(STRUCT_ITEM.get()));for(int i=0;i<4;i++)out.add(FusionComponent.stack(i,1));out.add(new ItemStack(ITEM.get()));return out;}
 private TorusContent(){}
}
