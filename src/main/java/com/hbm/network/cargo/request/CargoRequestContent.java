package com.hbm.network.cargo.request;
import com.hbm.item.BasicBlockItem;
import com.hbm.network.cargo.CargoDroneContent;
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
public final class CargoRequestContent {
 public enum Kind{DOCK("drone_dock"),PROVIDER("drone_crate_provider"),REQUESTER("drone_crate_requester");public final String id;Kind(String id){this.id=id;}}
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> BES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final Map<Kind,DeferredBlock<DroneEndpointBlock>> BLOCKS_BY_KIND=new LinkedHashMap<>();public static final Map<Kind,DeferredItem<BasicBlockItem>> ITEMS_BY_KIND=new LinkedHashMap<>();public static final Map<Kind,DeferredHolder<MenuType<?>,MenuType<CargoRequestMenu>>> MENUS_BY_KIND=new LinkedHashMap<>();
 static{for(var kind:Kind.values()){var block=BLOCKS.register(kind.id,()->new DroneEndpointBlock(kind));BLOCKS_BY_KIND.put(kind,block);ITEMS_BY_KIND.put(kind,ITEMS.register(kind.id,()->new BasicBlockItem(block.get(),new Item.Properties(),"BlockGenericTooltip",kind.id)));MENUS_BY_KIND.put(kind,MENUS.register(kind.id,()->new MenuType<>((IContainerFactory<CargoRequestMenu>)(id,inv,buf)->new CargoRequestMenu(id,inv,buf,kind),FeatureFlags.DEFAULT_FLAGS)));}}
 public static final DeferredBlock<RequestWaypointBlock> WAYPOINT=BLOCKS.register("drone_waypoint_request",RequestWaypointBlock::new);public static final DeferredItem<BlockItem> WAYPOINT_ITEM=ITEMS.register("drone_waypoint_request",()->new BlockItem(WAYPOINT.get(),new Item.Properties()));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RequestWaypointEntity>> WAYPOINT_ENTITY=BES.register("drone_waypoint_request",()->BlockEntityType.Builder.of(RequestWaypointEntity::new,WAYPOINT.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DroneDockEntity>> DOCK_ENTITY=BES.register("drone_dock",()->BlockEntityType.Builder.of(DroneDockEntity::new,BLOCKS_BY_KIND.get(Kind.DOCK).get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DroneProviderEntity>> PROVIDER_ENTITY=BES.register("drone_crate_provider",()->BlockEntityType.Builder.of(DroneProviderEntity::new,BLOCKS_BY_KIND.get(Kind.PROVIDER).get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DroneRequesterEntity>> REQUESTER_ENTITY=BES.register("drone_crate_requester",()->BlockEntityType.Builder.of(DroneRequesterEntity::new,BLOCKS_BY_KIND.get(Kind.REQUESTER).get()).build(null));
 public static final DeferredHolder<EntityType<?>,EntityType<RequestDrone>> REQUEST_ENTITY=ENTITIES.register("entity_request_drone",()->EntityType.Builder.<RequestDrone>of(RequestDrone::new,MobCategory.MISC).sized(.75F,.75F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_request_drone"));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BES.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(CargoRequestContent::capabilities);bus.addListener(CargoRequestFilterControl::register);}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,DOCK_ENTITY.get(),(tile,side)->new SidedInvWrapper(tile,side==null?net.minecraft.core.Direction.DOWN:side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PROVIDER_ENTITY.get(),(tile,side)->new SidedInvWrapper(tile,side==null?net.minecraft.core.Direction.DOWN:side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,REQUESTER_ENTITY.get(),(tile,side)->new SidedInvWrapper(tile,side==null?net.minecraft.core.Direction.DOWN:side));}
 public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("machine"))return List.of();var stacks=new ArrayList<ItemStack>();stacks.add(CargoDroneContent.DRONE.get().stack(4));stacks.add(new ItemStack(WAYPOINT_ITEM.get()));ITEMS_BY_KIND.values().forEach(item->stacks.add(new ItemStack(item.get())));return stacks;}
 private CargoRequestContent(){}
}
