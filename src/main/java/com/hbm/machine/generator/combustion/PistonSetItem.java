package com.hbm.machine.generator.combustion;

import com.hbm.fluid.FluidTraits;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** One immutable source ItemPistons type; engine fuel/throttle state belongs to its block entity. */
public final class PistonSetItem extends Item {
    public static final List<String> NAMES=List.of("steel","dura","desh","starmetal");
    private static final String[] GRADE_KEYS={"low","medium","high","aviation","gaseous"};
    private static final double[][] EFF={{1,.75,.25,0,0},{.5,1,.9,.5,0},{0,.5,1,.75,0},{.5,.75,1,.9,.5}};
    private final int pistonType;
    public PistonSetItem(int pistonType){super(new Properties().stacksTo(1));if(pistonType<0||pistonType>=NAMES.size())throw new IllegalArgumentException("Not a source piston type: "+pistonType);this.pistonType=pistonType;}
    public int type(){return pistonType;}
    public static double efficiency(ItemStack stack,FluidTraits.FuelGrade grade){if(!(stack.getItem() instanceof PistonSetItem piston))throw new IllegalArgumentException("Not a piston-set item");return EFF[piston.pistonType][grade.ordinal()];}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.piston_set_"+NAMES.get(pistonType)+".name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.piston_set.efficiency").withStyle(ChatFormatting.YELLOW));for(int i=0;i<5;i++)lines.add(Component.literal("-").withStyle(ChatFormatting.YELLOW).append(Component.translatable("hbmfluid.trait.fuel."+GRADE_KEYS[i])).append(Component.literal(": ").withStyle(ChatFormatting.YELLOW)).append(Component.literal((int)(EFF[pistonType][i]*100)+"%").withStyle(ChatFormatting.RED)));}
}
