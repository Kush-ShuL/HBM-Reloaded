package com.hbm.entity.mob.dummy;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

/** X5846 EntityMappings196: white/black shared spawn egg, no natural spawn entry. */
public final class TestDummyContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityDummy>> DUMMY=ENTITIES.register("entity_ntm_test_dummy",()->EntityType.Builder.<EntityDummy>of(EntityDummy::new,MobCategory.CREATURE).sized(.6F,1.8F).eyeHeight(1.8F*.85F).clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(true).build("hbm:entity_ntm_test_dummy"));
    public static final DeferredItem<DeferredSpawnEggItem> EGG=ITEMS.register("test_dummy_spawn_egg",()->new DeferredSpawnEggItem(DUMMY,0xFFFFFF,0x000000,new Item.Properties()));
    private TestDummyContent(){}
    public static void register(IEventBus bus){ENTITIES.register(bus);ITEMS.register(bus);bus.addListener((EntityAttributeCreationEvent event)->event.put(DUMMY.get(),EntityDummy.attributes().build()));bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS)event.accept(EGG.get());});}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(EGG.get()));}
}
