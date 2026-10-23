package com.hbm.item.toolability.hoes;

import com.hbm.block.junglemechanics.JungleWeaponTiers;
import com.hbm.item.toolability.SteelHoeItem;
import com.hbm.item.toolability.chlorophyte.UpstreamToolTier;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Three actual remaining ModHoe siblings on the existing old-hoe implementation. */
public final class SourceHoesContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<SteelHoeItem> COBALT = ITEMS.register("cobalt_hoe", () -> new SteelHoeItem(JungleWeaponTiers.COBALT));
    public static final DeferredItem<SteelHoeItem> DECORATED_COBALT = ITEMS.register("cobalt_decorated_hoe", () -> new SteelHoeItem(UpstreamToolTier.DECORATED_COBALT));
    public static final DeferredItem<SteelHoeItem> STARMETAL = ITEMS.register("starmetal_hoe", () -> new SteelHoeItem(UpstreamToolTier.STARMETAL));
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(SourceHoesContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private SourceHoesContent() { }
}
