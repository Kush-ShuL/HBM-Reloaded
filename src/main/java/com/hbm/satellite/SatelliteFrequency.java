package com.hbm.satellite;
import com.hbm.machine.storage.CrateStackData;import net.minecraft.world.item.ItemStack;
/** Actual ISatChip freq NBT, shared by real payloads, interfaces and frequency relays. */
public interface SatelliteFrequency {
 default int getFreq(ItemStack stack){var tag=CrateStackData.data(stack);if(!stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA))CrateStackData.data(stack,tag);return tag.getInt("freq");}
 default void setFreq(ItemStack stack,int frequency){var tag=CrateStackData.data(stack);tag.putInt("freq",frequency);CrateStackData.data(stack,tag);}
 static int getFreqS(ItemStack stack){return !stack.isEmpty()&&stack.getItem() instanceof SatelliteFrequency chip?chip.getFreq(stack):0;}
 static void setFreqS(ItemStack stack,int frequency){if(!stack.isEmpty()&&stack.getItem() instanceof SatelliteFrequency chip)chip.setFreq(stack,frequency);}
}
