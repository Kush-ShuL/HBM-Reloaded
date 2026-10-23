package com.hbm.client.oilfracking;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.oilfracking.FrackingConfig;
import com.hbm.machine.oilextraction.OilDrillEntity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
/** Original two reservoir kinds, exact real FRACKSOL consumption and source per-operation gas/depletion ranges. */
final class FrackingViews {
    record Extraction(boolean bedrock,ItemStack deposit,ItemStack depleted,ItemStack solution,ItemStack oil,ItemStack gas,FrackingConfig.Values config){}
    static List<Extraction> recipes(){var c=FrackingConfig.get();return List.of(entry(false,c),entry(true,c));}
    private static Extraction entry(boolean bedrock,FrackingConfig.Values c){return new Extraction(bedrock,new ItemStack(OilDrillEntity.block(bedrock?"ore_bedrock_oil":"ore_oil")),new ItemStack(OilDrillEntity.block(bedrock?"ore_bedrock_oil":"ore_oil_empty")),FluidIconItem.make(HbmFluids.fromName("FRACKSOL"),c.solution()),FluidIconItem.make(HbmFluids.OIL,bedrock?c.bedrockOil():c.oil()),FluidIconItem.make(HbmFluids.GAS,bedrock?c.bedrockGasMin():c.gasMin()),c);}
    static Component gas(Extraction r){return Component.translatable("gui.hbm.oil_drill.gas_range",r.bedrock?r.config.bedrockGasMin():r.config.gasMin(),r.bedrock?r.config.bedrockGasMax():r.config.gasMax());}
    static Component depletion(Extraction r){return Component.translatable(r.bedrock?"gui.hbm.fracking.bedrock":"gui.hbm.oil_drill.depletion",r.config.drain()*100);}
}
