package com.hbm.bomb.fishing;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class FishingDynamiteContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredItem<FishingDynamiteItem> ITEM=ITEMS.register("stick_dynamite_fishing",FishingDynamiteItem::new);
 public static final DeferredHolder<EntityType<?>,EntityType<EntityItemBuoyant>> BUOYANT=ENTITIES.register("entity_item_buoyant",()->EntityType.Builder.<EntityItemBuoyant>of(EntityItemBuoyant::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(7).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_item_buoyant"));
 public static final DeferredHolder<EntityType<?>,EntityType<FishingDetonation>> PENDING=ENTITIES.register("fishing_detonation_pending",()->EntityType.Builder.<FishingDetonation>of(FishingDetonation::new,MobCategory.MISC).sized(0,0).fireImmune().clientTrackingRange(1).updateInterval(Integer.MAX_VALUE).build("hbm:fishing_detonation_pending"));
 private FishingDynamiteContent(){}
 public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(ITEM.get())):List.of();}
}
