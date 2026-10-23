package com.hbm.block.sourcecrystal;

import java.util.Map;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;

/** Source-null creative tabs are retained. Hardened crystal and actual crafting materials already have canonical owners. */
public final class SourceCrystalsContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<CrystalVirusBlock> VIRUS = BLOCKS.register("crystal_virus", CrystalVirusBlock::new);
    public static final DeferredBlock<CrystalPulsarBlock> PULSAR = BLOCKS.register("crystal_pulsar", CrystalPulsarBlock::new);
    public static final DeferredItem<BlockItem> VIRUS_ITEM = ITEMS.registerSimpleBlockItem(VIRUS);
    public static final DeferredItem<BlockItem> PULSAR_ITEM = ITEMS.registerSimpleBlockItem(PULSAR);
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus);
        NeoForge.EVENT_BUS.addListener(CrystalActions::loaded);
        NeoForge.EVENT_BUS.addListener(CrystalActions::tick);
        NeoForge.EVENT_BUS.addListener(CrystalActions::unload);
        NeoForge.EVENT_BUS.addListener(CrystalActions::stopped);
    }
    public static Map<String, Item> indexedItems() { return Map.of("crystal_virus", VIRUS_ITEM.get(), "crystal_pulsar", PULSAR_ITEM.get()); }
    private SourceCrystalsContent() { }
}
