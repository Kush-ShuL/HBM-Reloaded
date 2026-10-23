package com.hbm.entity.mob.duck;

import com.hbm.exposure.*;
import com.hbm.item.spawn.LegacyChopperSpawnerItem;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

public final class DuckContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<SourceDuck>> DUCK=ENTITIES.register("entity_fucc_a_ducc",()->EntityType.Builder.<SourceDuck>of(SourceDuck::new,MobCategory.CREATURE).sized(.3F,.7F).eyeHeight(.595F).clientTrackingRange(5).updateInterval(3).build("hbm:entity_fucc_a_ducc"));
    public static final DeferredHolder<EntityType<?>,EntityType<Quackos>> QUACKOS=ENTITIES.register("entity_elder_one",()->EntityType.Builder.<Quackos>of(Quackos::new,MobCategory.CREATURE).sized(7.5F,17.5F).eyeHeight(14.875F).clientTrackingRange(5).updateInterval(3).build("hbm:entity_elder_one"));
    public static final DeferredItem<LegacyChopperSpawnerItem> SPAWNER=ITEMS.register("spawn_duck",()->new LegacyChopperSpawnerItem(DUCK,16));
    public static final DeferredItem<PeasItem> PEAS=ITEMS.register("peas",PeasItem::new);
    public static final DeferredItem<DeferredSpawnEggItem> DUCK_EGG=ITEMS.register("duck_spawn_egg",()->new DeferredSpawnEggItem(DUCK,0xd0d0d0,0xffbf00,new Item.Properties()));
    public static final DeferredItem<DeferredSpawnEggItem> QUACKOS_EGG=ITEMS.register("quackos_spawn_egg",()->new DeferredSpawnEggItem(QUACKOS,0xd0d0d0,0xffbf00,new Item.Properties()));
    public static void register(IEventBus bus){
        ENTITIES.register(bus);ITEMS.register(bus);
        bus.addListener((EntityAttributeCreationEvent event)->{event.put(DUCK.get(),Chicken.createAttributes().build());event.put(QUACKOS.get(),Chicken.createAttributes().build());});
        bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS){event.accept(DUCK_EGG.get());event.accept(QUACKOS_EGG.get());}});
        HbmExposure.addProtection(new ExposureProtection(){@Override public boolean radiationImmune(LivingEntity entity){return entity instanceof Quackos;}});
    }
    /** Source EntityEffectHandler checks exact Duck class before immunity or radiation damage. */
    public static boolean transformRadiation(LivingEntity entity,ServerLevel level,float dose){
        if(entity.getClass()!=SourceDuck.class||dose<200)return false;
        var boss=QUACKOS.get().create(level);if(boss==null)return false;
        boss.moveTo(entity.getX(),entity.getY(),entity.getZ(),entity.getYRot(),entity.getXRot());
        level.addFreshEntity(boss);entity.discard();return true;
    }
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(SPAWNER.get()),new ItemStack(PEAS.get())):List.of();}
    private DuckContent(){}
}
