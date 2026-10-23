package com.hbm.machine.oilhydrotreater;
import com.hbm.fluid.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source IPersistentInfoProvider: all four original saved tank lines, in source order. */
public final class HydrotreaterItem extends BlockItem {
    public HydrotreaterItem(HydrotreaterBlock block){super(block,new Item.Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);var data=stack.get(DataComponents.CUSTOM_DATA);if(data==null||!data.copyTag().contains("persistent"))return;var tag=data.copyTag().getCompound("persistent");for(int i=0;i<4;i++){var tank=new FluidTank(HbmFluids.NONE,0);tank.readFromNBT(tag,""+i);lines.add(Component.translatable("tooltip.hbm.barrel.contents",tank.getFill(),tank.getMaxFill(),tank.getTankType().displayName()).withStyle(ChatFormatting.YELLOW));}}
}
