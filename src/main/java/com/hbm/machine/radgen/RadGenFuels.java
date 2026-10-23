package com.hbm.machine.radgen;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.nuclear.waste.SourceWasteIsotopes;
import com.hbm.registry.HbmContent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Unique actual TileEntityMachineRadGen.fuels table. Durations are literal source constants, not drum decay rates. */
public final class RadGenFuels {
    public record Fuel(String input, int metadata, int production, int duration, String output) {
        public ItemStack inputStack() { return stack(input, metadata); }
        public ItemStack outputStack() { return output == null ? ItemStack.EMPTY : stack(output, metadata); }
    }
    private static final List<Fuel> FUELS = build();
    private static List<Fuel> build() {
        var result = new ArrayList<Fuel>();
        for (int meta = 0; meta < SourceWasteIsotopes.ShortLived.values().length; meta++) {
            result.add(new Fuel("nuclear_waste_short", meta, 1500, 30 * 60 * 20, "nuclear_waste_short_depleted"));
            result.add(new Fuel("nuclear_waste_short_tiny", meta, 150, 3 * 60 * 20, "nuclear_waste_short_depleted_tiny"));
        }
        for (int meta = 0; meta < SourceWasteIsotopes.LongLived.values().length; meta++) {
            result.add(new Fuel("nuclear_waste_long", meta, 500, 2 * 60 * 60 * 20, "nuclear_waste_long_depleted"));
            result.add(new Fuel("nuclear_waste_long_tiny", meta, 50, 12 * 60 * 20, "nuclear_waste_long_depleted_tiny"));
        }
        result.add(new Fuel("scrap_nuclear", 0, 50, 5 * 60 * 20, null));
        result.add(new Fuel("gem_rad", 0, 25000, 30 * 60 * 20, "minecraft:diamond"));
        return List.copyOf(result);
    }
    public static List<Fuel> fuels() { return FUELS; }
    public static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue()); }
    public static ItemStack stack(String symbol, int meta) {
        var item = symbol.equals("minecraft:diamond") ? Items.DIAMOND : HbmContent.item(symbol);
        var result = new ItemStack(item);
        if (meta != 0 || result.has(HbmHazards.LEGACY_METADATA.get())) result.set(HbmHazards.LEGACY_METADATA.get(), meta);
        return result;
    }
    public static Fuel find(ItemStack input) {
        if (input.isEmpty()) return null;
        int meta = metadata(input);
        for (var fuel : FUELS) if (meta == fuel.metadata() && input.is(HbmContent.item(fuel.input()))) return fuel;
        return null;
    }
    private RadGenFuels() {}
}
