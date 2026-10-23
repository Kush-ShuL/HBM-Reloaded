package com.hbm.block.meteordeco;
import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
public final class MetalFenceItem extends BlockItem {
 public MetalFenceItem(MetalFenceBlock block){super(block,new Properties());}@Override public Component getName(ItemStack stack){return Component.translatable(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)==1?"block.hbm.fence_metal_post":"block.hbm.fence_metal");}
}
