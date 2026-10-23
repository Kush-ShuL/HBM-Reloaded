package com.hbm.machine.oilextraction;
import com.hbm.fluid.*;import java.util.List;import net.minecraft.ChatFormatting;import net.minecraft.core.component.DataComponents;import net.minecraft.network.chat.Component;import net.minecraft.world.item.*;
public final class OilDrillItem extends BlockItem {
 public OilDrillItem(OilDrillBlock block){super(block,new Item.Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);var custom=stack.get(DataComponents.CUSTOM_DATA);if(custom==null||!custom.copyTag().contains("persistent"))return;var data=custom.copyTag().getCompound("persistent");lines.add(Component.literal((data.contains("powerExact")?data.getString("powerExact"):Long.toString(data.getLong("power")))+"HE").withStyle(ChatFormatting.GREEN));for(int i=0;i<2;i++){var tank=new FluidTank(HbmFluids.NONE,0);tank.readFromNBT(data,"t"+i);lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB ").append(tank.getTankType().displayName()).withStyle(ChatFormatting.YELLOW));}}
}
