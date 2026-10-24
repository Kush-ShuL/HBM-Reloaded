package com.hbm.network.cargo;
import com.hbm.item.BasicBlockItem;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
public final class CargoDroneContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 private static final Map<DroneItem.Kind,DeferredItem<DroneItem>> DRONES=registerDrones();
 private static Map<DroneItem.Kind,DeferredItem<DroneItem>> registerDrones(){var drones=new EnumMap<DroneItem.Kind,DeferredItem<DroneItem>>(DroneItem.Kind.class);for(var kind:DroneItem.Kind.values())drones.put(kind,ITEMS.register(kind.id(),()->new DroneItem(kind)));return Collections.unmodifiableMap(drones);}
 public static DroneItem drone(DroneItem.Kind kind){return DRONES.get(kind).get();}
 public static ItemStack droneStack(DroneItem.Kind kind){return new ItemStack(drone(kind));}public static final DeferredItem<DroneLinkerItem> LINKER=ITEMS.register("drone_linker",DroneLinkerItem::new);
 public static final DeferredBlock<DroneWaypointBlock> WAYPOINT=BLOCKS.register("drone_waypoint",DroneWaypointBlock::new);public static final DeferredBlock<DroneCrateBlock> CRATE=BLOCKS.register("drone_crate",DroneCrateBlock::new);
 public static final DeferredItem<BasicBlockItem> WAYPOINT_ITEM=ITEMS.register("drone_waypoint",()->new BasicBlockItem(WAYPOINT.get(),new Item.Properties(),"BlockGenericTooltip","drone_waypoint")),CRATE_ITEM=ITEMS.register("drone_crate",()->new BasicBlockItem(CRATE.get(),new Item.Properties(),"BlockGenericTooltip","drone_crate"));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DroneWaypointEntity>> WAYPOINT_ENTITY=BLOCK_ENTITIES.register("drone_waypoint",()->BlockEntityType.Builder.of(DroneWaypointEntity::new,WAYPOINT.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DroneCrateEntity>> CRATE_ENTITY=BLOCK_ENTITIES.register("drone_crate",()->BlockEntityType.Builder.of(DroneCrateEntity::new,CRATE.get()).build(null));
 public static final DeferredHolder<EntityType<?>,EntityType<PatrolDrone>> PATROL_ENTITY=ENTITIES.register("entity_delivery_drone",()->EntityType.Builder.<PatrolDrone>of(PatrolDrone::new,MobCategory.MISC).sized(.75F,.75F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_delivery_drone"));
 public static final DeferredHolder<MenuType<?>,MenuType<DroneCrateMenu>> CRATE_MENU=MENUS.register("drone_crate",()->new MenuType<>((IContainerFactory<DroneCrateMenu>)DroneCrateMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BLOCK_ENTITIES.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(CargoCrateControl::register);bus.addListener(CargoEffectPacket::register);bus.addListener(CargoDroneContent::capabilities);bus.addListener(CargoDroneContent::tickets);}
 private static void tickets(RegisterTicketControllersEvent event){event.register(PatrolDrone.TICKETS);}private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,CRATE_ENTITY.get(),(crate,side)->side==null?new InvWrapper(crate):new SidedInvWrapper(crate,side));}
 public static List<ItemStack> creativeStacks(String tab){if(tab.equals("machine")){var list=new ArrayList<ItemStack>();for(var kind:DroneItem.Kind.values())if(kind!=DroneItem.Kind.REQUEST)list.add(droneStack(kind));list.add(new ItemStack(WAYPOINT_ITEM.get()));list.add(new ItemStack(CRATE_ITEM.get()));return list;}return tab.equals("consumable")?List.of(new ItemStack(LINKER.get())):List.of();}
 private CargoDroneContent(){}
}
