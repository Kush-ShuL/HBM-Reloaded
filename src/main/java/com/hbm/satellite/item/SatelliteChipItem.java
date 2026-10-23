package com.hbm.satellite.item;
import com.hbm.satellite.*;import java.util.List;import net.minecraft.ChatFormatting;import net.minecraft.network.chat.Component;import net.minecraft.world.item.*;
/** Source ItemSatChip, not an invented receiver/relay machine or a world satellite. */
public class SatelliteChipItem extends Item implements SatelliteFrequency {
 private final SatelliteKind legacyKind;public SatelliteChipItem(){this(null);}public SatelliteChipItem(SatelliteKind legacyKind){super(new Properties().stacksTo(1));this.legacyKind=legacyKind;}public SatelliteKind legacyKind(){return legacyKind;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> text,TooltipFlag flag){text.add(Component.translatable("satchip.frequency").append(": "+getFreq(stack)).withStyle(ChatFormatting.AQUA));if(legacyKind!=null){String key=switch(legacyKind){case XENIUM_RESONATOR->"resonator";case SPY->"mapper";case PRECISION_LASER->"laser";case MINER_ASTRO->"miner";case MINER_LUNAR->"lunar_miner";default->legacyKind.serializedName();};text.add(Component.translatable("satchip."+key));}}
}
