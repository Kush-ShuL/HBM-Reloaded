package com.hbm.satellite.resources;

import com.hbm.entity.mob.fbi.*;
import com.hbm.satellite.SatelliteRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

/** Registers only the actual missing factories/entities; existing payload/frequency/cargo/impact stores remain unique. */
public final class SatelliteResources113 {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityTom>> TOM=ENTITIES.register("entity_tom_the_moonstone",()->EntityType.Builder.<EntityTom>of(EntityTom::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(63).updateInterval(1).build("hbm:entity_tom_the_moonstone"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityTomBlast>> BLAST=ENTITIES.register("entity_tom_bust",()->EntityType.Builder.<EntityTomBlast>of(EntityTomBlast::new,MobCategory.MISC).sized(.6F,1.8F).clientTrackingRange(63).updateInterval(1).build("hbm:entity_tom_bust"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityCloudTom>> CLOUD=ENTITIES.register("entity_moonstone_blast",()->EntityType.Builder.<EntityCloudTom>of(EntityCloudTom::new,MobCategory.MISC).sized(1,4).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:entity_moonstone_blast"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityFBI>> AGENT=ENTITIES.register("entity_ntm_fbi",()->EntityType.Builder.<EntityFBI>of(EntityFBI::new,MobCategory.MONSTER).sized(.6F,1.8F).fireImmune().clientTrackingRange(5).updateInterval(3).build("hbm:entity_ntm_fbi"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityFBIDrone>> DRONE=ENTITIES.register("entity_ntm_fbi_drone",()->EntityType.Builder.<EntityFBIDrone>of(EntityFBIDrone::new,MobCategory.MONSTER).sized(.6F,1.8F).clientTrackingRange(5).updateInterval(3).build("hbm:entity_ntm_fbi_drone"));
    public static final DeferredItem<DeferredSpawnEggItem> AGENT_EGG=ITEMS.register("fbi_spawn_egg",()->new DeferredSpawnEggItem(AGENT,0x008000,0x404040,new Item.Properties())),DRONE_EGG=ITEMS.register("fbi_drone_spawn_egg",()->new DeferredSpawnEggItem(DRONE,0x008000,0x404040,new Item.Properties()));
    public static void register(IEventBus bus,ModContainer container){
        SatelliteRegistry.bind(6,SatelliteMiner.class,SatelliteMiner::new);SatelliteRegistry.bind(7,SatelliteLunarMiner.class,SatelliteLunarMiner::new);SatelliteRegistry.bind(8,SatelliteHorizons.class,SatelliteHorizons::new);
        FbiConfig.register(container);FbiRaids.register();PaaContent.register(bus);ImpactTerrainContent.register(bus);ENTITIES.register(bus);ITEMS.register(bus);
        bus.addListener((EntityAttributeCreationEvent event)->{event.put(AGENT.get(),EntityFBI.attributes().build());event.put(DRONE.get(),EntityFBIDrone.attributes().build());});
        bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS){event.accept(AGENT_EGG.get());event.accept(DRONE_EGG.get());}});
    }
    private SatelliteResources113(){}
}
