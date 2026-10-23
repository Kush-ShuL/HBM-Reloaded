package com.hbm.item.nuclear.waste;

import com.hbm.hazard.HbmHazards;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Original ItemWasteLong/Short enum order and per-item recovery amounts, not decay timers. */
public final class SourceWasteIsotopes {
    public enum LongLived {
        URANIUM235("uranium235", 0, 0),
        URANIUM233("uranium233", 0, 50),
        NEPTUNIUM("neptunium", 0, 100),
        THORIUM("thorium", 0, 0),
        SCHRABIDIUM("schrabidium", 0, 250);

        public final String translation;
        public final int liquid, gas;
        LongLived(String translation, int liquid, int gas) {
            this.translation = translation;
            this.liquid = liquid;
            this.gas = gas;
        }
    }

    public enum ShortLived {
        URANIUM235("uranium235", 0, 100),
        URANIUM233("uranium233", 50, 100),
        NEPTUNIUM("neptunium", 150, 500),
        PLUTONIUM239("plutonium239", 250, 1000),
        PLUTONIUM240("plutonium240", 350, 1000),
        PLUTONIUM241("plutonium241", 500, 1000),
        AMERICIUM242("americium242", 750, 1000),
        SCHRABIDIUM("schrabidium", 1000, 1000);

        public final String translation;
        public final int liquid, gas;
        ShortLived(String translation, int liquid, int gas) {
            this.translation = translation;
            this.liquid = liquid;
            this.gas = gas;
        }
    }

    public static int longMetadata(ItemStack stack) {
        return Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)) % LongLived.values().length;
    }

    public static int shortMetadata(ItemStack stack) {
        return Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)) % ShortLived.values().length;
    }

    public static Component tooltip(String isotope) {
        return Component.translatable("tooltip.hbm.nuclear_waste.isotope." + isotope).withStyle(ChatFormatting.ITALIC);
    }

    public static List<ItemStack> stacks(Item item, int variants) {
        var result = new ArrayList<ItemStack>(variants);
        for (int meta = 0; meta < variants; meta++) {
            var stack = new ItemStack(item);
            stack.set(HbmHazards.LEGACY_METADATA.get(), meta);
            result.add(stack);
        }
        return List.copyOf(result);
    }

    private SourceWasteIsotopes() {}
}
