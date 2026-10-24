package com.hbm.item.nuclear.waste;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

    public enum Form {
        LONG("nuclear_waste_long",true),
        LONG_TINY("nuclear_waste_long_tiny",true),
        SHORT("nuclear_waste_short",false),
        SHORT_TINY("nuclear_waste_short_tiny",false),
        LONG_DEPLETED("nuclear_waste_long_depleted",true),
        LONG_DEPLETED_TINY("nuclear_waste_long_depleted_tiny",true),
        SHORT_DEPLETED("nuclear_waste_short_depleted",false),
        SHORT_DEPLETED_TINY("nuclear_waste_short_depleted_tiny",false);
        private final String symbol;private final boolean longLived;
        Form(String symbol,boolean longLived){this.symbol=symbol;this.longLived=longLived;}
        public String symbol(){return symbol;}public boolean longLived(){return longLived;}
        public int variants(){return longLived?LongLived.values().length:ShortLived.values().length;}
    }
    public static Form form(ItemStack stack){return stack.getItem() instanceof LongLivedWasteItem item?item.form():stack.getItem() instanceof ShortLivedWasteItem item?item.form():null;}
    public static boolean is(ItemStack stack,Form form){return form(stack)==form;}
    public static int isotope(ItemStack stack){if(stack.getItem() instanceof LongLivedWasteItem item)return item.isotope().ordinal();if(stack.getItem() instanceof ShortLivedWasteItem item)return item.isotope().ordinal();throw new IllegalArgumentException("Not an isotopic waste item");}

    public static Component tooltip(String isotope) {
        return Component.translatable("tooltip.hbm.nuclear_waste.isotope." + isotope).withStyle(ChatFormatting.ITALIC);
    }

    private SourceWasteIsotopes() {}
}
