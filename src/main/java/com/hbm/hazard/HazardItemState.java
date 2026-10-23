package com.hbm.hazard;

import java.util.OptionalDouble;
import net.minecraft.world.item.ItemStack;

/** Stateful fuel items can supply live values instead of the source-compatible custom-data adapter. */
public interface HazardItemState {
    default OptionalDouble hazardDepletion(ItemStack stack){return OptionalDouble.empty();}
    default OptionalDouble hazardEnrichment(ItemStack stack){return OptionalDouble.empty();}
    default OptionalDouble hazardXenon(ItemStack stack){return OptionalDouble.empty();}
    default OptionalDouble hazardHullHeat(ItemStack stack){return OptionalDouble.empty();}
}
