package com.hbm.machine.reactor.watz;
import com.hbm.hazard.HbmHazards;
import net.minecraft.world.item.*;
/** Actual ItemBlockMulti two creative subtypes and source variant names. */
public final class WatzCasingItem extends BlockItem {
    public WatzCasingItem(){super(WatzContent.CASING.get(),new Properties());}
    @Override public String getDescriptionId(ItemStack stack){return "block.hbm.watz_end"+(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)==1?"_bolted":"");}
}
