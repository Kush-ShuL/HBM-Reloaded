package com.hbm.bobmazon;

import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;

/** Only the two active X5846 catalogs, one registered custom-kit item, and the actual delivery entity. */
public final class BobmazonContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredItem<CatalogItem> CATALOG=ITEMS.register("bobmazon",()->new CatalogItem(false));
 public static final DeferredItem<CatalogItem> HIDDEN=ITEMS.register("bobmazon_hidden",()->new CatalogItem(true));
 public static final DeferredItem<CustomKitItem> KIT=ITEMS.register("kit_custom",CustomKitItem::new);
 public static final DeferredHolder<EntityType<?>,EntityType<BobmazonDelivery>> DELIVERY=ENTITIES.register("entity_bobmazon_delivery",()->EntityType.Builder.<BobmazonDelivery>of(BobmazonDelivery::new,MobCategory.MISC).sized(1,3).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_bobmazon_delivery"));
 public static BiConsumer<Player,InteractionHand> CLIENT_OPEN=(player,hand)->{};
 public static java.util.function.Consumer<BobmazonDelivery> CLIENT_EXHAUST=entity->{};
 public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(BobmazonPurchase::register);NeoForge.EVENT_BUS.register(new BobmazonUnlocks());}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(CATALOG.get())):List.of();}
 public static List<ItemStack> indexedItems(){return List.of(new ItemStack(CATALOG.get()),new ItemStack(HIDDEN.get()),new ItemStack(KIT.get()));}
 public static List<ItemStack> allStacks(){return indexedItems();}
 private BobmazonContent(){}
}
