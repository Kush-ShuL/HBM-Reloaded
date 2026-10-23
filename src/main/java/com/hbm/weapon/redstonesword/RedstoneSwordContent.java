package com.hbm.weapon.redstonesword;

import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RedstoneSwordContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<RedstoneSwordItem> SWORD = ITEMS.register("redstone_sword", RedstoneSwordItem::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(RedstoneSwordContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) event.accept(SWORD.get());
    }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(SWORD.get())); }
    private RedstoneSwordContent() { }
}
