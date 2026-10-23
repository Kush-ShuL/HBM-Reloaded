package com.hbm.item.plasticbag;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PlasticBagContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    public static final DeferredItem<PlasticBagItem> BAG = ITEMS.register("plastic_bag", PlasticBagItem::new);
    public static final DeferredHolder<MenuType<?>, MenuType<PlasticBagMenu>> MENU = MENUS.register("plastic_bag", () -> IMenuTypeExtension.create(PlasticBagMenu::new));
    public static void register(IEventBus bus) { ITEMS.register(bus); MENUS.register(bus); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(BAG.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private PlasticBagContent() { }
}
