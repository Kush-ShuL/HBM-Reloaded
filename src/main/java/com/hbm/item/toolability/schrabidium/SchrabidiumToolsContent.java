package com.hbm.item.toolability.schrabidium;

import com.hbm.item.toolability.AvailableAbilities;
import com.hbm.item.toolability.ItemToolAbility;
import static com.hbm.item.toolability.AvailableAbilities.Ability.*;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Exact source Schrabidium quartet; the real sword/armor already registered elsewhere remain untouched. */
public final class SchrabidiumToolsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<SchrabidiumToolItem> PICKAXE = ITEMS.register("schrabidium_pickaxe", () ->
        new SchrabidiumToolItem("schrabidium_pickaxe", 20, ItemToolAbility.Type.PICKAXE, abilities()));
    public static final DeferredItem<SchrabidiumToolItem> AXE = ITEMS.register("schrabidium_axe", () ->
        new SchrabidiumToolItem("schrabidium_axe", 25, ItemToolAbility.Type.AXE, abilities().add(BEHEADER, 0)));
    public static final DeferredItem<SchrabidiumToolItem> SHOVEL = ITEMS.register("schrabidium_shovel", () ->
        new SchrabidiumToolItem("schrabidium_shovel", 15, ItemToolAbility.Type.SHOVEL, abilities()));
    public static final DeferredItem<SchrabidiumHoeItem> HOE = ITEMS.register("schrabidium_hoe", SchrabidiumHoeItem::new);
    private static AvailableAbilities abilities() {
        return new AvailableAbilities().add(RADIATION, 0).add(HAMMER, 1).add(HAMMER_FLAT, 1).add(RECURSION, 6)
            .add(SILK, 0).add(LUCK, 4).add(SMELTER, 0).add(SHREDDER, 0);
    }
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(SchrabidiumToolsContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private SchrabidiumToolsContent() { }
}
