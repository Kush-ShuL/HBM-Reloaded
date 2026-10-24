package com.hbm.machine.radgen;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.nuclear.waste.SourceWasteIsotopes;
import com.hbm.item.nuclear.waste.IsotopicWasteContent;
import com.hbm.registry.HbmContent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Unique actual TileEntityMachineRadGen.fuels table. Durations are literal source constants, not drum decay rates. */
public final class RadGenFuels {
    public record Fuel(SourceWasteIsotopes.Form inputForm,SourceWasteIsotopes.Form outputForm,String input,int metadata,int production,int duration,String output) {
        public Fuel(SourceWasteIsotopes.Form input,int isotope,int production,int duration,SourceWasteIsotopes.Form output){this(input,output,null,isotope,production,duration,null);}
        public Fuel(String input,int metadata,int production,int duration,String output){this(null,null,input,metadata,production,duration,output);}
        public ItemStack inputStack(){return inputForm!=null?IsotopicWasteContent.stack(inputForm,metadata):stack(input,metadata);}
        public ItemStack outputStack(){return outputForm!=null?IsotopicWasteContent.stack(outputForm,metadata):output==null?ItemStack.EMPTY:stack(output,metadata);}
        public boolean matches(ItemStack stack){return inputForm!=null?SourceWasteIsotopes.is(stack,inputForm)&&SourceWasteIsotopes.isotope(stack)==metadata:stack.is(HbmContent.item(input))&&RadGenFuels.metadata(stack)==metadata;}
    }
    private static final List<Fuel> FUELS = build();
    private static List<Fuel> build() {
        var result = new ArrayList<Fuel>();
        for (int meta = 0; meta < SourceWasteIsotopes.ShortLived.values().length; meta++) {
            result.add(new Fuel(SourceWasteIsotopes.Form.SHORT, meta, 1500, 30 * 60 * 20, SourceWasteIsotopes.Form.SHORT_DEPLETED));
            result.add(new Fuel(SourceWasteIsotopes.Form.SHORT_TINY, meta, 150, 3 * 60 * 20, SourceWasteIsotopes.Form.SHORT_DEPLETED_TINY));
        }
        for (int meta = 0; meta < SourceWasteIsotopes.LongLived.values().length; meta++) {
            result.add(new Fuel(SourceWasteIsotopes.Form.LONG, meta, 500, 2 * 60 * 60 * 20, SourceWasteIsotopes.Form.LONG_DEPLETED));
            result.add(new Fuel(SourceWasteIsotopes.Form.LONG_TINY, meta, 50, 12 * 60 * 20, SourceWasteIsotopes.Form.LONG_DEPLETED_TINY));
        }
        result.add(new Fuel("scrap_nuclear", 0, 50, 5 * 60 * 20, null));
        result.add(new Fuel("gem_rad", 0, 25000, 30 * 60 * 20, "minecraft:diamond"));
        return List.copyOf(result);
    }
    public static List<Fuel> fuels() { return FUELS; }
    public static int metadata(ItemStack stack) { return SourceWasteIsotopes.form(stack)!=null?SourceWasteIsotopes.isotope(stack):stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue()); }
    public static ItemStack stack(String symbol, int meta) {
        var item = symbol.equals("minecraft:diamond") ? Items.DIAMOND : HbmContent.item(symbol);
        var result = new ItemStack(item);
        if (meta != 0 || result.has(HbmHazards.LEGACY_METADATA.get())) result.set(HbmHazards.LEGACY_METADATA.get(), meta);
        return result;
    }
    public static Fuel find(ItemStack input) {
        if (input.isEmpty()) return null;
        for (var fuel : FUELS) if (fuel.matches(input)) return fuel;
        return null;
    }
    private RadGenFuels() {}
}
