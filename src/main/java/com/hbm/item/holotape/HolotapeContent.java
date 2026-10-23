package com.hbm.item.holotape;
import java.util.List;import net.minecraft.world.item.*;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.registries.*;
/** Damaged Holotape already has its source plain Item identity in basic content; it is not registered twice. */
public final class HolotapeContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<PolaroidItem> POLAROID=ITEMS.register("polaroid",PolaroidItem::new);
 public static final DeferredItem<HolotapeImageItem> IMAGE=ITEMS.register("holotape_image",HolotapeImageItem::new);
 public static void register(IEventBus bus){ITEMS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(POLAROID.get())):List.of();}
 private HolotapeContent(){}
}
