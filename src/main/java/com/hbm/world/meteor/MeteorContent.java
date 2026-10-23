package com.hbm.world.meteor;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.*;

/** Actual falling meteor, remote and both original armor charms. */
public final class MeteorContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredHolder<EntityType<?>,EntityType<MeteorEntity>> METEOR=ENTITIES.register("entity_meteor",()->EntityType.Builder.<MeteorEntity>of(MeteorEntity::new,MobCategory.MISC).sized(4,4).fireImmune().clientTrackingRange(16).updateInterval(1).build("hbm:entity_meteor"));
 public static final DeferredItem<MeteorRemoteItem> REMOTE=ITEMS.register("meteor_remote",MeteorRemoteItem::new);
 public static final DeferredItem<MeteorCharmItem> PROTECTION=ITEMS.register("protection_charm",()->new MeteorCharmItem(false));
 public static final DeferredItem<MeteorCharmItem> CHARM=ITEMS.register("meteor_charm",()->new MeteorCharmItem(true));
 public static void register(IEventBus bus,ModContainer container){ITEMS.register(bus);ENTITIES.register(bus);MeteorConfig.register(container);MeteoriteWorldgen.register(bus);MeteorScheduler.register();MeteorCharmItem.register();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(REMOTE.get()),new ItemStack(PROTECTION.get()),new ItemStack(CHARM.get())):List.of();}
 private MeteorContent(){}
}
