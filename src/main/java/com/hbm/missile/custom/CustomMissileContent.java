package com.hbm.missile.custom;

import java.util.*;
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
