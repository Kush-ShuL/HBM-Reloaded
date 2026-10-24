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
 public static final DeferredItem<ItemRangefinder> RANGEFINDER=ITEMS.register("rangefinder",()->new ItemRangefinder(false));
 public static final DeferredItem<ItemRangefinder> RANGEFINDER_POLARIZED=ITEMS.register("rangefinder_polarized",()->new ItemRangefinder(true));
 private static final List<String> CALLER_NAMES=List.of("carpet","napalm","chlorine","orange","atomic","stinger","boxcar","pink_cloud");
 public static final List<DeferredItem<ItemBombCaller>> CALLERS=registerCallers();
 private static List<DeferredItem<ItemBombCaller>> registerCallers(){var result=new ArrayList<DeferredItem<ItemBombCaller>>();for(int type=0;type<CALLER_NAMES.size();type++){int sourceType=type;result.add(ITEMS.register("bomb_caller_"+CALLER_NAMES.get(type),()->new ItemBombCaller(sourceType)));}return List.copyOf(result);}
 public static final DeferredHolder<EntityType<?>,EntityType<EntityBomber>> BOMBER=ENTITIES.register("entity_bomber",()->EntityType.Builder.<EntityBomber>of(EntityBomber::new,MobCategory.MISC).sized(8F,4F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_bomber"));
 public static final DeferredHolder<EntityType<?>,EntityType<EntityBombletZeta>> ZETA=ENTITIES.register("entity_zeta",()->EntityType.Builder.<EntityBombletZeta>of(EntityBombletZeta::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_zeta"));
 public static final DeferredHolder<EntityType<?>,EntityType<EntityPinkCloudFX>> PINK=ENTITIES.register("entity_pink_cloud_fx",()->EntityType.Builder.<EntityPinkCloudFX>of(EntityPinkCloudFX::new,MobCategory.MISC).sized(.2F,.2F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_pink_cloud_fx"));
 public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);}
 public static List<ItemStack> sourceVisible(){var stacks=new ArrayList<ItemStack>();for(int type=0;type<=4;type++)stacks.add(ItemBombCaller.stack(type));return List.copyOf(stacks);}
 public static List<ItemStack> sourceHidden(){var stacks=new ArrayList<ItemStack>();for(int type=5;type<=7;type++)stacks.add(ItemBombCaller.stack(type));stacks.add(new ItemStack(RANGEFINDER_POLARIZED.get()));return List.copyOf(stacks);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?sourceVisible():tab.equals("missile")?List.of(new ItemStack(RANGEFINDER.get())):List.of();}
 private BomberContent(){}
}
