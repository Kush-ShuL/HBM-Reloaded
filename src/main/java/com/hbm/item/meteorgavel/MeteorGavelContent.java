package com.hbm.item.meteorgavel;

import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

public final class MeteorGavelContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<LegacyGavelItem> LEAD = ITEMS.register("lead_gavel", () -> new LegacyGavelItem(LegacyGavelItem.Kind.LEAD));
    public static final DeferredItem<LegacyGavelItem> DIAMOND = ITEMS.register("diamond_gavel", () -> new LegacyGavelItem(LegacyGavelItem.Kind.DIAMOND));
    public static final DeferredItem<LegacyGavelItem> MESE = ITEMS.register("mese_gavel", () -> new LegacyGavelItem(LegacyGavelItem.Kind.MESE));
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(MeteorGavelContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) { if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) { event.accept(LEAD.get()); event.accept(DIAMOND.get()); event.accept(MESE.get()); } }
    public static List<DeferredItem<LegacyGavelItem>> all() { return List.of(LEAD, DIAMOND, MESE); }
    private MeteorGavelContent() {}
}
