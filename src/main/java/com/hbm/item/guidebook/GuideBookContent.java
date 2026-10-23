package com.hbm.item.guidebook;
import java.util.List;import net.minecraft.world.item.*;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.registries.*;
public final class GuideBookContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 /** Source item.book_guide differs from the independent tile.book_guide block; retain distinct modern registry IDs. */
 public static final DeferredItem<GuideBookItem> BOOK=ITEMS.register("book_guide_item",GuideBookItem::new);
 public static void register(IEventBus bus){ITEMS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?BOOK.get().visibleVariants():List.of();}
 private GuideBookContent(){}
}
