package com.hbm.machine.reactor.zirnox;
import com.hbm.registry.HbmContent;
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
/** Independent source-closed Zirnox registration group; source spent rods/items remain the existing shared registrations. */
public final class ZirnoxContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredBlock<ZirnoxBlock> REACTOR=BLOCKS.register("machine_zirnox",ZirnoxBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(REACTOR);
    public static final DeferredBlock<ZirnoxDestroyedBlock> DESTROYED=BLOCKS.register("zirnox_destroyed",ZirnoxDestroyedBlock::new);public static final DeferredItem<BlockItem> DESTROYED_ITEM=ITEMS.registerSimpleBlockItem(DESTROYED);
    public static final DeferredBlock<SteelGrateBlock> GRATE=BLOCKS.register("steel_grate",()->new SteelGrateBlock(false)),WIDE_GRATE=BLOCKS.register("steel_grate_wide",()->new SteelGrateBlock(true));
    public static final DeferredItem<SteelGrateBlock.GrateItem> GRATE_ITEM=ITEMS.register("steel_grate",()->new SteelGrateBlock.GrateItem(GRATE.get(),"steel_grate")),WIDE_GRATE_ITEM=ITEMS.register("steel_grate_wide",()->new SteelGrateBlock.GrateItem(WIDE_GRATE.get(),"steel_grate_wide"));
    private static final List<DeferredItem<ZirnoxRod>> RODS=registerRods();
    private static List<DeferredItem<ZirnoxRod>> registerRods(){var rods=new ArrayList<DeferredItem<ZirnoxRod>>();for(var type:ZirnoxRod.Type.values())rods.add(ITEMS.register("rod_zirnox_"+type.name().toLowerCase(Locale.ROOT),()->new ZirnoxRod(type)));return List.copyOf(rods);}
    public static ItemStack rodStack(ZirnoxRod.Type type){return new ItemStack(RODS.get(type.ordinal()).get());}
    public static List<ItemStack> rodStacks(){return RODS.stream().map(item->new ItemStack(item.get())).toList();}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ZirnoxBlockEntity>> REACTOR_ENTITY=BLOCK_ENTITIES.register("zirnox_reactor",()->BlockEntityType.Builder.of(ZirnoxBlockEntity::new,REACTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ZirnoxPort>> PORT_ENTITY=BLOCK_ENTITIES.register("zirnox_port",()->BlockEntityType.Builder.of(ZirnoxPort::new,REACTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ZirnoxDestroyedEntity>> DESTROYED_ENTITY=BLOCK_ENTITIES.register("zirnox_destroyed",()->BlockEntityType.Builder.of(ZirnoxDestroyedEntity::new,DESTROYED.get()).build(null));
    public static final DeferredHolder<EntityType<?>,EntityType<ZirnoxDebris>> DEBRIS=ENTITIES.register("zirnox_debris",()->EntityType.Builder.<ZirnoxDebris>of(ZirnoxDebris::new,MobCategory.MISC).sized(.5F,.5F).clientTrackingRange(63).updateInterval(1).build("hbm:zirnox_debris"));
    public static final DeferredHolder<MenuType<?>,MenuType<ZirnoxMenu>> MENU=MENUS.register("zirnox",()->new MenuType<>((IContainerFactory<ZirnoxMenu>)ZirnoxMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static ItemStack spent(ZirnoxRod.Type type){if(type==ZirnoxRod.Type.TH232)return rodStack(ZirnoxRod.Type.THORIUM_FUEL);return new ItemStack(HbmContent.item(type==ZirnoxRod.Type.LITHIUM?"rod_zirnox_tritium":"rod_zirnox_"+type.name().toLowerCase(Locale.ROOT)+"_depleted"));}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BLOCK_ENTITIES.register(bus);MENUS.register(bus);ENTITIES.register(bus);ReactorWorldRules.initialize();bus.addListener(ZirnoxControl::register);bus.addListener(ZirnoxFlameEffect::register);bus.addListener(ZirnoxContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,REACTOR_ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT_ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):tab.equals("block")?List.of(new ItemStack(GRATE_ITEM.get()),new ItemStack(WIDE_GRATE_ITEM.get())):tab.equals("control")?rodStacks():List.of();}
    private ZirnoxContent(){}
}
