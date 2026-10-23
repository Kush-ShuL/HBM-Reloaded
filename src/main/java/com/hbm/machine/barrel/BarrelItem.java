package com.hbm.machine.barrel;

import com.hbm.fluid.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

public final class BarrelItem extends BlockItem {
    public BarrelItem(BarrelBlock block){super(block,new Item.Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> text,TooltipFlag flag){
        super.appendHoverText(stack,context,text,flag);var variant=((BarrelBlock)getBlock()).variant;
        text.add(Component.translatable("tooltip.hbm.barrel.capacity",String.format(java.util.Locale.US,"%,d",variant.capacity)).withStyle(ChatFormatting.AQUA));
        switch(variant){
            case PLASTIC->{line(text,"no_hot",ChatFormatting.YELLOW);line(text,"no_corrosive",ChatFormatting.YELLOW);line(text,"no_antimatter",ChatFormatting.YELLOW);}
            case CORRODED->line(text,"leaky",ChatFormatting.RED);
            case STEEL->{line(text,"hot",ChatFormatting.GREEN);line(text,"corrosive",ChatFormatting.GREEN);line(text,"no_highly_corrosive",ChatFormatting.YELLOW);line(text,"no_antimatter",ChatFormatting.YELLOW);}
            case ANTIMATTER->{line(text,"hot",ChatFormatting.GREEN);line(text,"highly_corrosive",ChatFormatting.GREEN);line(text,"antimatter",ChatFormatting.GREEN);}
            case TCALLOY->{line(text,"hot",ChatFormatting.GREEN);line(text,"highly_corrosive",ChatFormatting.GREEN);line(text,"no_antimatter",ChatFormatting.YELLOW);}
        }
        var custom=stack.get(DataComponents.CUSTOM_DATA);if(custom!=null&&custom.copyTag().contains("persistent")){var tank=new FluidTank(HbmFluids.NONE,0);tank.readFromNBT(custom.copyTag().getCompound("persistent"),"tank");text.add(Component.translatable("tooltip.hbm.barrel.contents",tank.getFill(),tank.getMaxFill(),tank.getTankType().displayName()).withStyle(ChatFormatting.YELLOW));}
    }
    private static void line(List<Component> text,String suffix,ChatFormatting color){text.add(Component.translatable("tooltip.hbm.barrel."+suffix).withStyle(color));}
}
