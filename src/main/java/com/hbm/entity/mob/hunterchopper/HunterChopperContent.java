package com.hbm.entity.mob.hunterchopper;
import com.hbm.exposure.*;
import com.hbm.item.spawn.LegacyChopperSpawnerItem;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;
/** Original EntityMappings174 and61 plus real ItemChopper; no new natural spawn entry. */
public final class HunterChopperContent {
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredHolder<EntityType<?>,EntityType<HunterChopper>> HUNTER_CHOPPER=ENTITIES.register("entity_mob_hunter_chopper",()->EntityType.Builder.<HunterChopper>of(HunterChopper::new,MobCategory.MONSTER).sized(8.25F,3F).fireImmune().clientTrackingRange(5).updateInterval(3).build("hbm:entity_mob_hunter_chopper"));
 public static final DeferredHolder<EntityType<?>,EntityType<ChopperMine>> MINE=ENTITIES.register("entity_chopper_mine",()->EntityType.Builder.<ChopperMine>of(ChopperMine::new,MobCategory.MISC).sized(.6F,1.8F).clientTrackingRange(63).updateInterval(1).build("hbm:entity_chopper_mine"));
 public static final DeferredItem<LegacyChopperSpawnerItem> SPAWNER=ITEMS.register("spawn_chopper",()->new LegacyChopperSpawnerItem(HUNTER_CHOPPER,1));public static final DeferredItem<DeferredSpawnEggItem> EGG=ITEMS.register("hunter_chopper_spawn_egg",()->new DeferredSpawnEggItem(HUNTER_CHOPPER,0x000020,0x2d2d72,new Item.Properties()));
 public static void register(IEventBus bus){ENTITIES.register(bus);ITEMS.register(bus);bus.addListener((EntityAttributeCreationEvent e)->e.put(HUNTER_CHOPPER.get(),HunterChopper.attributes().build()));bus.addListener((BuildCreativeModeTabContentsEvent e)->{if(e.getTabKey()==CreativeModeTabs.SPAWN_EGGS)e.accept(EGG.get());});bus.addListener(HunterChopperEffects::register);HbmExposure.addProtection(new ExposureProtection(){@Override public boolean radiationImmune(LivingEntity entity){return entity instanceof HunterChopper;}});}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(SPAWNER.get())):List.of();}private HunterChopperContent(){}
}
