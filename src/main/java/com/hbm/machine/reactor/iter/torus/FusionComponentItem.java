package com.hbm.machine.reactor.iter.torus;
import com.hbm.hazard.HbmHazards;
import net.minecraft.world.item.*;
/** Source BlockToolConversion's item variantnames. */
public final class FusionComponentItem extends BlockItem {
    public FusionComponentItem(){super(TorusContent.COMPONENT.get(),new Properties());}
    @Override public String getDescriptionId(ItemStack stack){int metadata=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);return "block.hbm.fusion_component"+switch(metadata){case 1->".bscco_welded";case 2->".blanket";case 3->".motor";default->"";};}
}
