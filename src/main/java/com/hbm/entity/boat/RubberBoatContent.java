package com.hbm.entity.boat;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
public final class RubberBoatContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredItem<ItemBoatRubber> ITEM=ITEMS.register("boat_rubber",ItemBoatRubber::new);
 public static final DeferredHolder<EntityType<?>,EntityType<EntityBoatRubber>> ENTITY=ENTITIES.register("entity_rubber_boat",()->EntityType.Builder.<EntityBoatRubber>of(EntityBoatRubber::new,MobCategory.MISC).sized(1.5F,.6F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_rubber_boat"));
 public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(RubberBoatContent::creative);}
 private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey()==CreativeModeTabs.TOOLS_AND_UTILITIES)event.accept(ITEM.get());}
 private RubberBoatContent(){}
}
