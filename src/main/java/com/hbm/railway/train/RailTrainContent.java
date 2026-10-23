package com.hbm.railway.train;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class RailTrainContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredItem<SourceTrainItem> TRAIN=ITEMS.register("train",SourceTrainItem::new);
    public static final DeferredItem<Item> COUPLING_TOOL=ITEMS.register("coupling_tool",()->new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<EntityType<?>,EntityType<TrainCargoTram>> TRAM=ENTITIES.register("entity_ntm_cargo_tram",()->EntityType.Builder.<TrainCargoTram>of(TrainCargoTram::new,MobCategory.MISC).sized(5F,2F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_ntm_cargo_tram"));
    public static final DeferredHolder<EntityType<?>,EntityType<TrainCargoTramTrailer>> TRAILER=ENTITIES.register("entity_ntm_cargo_tram_trailer",()->EntityType.Builder.<TrainCargoTramTrailer>of(TrainCargoTramTrailer::new,MobCategory.MISC).sized(5F,2F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_ntm_cargo_tram_trailer"));
    public static final DeferredHolder<EntityType<?>,EntityType<RailBoundsEntity>> BOUNDS=ENTITIES.register("entity_ntm_bounding_dummy",()->EntityType.Builder.<RailBoundsEntity>of(RailBoundsEntity::new,MobCategory.MISC).sized(2F,1F).noSave().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_ntm_bounding_dummy"));
    public static final DeferredHolder<EntityType<?>,EntityType<RailSeatEntity>> SEAT=ENTITIES.register("entity_ntm_seat_dummy",()->EntityType.Builder.<RailSeatEntity>of(RailSeatEntity::new,MobCategory.MISC).sized(.5F,.1F).noSave().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_ntm_seat_dummy"));
    public static final DeferredHolder<MenuType<?>,MenuType<RailTrainMenu>> TRAM_MENU=MENUS.register("source_cargo_tram",()->new MenuType<>((IContainerFactory<RailTrainMenu>)(id,inventory,data)->new RailTrainMenu(id,inventory,data,false),FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>,MenuType<RailTrainMenu>> TRAILER_MENU=MENUS.register("source_cargo_tram_trailer",()->new MenuType<>((IContainerFactory<RailTrainMenu>)(id,inventory,data)->new RailTrainMenu(id,inventory,data,true),FeatureFlags.DEFAULT_FLAGS));
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(COUPLING_TOOL.get())):List.of();}
    public static List<ItemStack> indexedItems(){return List.of(SourceTrainItem.stack(0),SourceTrainItem.stack(1),new ItemStack(COUPLING_TOOL.get()));}
    private RailTrainContent(){}
}
