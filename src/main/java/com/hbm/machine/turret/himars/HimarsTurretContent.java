package com.hbm.machine.turret.himars;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.*;
/** Sole Henry turret/pod/rocket identities; native HE and inherited artillery target/32-port/CC/ROR backend. */
public final class HimarsTurretContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<HimarsTurretBlock> BLOCK=BLOCKS.register("turret_himars",HimarsTurretBlock::new);
 public static final DeferredItem<BlockItem> ITEM=ITEMS.register("turret_himars",()->new BlockItem(BLOCK.get(),new Item.Properties()));
 private static final List<DeferredItem<HimarsAmmoItem>> AMMO_ITEMS=registerAmmo();
 private static List<DeferredItem<HimarsAmmoItem>> registerAmmo(){var items=new ArrayList<DeferredItem<HimarsAmmoItem>>(Collections.nCopies(HimarsRocketTypes.TYPES.length,null));for(int type:new int[]{0,2,3,4,7,6,1,5})items.set(type,ITEMS.register("ammo_himars_"+HimarsRocketTypes.type(type).name(),()->new HimarsAmmoItem(type)));return List.copyOf(items);}
 public static HimarsAmmoItem ammo(int type){return AMMO_ITEMS.get(type).get();}
 public static List<HimarsAmmoItem> ammoItems(){return AMMO_ITEMS.stream().map(holder->holder.get()).toList();}
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HimarsTurretEntity>> ENTITY=BLOCK_ENTITIES.register("turret_himars",()->BlockEntityType.Builder.of(HimarsTurretEntity::new,BLOCK.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HimarsTurretProxy>> PROXY=BLOCK_ENTITIES.register("turret_himars_proxy",()->BlockEntityType.Builder.of(HimarsTurretProxy::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<HimarsTurretMenu>> MENU=MENUS.register("turret_himars",()->IMenuTypeExtension.create((id,inventory,buffer)->new HimarsTurretMenu(id,inventory,buffer.readBlockPos())));
 public static final DeferredHolder<EntityType<?>,EntityType<HimarsRocketEntity>> ROCKET=ENTITIES.register("entity_himars",()->EntityType.Builder.<HimarsRocketEntity>of(HimarsRocketEntity::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(63).updateInterval(1).build("hbm:entity_himars"));
 public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BLOCK_ENTITIES.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(HimarsTurretContent::capabilities);bus.addListener((RegisterTicketControllersEvent event)->event.register(HimarsRocketEntity.TICKETS));bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(()->com.hbm.machine.turret.TurretTargeting.machines.add(HimarsRocketEntity.class)));}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.DOWN:side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PROXY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.DOWN:side));}
 public static List<ItemStack> creativeStacks(String tab){if(!"weapon".equals(tab))return List.of();var list=new ArrayList<ItemStack>();list.add(new ItemStack(ITEM.get()));list.addAll(HimarsAmmoItem.creative());return list;}
 private HimarsTurretContent(){}
}
