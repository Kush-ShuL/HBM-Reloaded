package com.hbm.item.guidebook;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The four source book types have independent identities; the display block stays separate. */
public final class GuideBookContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final Map<GuideBooks.BookType, DeferredItem<GuideBookItem>> BOOKS = new EnumMap<>(GuideBooks.BookType.class);
    static {
        for (var type : GuideBooks.BookType.values()) {
            BOOKS.put(type, ITEMS.register("book_guide_" + type.name().toLowerCase(Locale.ROOT), () -> new GuideBookItem(type)));
        }
    }
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static ItemStack stack(GuideBooks.BookType type) { return new ItemStack(BOOKS.get(type).get()); }
    public static List<ItemStack> visibleStacks() {
        return List.of(stack(GuideBooks.BookType.RBMK), stack(GuideBooks.BookType.STARTER));
    }
    public static List<ItemStack> hiddenStacks() {
        return List.of(stack(GuideBooks.BookType.TEST), stack(GuideBooks.BookType.HADRON));
    }
    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("consumable") ? visibleStacks() : List.of();
    }
    private GuideBookContent() { }
}
