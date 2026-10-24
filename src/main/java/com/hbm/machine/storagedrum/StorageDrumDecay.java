package com.hbm.machine.storagedrum;

import com.hbm.config.GeneralModes;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.nuclear.waste.SourceWasteIsotopes;
import com.hbm.item.nuclear.waste.IsotopicWasteContent;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/** The real drum's per-slot RNG branches, also consumed by its original NEI row adapter. No independent ticker. */
public final class StorageDrumDecay {
    public record Rule(SourceWasteIsotopes.Form inputForm,SourceWasteIsotopes.Form outputForm,String input,String output,boolean longLived,int divisor,int isotopeFamily) {
        public Rule(SourceWasteIsotopes.Form input,SourceWasteIsotopes.Form output,boolean longLived,int divisor,int isotopeFamily){this(input,output,null,null,longLived,divisor,isotopeFamily);}
        public Rule(String input,String output,boolean longLived,int divisor,int isotopeFamily){this(null,null,input,output,longLived,divisor,isotopeFamily);}
        public boolean matches(ItemStack stack){return inputForm!=null?SourceWasteIsotopes.is(stack,inputForm):stack.is(HbmContent.item(input));}
        public ItemStack inputStack(int isotope){return inputForm!=null?IsotopicWasteContent.stack(inputForm,isotope):stack(input,isotope);}
        public ItemStack outputStack(int isotope){return outputForm!=null?IsotopicWasteContent.stack(outputForm,isotope):stack(output,isotope);}
        public int chance() { return (longLived ? longChance() : shortChance()) / divisor; }
        public int variants() { return isotopeFamily == 1 ? 5 : isotopeFamily == 2 ? 8 : 1; }
    }
    public record Result(ItemStack stack, int liquid, int gas) {}

    public static final List<Rule> RULES = List.of(
        new Rule(SourceWasteIsotopes.Form.LONG, SourceWasteIsotopes.Form.LONG_DEPLETED, true, 1, 1),
        new Rule(SourceWasteIsotopes.Form.LONG_TINY, SourceWasteIsotopes.Form.LONG_DEPLETED_TINY, true, 10, 1),
        new Rule(SourceWasteIsotopes.Form.SHORT, SourceWasteIsotopes.Form.SHORT_DEPLETED, false, 1, 2),
        new Rule(SourceWasteIsotopes.Form.SHORT_TINY, SourceWasteIsotopes.Form.SHORT_DEPLETED_TINY, false, 10, 2),
        new Rule("ingot_au198", "ingot_mercury", false, 20, 0),
        new Rule("nugget_au198", "nugget_mercury", false, 100, 0),
        new Rule("ingot_pb209", "ingot_bismuth", false, 10, 0),
        new Rule("nugget_pb209", "nugget_bismuth", false, 50, 0),
        new Rule("powder_sr90", "powder_zirconium", false, 10, 0),
        new Rule("nugget_sr90", "nugget_zirconium", false, 50, 0)
    );

    public static int longChance() {
        return GeneralModes.mode528() ? 15 * 60 * 60 * 20
            : GeneralModes.lbsm("LBSM_shortDecay") ? 15 * 60 * 20 : 3 * 60 * 60 * 20;
    }

    public static int shortChance() {
        return GeneralModes.mode528() ? 3 * 60 * 60 * 20
            : GeneralModes.lbsm("LBSM_shortDecay") ? 3 * 60 * 20 : 15 * 60 * 20;
    }

    public static int metadata(ItemStack stack) {
        return SourceWasteIsotopes.form(stack)!=null?SourceWasteIsotopes.isotope(stack):stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue());
    }

    public static ItemStack stack(String symbol, int meta) {
        var result = new ItemStack(HbmContent.item(symbol));
        result.set(HbmHazards.LEGACY_METADATA.get(), meta);
        return result;
    }

    public static Result decay(ItemStack original, RandomSource random) {
        for (var rule : RULES) {
            if (!rule.matches(original)) continue;
            // Preserve short-circuit RNG: exactly one nextInt only for an eligible original identity.
            if (random.nextInt(rule.chance()) != 0) return null;
            int meta = metadata(original), liquid = 0, gas = 0;
            if (rule.isotopeFamily() == 1) {
                var isotope = SourceWasteIsotopes.LongLived.values()[Math.abs(meta) % 5];
                liquid = isotope.liquid / rule.divisor();
                gas = isotope.gas / rule.divisor();
            } else if (rule.isotopeFamily() == 2) {
                // X5846 really rectifies short waste with ItemWasteLong before indexing its short enum.
                var isotope = SourceWasteIsotopes.ShortLived.values()[Math.abs(meta) % 5];
                liquid = isotope.liquid / rule.divisor();
                gas = isotope.gas / rule.divisor();
            }
            // Source discards input tags/count and constructs one output with the same original isotope identity.
            return new Result(rule.outputStack(meta), liquid, gas);
        }
        return null;
    }

    private StorageDrumDecay() {}
}
