package com.hbm.item.containment;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Unique active ItemLeadBox registration; no block, machine, network, energy or fluid interface. */
public final class ContainmentBoxContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredItem<ContainmentBoxItem> ITEM=ITEMS.register("containment_box",ContainmentBoxItem::new);
    public static final DeferredHolder<MenuType<?>,MenuType<ContainmentBoxMenu>> MENU=MENUS.register("containment_box",()->IMenuTypeExtension.create(ContainmentBoxMenu::new));
    private static boolean registered;
    public static void register(IEventBus bus){if(registered)return;registered=true;ITEMS.register(bus);MENUS.register(bus);}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(ITEM.get()));}
    public static List<ItemStack> allStacks(){return indexedItems();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?indexedItems():List.of();}
    private ContainmentBoxContent(){}
}
