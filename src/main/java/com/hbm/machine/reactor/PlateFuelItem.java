package com.hbm.machine.reactor;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

/** The five original response functions and original integer-event fuel lifetime. */
public final class PlateFuelItem extends Item {
    public enum Function { LOGARITHM, SQUARE_ROOT, NEGATIVE_QUADRATIC, LINEAR, PASSIVE }
    public final int lifeTime,reactivity;
    public final Function function;
    public PlateFuelItem(int lifeTime,Function function,int reactivity){super(new Properties().stacksTo(1));this.lifeTime=lifeTime;this.function=function;this.reactivity=reactivity;}
    public static int life(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("life");}
    public static void life(ItemStack stack,int value){CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.putInt("life",value));}
    public int react(ItemStack stack,int flux){
        if(function!=Function.PASSIVE)life(stack,life(stack)+flux);
        return switch(function){
            case LOGARITHM -> (int)(Math.log10(flux+1)*.5D*reactivity);
            case SQUARE_ROOT -> (int)(Math.sqrt(flux)*reactivity/10D);
            case NEGATIVE_QUADRATIC -> (int)Math.max((flux-(flux*flux/10000D))/100D*reactivity,0);
            case LINEAR -> (int)(flux/100D*reactivity);
            case PASSIVE -> {life(stack,life(stack)+reactivity);yield reactivity;}
        };
    }
    public String formula(){return switch(function){case LOGARITHM->"f(x) = log10(x + 1) * 0.5 * "+reactivity;case SQUARE_ROOT->"f(x) = sqrt(x) * "+reactivity+" / 10";case NEGATIVE_QUADRATIC->"f(x) = [x - (x² / 10000)] / 100 * "+reactivity;case LINEAR->"f(x) = x / 100 * "+reactivity;case PASSIVE->"f(x) = "+reactivity;};}
    @Override public boolean isBarVisible(ItemStack stack){return life(stack)>0;}
    @Override public int getBarWidth(ItemStack stack){return Math.round(13F-life(stack)*13F/lifeTime);}
    @Override public int getBarColor(ItemStack stack){float f=Math.max(0,1F-(float)life(stack)/lifeTime);return Mth.hsvToRgb(f/3F,1,1);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.reactor.plate").withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable(function==Function.PASSIVE?"tooltip.hbm.reactor.neutron_source":"tooltip.hbm.reactor.neutron_required").withStyle(function==Function.PASSIVE?ChatFormatting.GREEN:ChatFormatting.GRAY));lines.add(Component.literal("   "+formula()).withStyle(ChatFormatting.DARK_AQUA));lines.add(Component.translatable("tooltip.hbm.reactor.yield",lifeTime).withStyle(ChatFormatting.DARK_AQUA));}
}
