package com.hbm.item.holotape;
import java.util.List;import net.minecraft.world.item.*;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.registries.*;
/** Damaged Holotape already has its source plain Item identity in basic content; it is not registered twice. */
public final class HolotapeContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<PolaroidItem> POLAROID=ITEMS.register("polaroid",PolaroidItem::new);
 private static final List<DeferredItem<HolotapeImageItem>> IMAGES=registerImages();
 private static List<DeferredItem<HolotapeImageItem>> registerImages(){var images=new java.util.ArrayList<DeferredItem<HolotapeImageItem>>();for(var image:HolotapeImageType.values())images.add(ITEMS.register("holotape_image_"+image.serializedName(),()->new HolotapeImageItem(image)));return List.copyOf(images);}
 public static List<ItemStack> imageStacks(){return IMAGES.stream().map(item->new ItemStack(item.get())).toList();}
 public static void register(IEventBus bus){ITEMS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(POLAROID.get())):List.of();}
 private HolotapeContent(){}
}
