package com.hbm.world.structure.dependencies.hadron;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Original ItemBlockBase delegates the coil's own tooltip directly; no Shift requirement. */
public final class HadronCoilItem extends BlockItem {
 public HadronCoilItem(HadronCoilBlock block){super(block,new Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){super.appendHoverText(stack,context,lines,flags);lines.add(Component.translatable("info.coil").append(": "+String.format(Locale.US,"%,d",((HadronCoilBlock)getBlock()).factor)));}
}
