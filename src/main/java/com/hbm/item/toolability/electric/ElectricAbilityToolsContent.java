package com.hbm.item.toolability.electric;

import com.hbm.item.toolability.AvailableAbilities;
import com.hbm.item.toolability.ItemToolAbility;
import com.hbm.item.toolability.ItemToolAbilityPower;
import com.hbm.weapon.legacymelee.LegacyMeleeMaterials;
import static com.hbm.item.toolability.AvailableAbilities.Ability.*;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Three original electric tools, not another powered sword or tool-ability engine. */
public final class ElectricAbilityToolsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static AvailableAbilities tools() {
        return new AvailableAbilities().add(HAMMER, 0).add(HAMMER_FLAT, 0).add(RECURSION, 2).add(SILK, 0).add(LUCK, 1);
    }
    public static final DeferredItem<ItemToolAbilityPower> PICKAXE = ITEMS.register("elec_pickaxe", () ->
        new ItemToolAbilityPower("elec_pickaxe", 6F, 0, LegacyMeleeMaterials.ELECTRIC, ItemToolAbility.Type.PICKAXE, tools(), 500_000, 1_000, 100));
    public static final DeferredItem<ItemToolAbilityPower> AXE = ITEMS.register("elec_axe", () ->
        new ItemToolAbilityPower("elec_axe", 10F, 0, LegacyMeleeMaterials.ELECTRIC, ItemToolAbility.Type.AXE,
            tools().add(CHAINSAW, 0).add(BEHEADER, 0), 500_000, 1_000, 100).setShears());
    public static final DeferredItem<ItemToolAbilityPower> SHOVEL = ITEMS.register("elec_shovel", () ->
        new ItemToolAbilityPower("elec_shovel", 5F, 0, LegacyMeleeMaterials.ELECTRIC, ItemToolAbility.Type.SHOVEL, tools(), 500_000, 1_000, 100));
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(ElectricAbilityToolsContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private ElectricAbilityToolsContent() { }
}
