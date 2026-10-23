package com.hbm.weapon.sedna.gun.energy.bag;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;

/** One authoritative registration shared by belt guns and the later Energy factory. */
public final class AmmoBagContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredItem<AmmoBagItem> AMMO_BAG=ITEMS.register("ammo_bag",()->new AmmoBagItem(false));
    public static final DeferredItem<AmmoBagItem> AMMO_BAG_INFINITE=ITEMS.register("ammo_bag_infinite",()->new AmmoBagItem(true));
    public static final DeferredHolder<MenuType<?>,MenuType<AmmoBagMenu>> AMMO_BAG_MENU=MENUS.register("ammo_bag",()->IMenuTypeExtension.create(AmmoBagMenu::new));
    private static boolean registered;
    public static void register(IEventBus bus){if(registered)return;registered=true;ITEMS.register(bus);MENUS.register(bus);}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(AMMO_BAG.get()),new ItemStack(AMMO_BAG_INFINITE.get()));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?indexedItems():List.of();}
    private AmmoBagContent(){}
}
