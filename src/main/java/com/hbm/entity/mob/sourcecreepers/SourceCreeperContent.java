package com.hbm.entity.mob.sourcecreepers;
import com.hbm.entity.mob.*;
import com.hbm.damage.HbmDamageSources;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;
public final class SourceCreeperContent {
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static <T extends Creeper> DeferredHolder<EntityType<?>,EntityType<T>> type(String id,EntityType.EntityFactory<T> factory){return ENTITIES.register("entity_mob_"+id+"_creeper",()->EntityType.Builder.of(factory,MobCategory.MONSTER).sized(.6F,1.8F).eyeHeight(1.53F).clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(true).build("hbm:entity_mob_"+id+"_creeper"));}
 public static final DeferredHolder<EntityType<?>,EntityType<EntityCreeperNuclear>> NUCLEAR=type("nuclear",EntityCreeperNuclear::new);public static final DeferredHolder<EntityType<?>,EntityType<EntityCreeperPhosgene>> PHOSGENE=type("phosgene",EntityCreeperPhosgene::new);public static final DeferredHolder<EntityType<?>,EntityType<EntityCreeperVolatile>> VOLATILE=type("volatile",EntityCreeperVolatile::new);public static final DeferredHolder<EntityType<?>,EntityType<EntityCreeperGold>> GOLD=type("gold",EntityCreeperGold::new);
 private static DeferredItem<DeferredSpawnEggItem> egg(String name,Supplier<? extends EntityType<? extends Mob>> type,int base,int spots){return ITEMS.register(name+"_creeper_spawn_egg",()->new DeferredSpawnEggItem(type,base,spots,new Item.Properties()));}
 public static final DeferredItem<DeferredSpawnEggItem> NUCLEAR_EGG=egg("nuclear",NUCLEAR,0x204131,0x75ce00),PHOSGENE_EGG=egg("phosgene",PHOSGENE,0xe3d398,0xb8a06b),VOLATILE_EGG=egg("volatile",VOLATILE,0xc28153,0x4d382c),GOLD_EGG=egg("gold",GOLD,0xecc136,0x9e8b3e);
 public static void register(IEventBus bus){ENTITIES.register(bus);ITEMS.register(bus);bus.addListener((EntityAttributeCreationEvent event)->{event.put(NUCLEAR.get(),EntityCreeperNuclear.attributes().build());for(var type:List.of(PHOSGENE.get(),VOLATILE.get(),GOLD.get()))event.put(type,Creeper.createAttributes().build());});bus.addListener((RegisterSpawnPlacementsEvent event)->{for(var type:List.of(NUCLEAR.get(),PHOSGENE.get(),VOLATILE.get(),GOLD.get()))event.register(type,SpawnPlacementTypes.ON_GROUND,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,SourceCreeperSpawns::canSpawn,RegisterSpawnPlacementsEvent.Operation.REPLACE);});bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS)for(var egg:List.of(NUCLEAR_EGG,PHOSGENE_EGG,VOLATILE_EGG,GOLD_EGG))event.accept(egg.get());});}
 /** Source EntityEffectHandler transforms only exact vanilla Creeper at dose200, before radiation sickness; powered/fuse/NBT are intentionally not copied. */
 public static boolean transformRadiation(LivingEntity entity,ServerLevel level,float dose){if(entity.getClass()!=Creeper.class||dose<200||entity.getHealth()<=0)return false;if(level.random.nextInt(3)==0){var next=NUCLEAR.get().create(level);if(next==null)throw new IllegalStateException("Missing actual nuclear creeper type");next.moveTo(entity.getX(),entity.getY(),entity.getZ(),entity.getYRot(),entity.getXRot());level.addFreshEntity(next);entity.discard();}else entity.hurt(HbmDamageSources.source(level,HbmDamageSources.radiation),100);return true;}
 private SourceCreeperContent(){}
}
