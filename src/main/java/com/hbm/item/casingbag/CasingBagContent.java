package com.hbm.item.casingbag;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Sole real ModItems.casing_bag registration; no extra recovery capability/storage engine. */
public final class CasingBagContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredItem<CasingBagItem> BAG=ITEMS.register("casing_bag",CasingBagItem::new);
    public static final DeferredHolder<MenuType<?>,MenuType<CasingBagMenu>> MENU=MENUS.register("casing_bag",()->IMenuTypeExtension.create(CasingBagMenu::new));
    private static boolean registered;
    private CasingBagContent() { }
    public static void register(IEventBus bus) { if(registered)return;registered=true;ITEMS.register(bus);MENUS.register(bus); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(BAG.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("consumable")?indexedItems():List.of(); }
}
