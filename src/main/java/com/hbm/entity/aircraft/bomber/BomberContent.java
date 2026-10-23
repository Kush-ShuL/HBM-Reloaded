package com.hbm.entity.aircraft.bomber;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Real source item/entity identities; reuses C130's already registered aircraft ticket controller. */
public final class BomberContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredItem<ItemRangefinder> RANGEFINDER=ITEMS.register("rangefinder",ItemRangefinder::new);
 public static final DeferredItem<ItemBombCaller> CALLER=ITEMS.register("bomb_caller",ItemBombCaller::new);
 public static final DeferredHolder<EntityType<?>,EntityType<EntityBomber>> BOMBER=ENTITIES.register("entity_bomber",()->EntityType.Builder.<EntityBomber>of(EntityBomber::new,MobCategory.MISC).sized(8F,4F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_bomber"));
 public static final DeferredHolder<EntityType<?>,EntityType<EntityBombletZeta>> ZETA=ENTITIES.register("entity_zeta",()->EntityType.Builder.<EntityBombletZeta>of(EntityBombletZeta::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_zeta"));
 public static final DeferredHolder<EntityType<?>,EntityType<EntityPinkCloudFX>> PINK=ENTITIES.register("entity_pink_cloud_fx",()->EntityType.Builder.<EntityPinkCloudFX>of(EntityPinkCloudFX::new,MobCategory.MISC).sized(.2F,.2F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_pink_cloud_fx"));
 public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);}
 public static List<ItemStack> sourceVisible(){var stacks=new ArrayList<ItemStack>();for(int meta=0;meta<=4;meta++)stacks.add(ItemBombCaller.stack(meta));return List.copyOf(stacks);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?sourceVisible():tab.equals("missile")?List.of(new ItemStack(RANGEFINDER.get())):List.of();}
 private BomberContent(){}
}
