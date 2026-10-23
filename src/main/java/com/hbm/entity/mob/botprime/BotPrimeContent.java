package com.hbm.entity.mob.botprime;
import com.hbm.entity.mob.botprime.mixin.MaxHealthAttributeAccess;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;
/** Exactly the2source EntityMappings types and actual consumable spawner; no invented egg/natural spawn/third base type. */
public final class BotPrimeContent {
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredHolder<EntityType<?>,EntityType<BotPrimeHead>> HEAD=ENTITIES.register("entity_balls_o_tron",()->EntityType.Builder.<BotPrimeHead>of(BotPrimeHead::new,MobCategory.MISC).sized(3,3).eyeHeight(1.5F).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:entity_balls_o_tron"));
 public static final DeferredHolder<EntityType<?>,EntityType<BotPrimeBody>> BODY=ENTITIES.register("entity_balls_o_tron_seg",()->EntityType.Builder.<BotPrimeBody>of(BotPrimeBody::new,MobCategory.MISC).sized(2,2).eyeHeight(1).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:entity_balls_o_tron_seg"));
 public static final DeferredItem<BotPrimeSpawnerItem> SPAWNER=ITEMS.register("spawn_worm",BotPrimeSpawnerItem::new);
 public static void register(IEventBus bus){var attribute=(MaxHealthAttributeAccess)Attributes.MAX_HEALTH.value();attribute.hbm$setMaximum(Math.max(attribute.hbm$getMaximum(),15000));ENTITIES.register(bus);ITEMS.register(bus);bus.addListener((EntityAttributeCreationEvent event)->{event.put(HEAD.get(),BotPrimeBase.attributes(true).build());event.put(BODY.get(),BotPrimeBase.attributes(false).build());});}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(SPAWNER.get())):List.of();}
 private BotPrimeContent(){}
}
