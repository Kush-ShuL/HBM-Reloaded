package com.hbm.entity.mob.pigeon;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.*;

public final class PigeonContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityPigeon>> PIGEON=ENTITIES.register("entity_pigeon",()->EntityType.Builder.<EntityPigeon>of(EntityPigeon::new,MobCategory.CREATURE).sized(.5F,1F).clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(true).build("hbm:entity_pigeon"));
    public static final DeferredItem<DeferredSpawnEggItem> EGG=ITEMS.register("pigeon_spawn_egg",()->new DeferredSpawnEggItem(PIGEON,0xC8C9CD,0x858894,new Item.Properties()));
    private PigeonContent(){}
    public static void register(IEventBus bus){ENTITIES.register(bus);ITEMS.register(bus);PigeonEffects.register(bus);bus.addListener((EntityAttributeCreationEvent event)->event.put(PIGEON.get(),EntityPigeon.attributes().build()));bus.addListener(PigeonContent::placements);bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS)event.accept(EGG.get());});}
    private static void placements(RegisterSpawnPlacementsEvent event){event.register(PIGEON.get(),SpawnPlacementTypes.ON_GROUND,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type,world,reason,pos,random)->true,RegisterSpawnPlacementsEvent.Operation.REPLACE);}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(EGG.get()));}
}
