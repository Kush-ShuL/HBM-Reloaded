package com.hbm.machine.generator.combustion;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.EnumMultiItem;
import com.hbm.fluid.FluidTraits;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Original ItemPistons, including all four exact five-grade efficiencies. */
public final class PistonSetItem extends EnumMultiItem {
    public static final List<String> NAMES=List.of("steel","dura","desh","starmetal");
    private static final String[] GRADE_KEYS={"low","medium","high","aviation","gaseous"};
    private static final double[][] EFF={{1,.75,.25,0,0},{.5,1,.9,.5,0},{0,.5,1,.75,0},{.5,.75,1,.9,.5}};
    public PistonSetItem(){super("piston_set",1,true,NAMES,List.of(0,1,2,3));}
    public static int metadata(ItemStack stack){return Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)%4);}
    public static double efficiency(ItemStack stack,FluidTraits.FuelGrade grade){return EFF[metadata(stack)][grade.ordinal()];}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.piston_set_"+NAMES.get(metadata(stack))+".name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.piston_set.efficiency").withStyle(ChatFormatting.YELLOW));for(int i=0;i<5;i++)lines.add(Component.literal("-").withStyle(ChatFormatting.YELLOW).append(Component.translatable("hbmfluid.trait.fuel."+GRADE_KEYS[i])).append(Component.literal(": ").withStyle(ChatFormatting.YELLOW)).append(Component.literal((int)(EFF[metadata(stack)][i]*100)+"%").withStyle(ChatFormatting.RED)));}
}
