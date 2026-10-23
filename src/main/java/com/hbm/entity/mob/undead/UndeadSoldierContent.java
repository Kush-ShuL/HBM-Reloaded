package com.hbm.entity.mob.undead;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class UndeadSoldierContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<UndeadSoldier>> SOLDIER=ENTITIES.register("entity_ntm_undead_soldier",()->EntityType.Builder.<UndeadSoldier>of(UndeadSoldier::new,MobCategory.MONSTER).sized(.6F,1.8F).eyeHeight(1.8F*.85F).clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(true).build("hbm:entity_ntm_undead_soldier"));
    public static final DeferredItem<DeferredSpawnEggItem> EGG=ITEMS.register("undead_soldier_spawn_egg",()->new DeferredSpawnEggItem(SOLDIER,0x749f30,0x6c5b44,new Item.Properties()));
    public static void register(IEventBus bus){
        ENTITIES.register(bus);ITEMS.register(bus);
        bus.addListener((EntityAttributeCreationEvent event)->event.put(SOLDIER.get(),UndeadSoldier.attributes().build()));
        bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS)event.accept(EGG.get());});
        // Source registers an egg but no natural-biome spawn entry. DungeonSpawner
        // is a separate real source module, not a fabricated replacement spawner here.
    }
    private UndeadSoldierContent(){ }
}
