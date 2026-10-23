package com.hbm.machine.refinery;
import com.hbm.fluid.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source IPersistentInfoProvider: all five original saved tank lines, in source order. */
public final class RefineryItem extends BlockItem {
    public RefineryItem(RefineryBlock block){super(block,new Item.Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);var data=stack.get(DataComponents.CUSTOM_DATA);if(data==null||!data.copyTag().contains("persistent"))return;var tag=data.copyTag().getCompound("persistent");for(int i=0;i<5;i++){var tank=new FluidTank(HbmFluids.NONE,0);tank.readFromNBT(tag,""+i);lines.add(Component.translatable("tooltip.hbm.barrel.contents",tank.getFill(),tank.getMaxFill(),tank.getTankType().displayName()).withStyle(ChatFormatting.YELLOW));}}
}
