package com.hbm.item.meteorbook;

import java.util.List;
import java.util.ArrayList;
import net.minecraft.world.item.ItemStack;
import com.hbm.item.StampItem.StampType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Neither source item has a creative tab. Pages remain the already registered real EnumMultiItem. */
public final class MeteorBookContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, "hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "hbm");
    public static final DeferredItem<BlackBookItem> BOOK = ITEMS.register("book_of_", BlackBookItem::new);
    private static final List<DeferredItem<PrintingStampItem>> STAMPS=registerPrintingStamps();
    private static List<DeferredItem<PrintingStampItem>> registerPrintingStamps(){var result=new ArrayList<DeferredItem<PrintingStampItem>>();for(int i=0;i<8;i++){var type=StampType.values()[StampType.PRINTING1.ordinal()+i];result.add(ITEMS.register("stamp_book_printing"+(i+1),()->new PrintingStampItem(type)));}return List.copyOf(result);}
    public static List<ItemStack> printingStamps(){return STAMPS.stream().map(item->new ItemStack(item.get())).toList();}
    public static final DeferredHolder<MenuType<?>, MenuType<BlackBookMenu>> MENU = MENUS.register("book_of_", () -> new MenuType<>(BlackBookMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>, RecipeType<MagicBookRecipe>> TYPE = TYPES.register("book_of_boxcars", () -> new RecipeType<MagicBookRecipe>() { @Override public String toString() { return "hbm:book_of_boxcars"; } });
    public static final DeferredHolder<RecipeSerializer<?>, MagicBookRecipe.Serializer> SERIALIZER = SERIALIZERS.register("book_of_boxcars", MagicBookRecipe.Serializer::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); MENUS.register(bus); TYPES.register(bus); SERIALIZERS.register(bus); }
    private MeteorBookContent() {}
}
