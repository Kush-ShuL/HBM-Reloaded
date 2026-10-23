package com.hbm.item.meteorbook.tool;

import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

/** Source Rod consumable tab and Balefire Match vanilla tools tab; no substitute registrations. */
public final class MeteorBookToolsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<BalefireMatchItem> MATCH = ITEMS.register("balefire_and_steel", BalefireMatchItem::new);
    public static final DeferredItem<DiscordRodItem> ROD = ITEMS.register("rod_of_discord", DiscordRodItem::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(MeteorBookToolsContent::vanillaTab); }
    private static void vanillaTab(BuildCreativeModeTabContentsEvent event) { if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) event.accept(MATCH.get()); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("consumable") ? List.of(new ItemStack(ROD.get())) : List.of(); }
    private MeteorBookToolsContent() {}
}
