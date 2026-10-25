package com.hbm.missile.custom;

import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual source item identities, including mp_chip_* whose registered names are mp_c_*. */
public final class CustomMissileContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final Map<String, DeferredItem<CustomMissilePartItem>> PARTS = registerParts();
    public static final DeferredItem<CustomMissileItem> MISSILE = ITEMS.register("missile_custom", CustomMissileItem::new);
    public static final DeferredItem<CustomMissileLootCrateItem> LOOT_10 = ITEMS.register("loot_10", () -> new CustomMissileLootCrateItem("10"));
    public static final DeferredItem<CustomMissileLootCrateItem> LOOT_15 = ITEMS.register("loot_15", () -> new CustomMissileLootCrateItem("15"));
    public static final DeferredItem<CustomMissileLootCrateItem> LOOT_MISC = ITEMS.register("loot_misc", () -> new CustomMissileLootCrateItem("misc"));
    private static Map<String, DeferredItem<CustomMissilePartItem>> registerParts() {
        var map = new LinkedHashMap<String, DeferredItem<CustomMissilePartItem>>();
        for (var definition : CustomMissileParts.DEFINITIONS) map.put(definition.symbol(), ITEMS.register(definition.id().getPath(), () -> new CustomMissilePartItem(definition)));
        return Collections.unmodifiableMap(map);
    }
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) {
        if (!tab.equals("missile")) return List.of();
        var result = new ArrayList<ItemStack>(); for (var item : PARTS.values()) result.add(new ItemStack(item.get()));
        for (var item : List.of(LOOT_10, LOOT_15, LOOT_MISC)) result.add(new ItemStack(item.get())); return result;
    }
    /** Source MissileTab appends these after the ordinary registered items have been ordered. */
    public static List<ItemStack> creativePresets() {
        return List.of(
                preset("Lil Bub", ChatFormatting.DARK_PURPLE, "mp_chip_3", "mp_warhead_10_he", "mp_fuselage_10_kerosene", "mp_stability_10_flat", "mp_thruster_10_kerosene"),
                preset("Long Boy", ChatFormatting.DARK_PURPLE, "mp_chip_3", "mp_warhead_10_incendiary", "mp_fuselage_10_long_solid", "mp_stability_10_space", "mp_thruster_10_solid"),
                preset("Uncle Kim", ChatFormatting.DARK_PURPLE, "mp_chip_3", "mp_warhead_10_nuclear", "mp_fuselage_10_15_kerosene", "mp_stability_15_flat", "mp_thruster_15_kerosene"),
                preset("Trotty's Toy Rocket", ChatFormatting.GREEN, "mp_chip_3", "mp_warhead_10_nuclear_large", "mp_fuselage_10_15_balefire", "mp_stability_15_flat", "mp_thruster_15_balefire_large"),
                preset("Stealthy Shark", ChatFormatting.DARK_PURPLE, "mp_chip_3", "mp_warhead_15_nuclear_shark", "mp_fuselage_15_kerosene_camo", "mp_stability_15_thin", "mp_thruster_15_kerosene_triple"),
                preset("Polite Lad", ChatFormatting.DARK_PURPLE, "mp_chip_3", "mp_warhead_15_he", "mp_fuselage_15_kerosene_polite", "mp_stability_15_thin", "mp_thruster_15_kerosene_dual"),
                preset("NERV's Leftover Missile", ChatFormatting.DARK_PURPLE, "mp_chip_3", "mp_warhead_15_n2", "mp_fuselage_15_solid_desh", "mp_stability_15_thin", "mp_thruster_15_solid_hexdecuple"),
                preset("Auntie Blackjack", ChatFormatting.RED, "mp_chip_5", "mp_warhead_15_boxcar", "mp_fuselage_15_kerosene_blackjack", "mp_stability_15_thin", "mp_thruster_15_kerosene"),
                preset("Hightower Missile", ChatFormatting.GREEN, "mp_chip_4", "mp_warhead_15_balefire", "mp_fuselage_15_20_kerosene_magnusson", null, "mp_thruster_20_kerosene"));
    }
    private static ItemStack preset(String name, ChatFormatting color, String chip, String warhead, String fuselage, String fins, String thruster) {
        var stack = CustomMissileItem.build(part(chip), part(warhead), part(fuselage), part(fins), part(thruster));
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name).withStyle(color));
        return stack;
    }
    private static ItemStack part(String symbol) {
        return symbol == null ? ItemStack.EMPTY : new ItemStack(PARTS.get(symbol).get());
    }
    public static List<CustomMissilePartItem> loot(String group) {
        var result = new ArrayList<CustomMissilePartItem>();
        for (var entry : PARTS.values()) {
            var item = entry.get(); var d = item.definition; if (d.rarity() == null) continue;
            String sourceGroup = d.type() == CustomMissileParts.PartType.FUSELAGE
                    ? d.top() == CustomMissileParts.PartSize.SIZE_10 ? "10" : d.top() == CustomMissileParts.PartSize.SIZE_15 ? "15" : "none" : "misc";
            if (sourceGroup.equals(group)) result.add(item);
        }
        return result;
    }
    private CustomMissileContent() {}
}
