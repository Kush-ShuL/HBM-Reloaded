package com.hbm.registry;

import com.google.gson.Gson;
import com.hbm.item.FixedEnumItem;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class HbmEnumItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final Map<String, List<DeferredItem<FixedEnumItem>>> TABS = new LinkedHashMap<>();
    private static final Map<String, List<DeferredItem<FixedEnumItem>>> FAMILIES = new LinkedHashMap<>();
    private static final List<DeferredItem<FixedEnumItem>> ALL = new ArrayList<>();

    private HbmEnumItems() {}

    public static void register(IEventBus bus) {
        try (var stream = HbmEnumItems.class.getResourceAsStream("/hbm/enum-items.json")) {
            if (stream == null) throw new IllegalStateException("Missing enum-items.json: run tools/port_enum_items.py");
            var content = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Content.class);
            for (Spec spec : content.items()) {
                if (spec.id().equals("ammo_standard") || spec.id().equals("ammo_secret") || spec.id().equals("casing") || spec.id().equals("chunk_ore") || spec.id().equals("coke") || spec.id().equals("briquette") || spec.id().equals("powder_ash") || spec.id().equals("oil_tar") || spec.id().equals("weapon_mod_caliber") || spec.id().equals("ingot_metal") || spec.id().equals("item_secret") || spec.id().equals("plant_item") || spec.id().equals("circuit_star_piece") || spec.id().equals("page_of_")) continue; // Source descriptors are not registered physical items.
                // PWR's three material forms are registered by the actual reactor fuel owner.
                if (spec.id().equals("pwr_fuel_hot") || spec.id().equals("pwr_fuel_depleted")) continue;
                var family = new ArrayList<DeferredItem<FixedEnumItem>>();
                for (int ordinal = 0; ordinal < spec.names().size(); ordinal++) {
                    int sourceOrdinal = ordinal;
                    String variant = spec.names().get(ordinal);
                    var item = ITEMS.register(spec.id() + "_" + variant,
                            () -> new FixedEnumItem(spec.id(), variant, sourceOrdinal, spec.stackSize(), spec.multiName()));
                    family.add(item);
                    ALL.add(item);
                }
                FAMILIES.put(spec.id(), List.copyOf(family));
                if (spec.tab() != null) {
                    for (int ordinal : spec.order()) {
                        TABS.computeIfAbsent(spec.tab(), ignored -> new ArrayList<>()).add(family.get(ordinal));
                    }
                }
            }
        } catch (java.io.IOException error) {
            throw new IllegalStateException("Cannot load X5846 enum item definitions", error);
        }
        ITEMS.register(bus);
        com.hbm.item.casing.FlatCasingContent.register(bus);
        com.hbm.item.orechunk.FlatOreChunkContent.register(bus);
        com.hbm.item.coke.FlatCokeContent.register(bus);
        com.hbm.item.briquette.FlatBriquetteContent.register(bus);
        com.hbm.item.ash.FlatAshContent.register(bus);
        com.hbm.item.oiltar.FlatOilTarContent.register(bus);
        com.hbm.item.ingotmetal.FlatIngotMetalContent.register(bus);
        com.hbm.item.secret.FlatSecretContent.register(bus);
        com.hbm.item.circuitpiece.FlatCircuitPieceContent.register(bus);
        com.hbm.item.page.FlatPageContent.register(bus);
        com.hbm.weapon.sedna.gun.mods.caliber.FlatCaliberContent.register(bus);
    }

    public static List<String> tabNames() {
        return List.copyOf(TABS.keySet());
    }

    public static List<ItemStack> creativeStacks(String tab) {
        var stacks = new ArrayList<>(TABS.getOrDefault(tab, List.of()).stream().flatMap(item -> item.get().creativeStacks().stream()).toList());
        stacks.addAll(com.hbm.item.casing.FlatCasingContent.creativeStacks(tab));
        stacks.addAll(com.hbm.item.orechunk.FlatOreChunkContent.creativeStacks(tab));
        stacks.addAll(com.hbm.item.coke.FlatCokeContent.creativeStacks(tab));
        stacks.addAll(com.hbm.item.briquette.FlatBriquetteContent.creativeStacks(tab));
        stacks.addAll(com.hbm.item.ash.FlatAshContent.creativeStacks(tab));
        stacks.addAll(com.hbm.item.oiltar.FlatOilTarContent.creativeStacks(tab));
        stacks.addAll(com.hbm.weapon.sedna.gun.mods.caliber.FlatCaliberContent.creativeStacks(tab));
        return stacks;
    }

    /** Called by source factories using their original enum constants; no old registry ID is exposed. */
    public static FixedEnumItem variant(String family, int sourceOrdinal) {
        return FAMILIES.get(family).get(sourceOrdinal).get();
    }

    public static ItemStack stack(String family, int sourceOrdinal) {
        return new ItemStack(variant(family, sourceOrdinal));
    }

    public static List<DeferredItem<FixedEnumItem>> all() {
        return List.copyOf(ALL);
    }

    private record Content(List<Spec> items) {}
    private record Spec(String id, int stackSize, boolean multiName, List<String> names, List<Integer> order, String tab) {}
}
