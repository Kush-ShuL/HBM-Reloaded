package com.hbm.item.toolability.baseextras;

import com.hbm.block.junglemechanics.JungleWeaponTiers;
import com.hbm.item.toolability.AvailableAbilities;
import com.hbm.item.toolability.ItemToolAbility;
import com.hbm.item.toolability.SteelHoeItem;
import com.hbm.weapon.legacymelee.LegacyMeleeMaterials;
import static com.hbm.item.toolability.AvailableAbilities.Ability.*;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Exact original remaining ordinary tools; material/ability/mining engines stay unique. */
public final class BaseAbilityToolsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemToolAbility> ALLOY_PICKAXE = ITEMS.register("alloy_pickaxe", () ->
        new ItemToolAbility("alloy_pickaxe", 5F, 0, LegacyMeleeMaterials.ALLOY, ItemToolAbility.Type.PICKAXE,
            new AvailableAbilities().add(RECURSION, 0)));
    public static final DeferredItem<ItemToolAbility> ALLOY_AXE = ITEMS.register("alloy_axe", () ->
        new ItemToolAbility("alloy_axe", 7F, 0, LegacyMeleeMaterials.ALLOY, ItemToolAbility.Type.AXE,
            new AvailableAbilities().add(RECURSION, 0).add(BEHEADER, 0)));
    public static final DeferredItem<ItemToolAbility> ALLOY_SHOVEL = ITEMS.register("alloy_shovel", () ->
        new ItemToolAbility("alloy_shovel", 4F, 0, LegacyMeleeMaterials.ALLOY, ItemToolAbility.Type.SHOVEL,
            new AvailableAbilities().add(RECURSION, 0)));
    public static final DeferredItem<SteelHoeItem> ALLOY_HOE = ITEMS.register("alloy_hoe", () -> new SteelHoeItem(LegacyMeleeMaterials.ALLOY));
    public static final DeferredItem<ItemToolAbility> CMB_PICKAXE = ITEMS.register("cmb_pickaxe", () ->
        new ItemToolAbility("cmb_pickaxe", 10F, 0, JungleWeaponTiers.CMB, ItemToolAbility.Type.PICKAXE, cmb()));
    public static final DeferredItem<ItemToolAbility> CMB_AXE = ITEMS.register("cmb_axe", () ->
        new ItemToolAbility("cmb_axe", 30F, 0, JungleWeaponTiers.CMB, ItemToolAbility.Type.AXE, cmb().add(BEHEADER, 0)));
    public static final DeferredItem<ItemToolAbility> CMB_SHOVEL = ITEMS.register("cmb_shovel", () ->
        new ItemToolAbility("cmb_shovel", 8F, 0, JungleWeaponTiers.CMB, ItemToolAbility.Type.SHOVEL, cmb()));
    private static AvailableAbilities cmb() { return new AvailableAbilities().add(RECURSION, 2).add(SMELTER, 0).add(SILK, 0).add(LUCK, 2); }
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(BaseAbilityToolsContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private BaseAbilityToolsContent() { }
}
