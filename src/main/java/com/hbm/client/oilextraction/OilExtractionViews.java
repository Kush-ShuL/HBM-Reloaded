package com.hbm.client.oilextraction;
import com.hbm.fluid.*;import com.hbm.item.fluid.FluidIconItem;import com.hbm.machine.oilextraction.*;import java.util.*;import net.minecraft.network.chat.Component;import net.minecraft.world.item.ItemStack;
/** Informational native-fluid extraction display; never creates a fake crafting/fluid recipe. */
final class OilExtractionViews {
 record Extraction(OilDrillKind kind,ItemStack deposit,ItemStack depleted,ItemStack oil,ItemStack gas,OilDrillConfig.Values config){}
 static List<Extraction> recipes(){return Arrays.stream(OilDrillKind.values()).map(kind->{var c=OilDrillConfig.get(kind);return new Extraction(kind,new ItemStack(OilDrillEntity.block("ore_oil")),new ItemStack(OilDrillEntity.block("ore_oil_empty")),FluidIconItem.make(HbmFluids.fromName("OIL"),c.oil()),FluidIconItem.make(HbmFluids.fromName("GAS"),c.gasMin()),c);}).toList();}
 static Component gas(Extraction recipe){return Component.translatable("gui.hbm.oil_drill.gas_range",recipe.config.gasMin(),recipe.config.gasMax());}static Component depletion(Extraction recipe){return Component.translatable("gui.hbm.oil_drill.depletion",recipe.config.drain()*100);}
}
