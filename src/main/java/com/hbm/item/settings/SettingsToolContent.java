package com.hbm.item.settings;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SettingsToolContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<SettingsToolItem> TOOL=ITEMS.register("settings_tool",SettingsToolItem::new);
    private SettingsToolContent() {}
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("consumable")?List.of(new ItemStack(TOOL.get())):List.of(); }
}
