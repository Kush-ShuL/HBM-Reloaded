package com.hbm.satellite.legacychips;

import com.hbm.satellite.SatelliteKind;
import com.hbm.satellite.item.SatelliteChipItem;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Three genuine deprecated ItemSatChip identities; no new satellite factories or duplicate orbital execution. */
public final class LegacySatelliteChipsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<SatelliteChipItem> MINER = ITEMS.register("sat_miner", () -> new SatelliteChipItem(SatelliteKind.MINER_ASTRO));
    public static final DeferredItem<SatelliteChipItem> LUNAR_MINER = ITEMS.register("sat_lunar_miner", () -> new SatelliteChipItem(SatelliteKind.MINER_LUNAR));
    public static final DeferredItem<FoeqChipItem> FOEQ = ITEMS.register("sat_foeq", FoeqChipItem::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    /** Tooltips/viewers may enumerate genuine stacks; source setCreativeTab(null) remains unchanged. */
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(MINER.get()), new ItemStack(LUNAR_MINER.get()), new ItemStack(FOEQ.get())); }
    private LegacySatelliteChipsContent() { }
}
