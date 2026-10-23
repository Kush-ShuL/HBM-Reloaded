package com.hbm.machine.bat9000;

import com.hbm.fluid.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

public final class Bat9000Item extends BlockItem {
    public Bat9000Item(Bat9000Block block){super(block,new Item.Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> text,TooltipFlag flag){super.appendHoverText(stack,context,text,flag);var custom=stack.get(DataComponents.CUSTOM_DATA);if(custom!=null&&custom.copyTag().contains("persistent")){var tank=new FluidTank(HbmFluids.NONE,0);tank.readFromNBT(custom.copyTag().getCompound("persistent"),"tank");text.add(Component.translatable("tooltip.hbm.barrel.contents",tank.getFill(),tank.getMaxFill(),tank.getTankType().displayName()).withStyle(ChatFormatting.YELLOW));}}
}
