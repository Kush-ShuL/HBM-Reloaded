package com.hbm.item.meteorbook;

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
    public static final DeferredItem<PrintingStampItem> STAMP = ITEMS.register("stamp_book", PrintingStampItem::new);
    public static final DeferredHolder<MenuType<?>, MenuType<BlackBookMenu>> MENU = MENUS.register("book_of_", () -> new MenuType<>(BlackBookMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>, RecipeType<MagicBookRecipe>> TYPE = TYPES.register("book_of_boxcars", () -> new RecipeType<MagicBookRecipe>() { @Override public String toString() { return "hbm:book_of_boxcars"; } });
    public static final DeferredHolder<RecipeSerializer<?>, MagicBookRecipe.Serializer> SERIALIZER = SERIALIZERS.register("book_of_boxcars", MagicBookRecipe.Serializer::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); MENUS.register(bus); TYPES.register(bus); SERIALIZERS.register(bus); }
    private MeteorBookContent() {}
}
