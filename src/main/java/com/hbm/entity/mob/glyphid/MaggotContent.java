package com.hbm.entity.mob.glyphid;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

/** Actual EntityMappings195 entity and its sourceD0D0D0/808080 egg; no unrelated natural spawn is added. */
public final class MaggotContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<ParasiteMaggot>> MAGGOT=ENTITIES.register("entity_parasite_maggot",()->EntityType.Builder.<ParasiteMaggot>of(ParasiteMaggot::new,MobCategory.MONSTER).sized(.3F,.7F).eyeHeight(.7F*.85F).clientTrackingRange(5).updateInterval(3).build("hbm:entity_parasite_maggot"));
    public static final DeferredItem<DeferredSpawnEggItem> EGG=ITEMS.register("parasite_maggot_spawn_egg",()->new DeferredSpawnEggItem(MAGGOT,0xD0D0D0,0x808080,new Item.Properties()));
    public static void register(IEventBus bus){ENTITIES.register(bus);ITEMS.register(bus);bus.addListener((EntityAttributeCreationEvent event)->event.put(MAGGOT.get(),ParasiteMaggot.attributes().build()));bus.addListener(MaggotContent::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS)event.accept(EGG.get());}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(EGG.get()));}
    private MaggotContent(){}
}
