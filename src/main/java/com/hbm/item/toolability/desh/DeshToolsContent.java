package com.hbm.item.toolability.desh;

import com.hbm.item.toolability.AvailableAbilities;
import com.hbm.item.toolability.ItemToolAbility;
import com.hbm.item.toolability.SteelHoeItem;
import com.hbm.item.toolability.swords.SourceSwordTier;
import static com.hbm.item.toolability.AvailableAbilities.Ability.*;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Original Desh tool quartet; the already-real sword and steam suit retain their unique identities. */
public final class DeshToolsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemToolAbility> PICKAXE = ITEMS.register("desh_pickaxe", () ->
        new ItemToolAbility("desh_pickaxe", 5F, -.05, SourceSwordTier.DESH, ItemToolAbility.Type.PICKAXE, abilities()));
    public static final DeferredItem<ItemToolAbility> AXE = ITEMS.register("desh_axe", () ->
        new ItemToolAbility("desh_axe", 7.5F, -.05, SourceSwordTier.DESH, ItemToolAbility.Type.AXE, abilities().add(BEHEADER, 0)));
    public static final DeferredItem<ItemToolAbility> SHOVEL = ITEMS.register("desh_shovel", () ->
        new ItemToolAbility("desh_shovel", 4F, -.05, SourceSwordTier.DESH, ItemToolAbility.Type.SHOVEL, abilities()));
    public static final DeferredItem<SteelHoeItem> HOE = ITEMS.register("desh_hoe", () -> new SteelHoeItem(SourceSwordTier.DESH));
    private static AvailableAbilities abilities() {
        return new AvailableAbilities().add(HAMMER, 0).add(HAMMER_FLAT, 0).add(RECURSION, 0).add(SILK, 0).add(LUCK, 1);
    }
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(DeshToolsContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private DeshToolsContent() { }
}
