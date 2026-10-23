package com.hbm.item.lemegeton;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** The source gives this item no creative tab and no manufacturing or loot recipe. */
public final class LemegetonContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, "hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "hbm");
    public static final DeferredItem<LemegetonItem> BOOK = ITEMS.register("book_lemegeton", LemegetonItem::new);
    public static final DeferredHolder<MenuType<?>, MenuType<LemegetonMenu>> MENU = MENUS.register("book_lemegeton", () -> new MenuType<>(LemegetonMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>, RecipeType<LemegetonRecipe>> TYPE = TYPES.register("lemegeton", () -> new RecipeType<LemegetonRecipe>() { @Override public String toString() { return "hbm:lemegeton"; } });
    public static final DeferredHolder<RecipeSerializer<?>, LemegetonRecipe.Serializer> SERIALIZER = SERIALIZERS.register("lemegeton", LemegetonRecipe.Serializer::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); MENUS.register(bus); TYPES.register(bus); SERIALIZERS.register(bus); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(BOOK.get())); }
    private LemegetonContent() { }
}
