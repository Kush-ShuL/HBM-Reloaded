package com.hbm.machine.pa.parts;
import com.hbm.item.EnumMultiItem;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Actual ItemPACoil, independent of the older nine BlockHadronCoils. */
public final class PaCoilItem extends EnumMultiItem{
    public enum Type{
        GOLD(0,2200,0,2200,15),NIOBIUM(1500,8400,1500,8400,21),BSCCO(7500,15000,7500,15000,27),CHLOROPHYTE(14500,75000,14500,75000,51);
        public final int quadMin,quadMax,diMin,diMax,diDistMin;Type(int quadMin,int quadMax,int diMin,int diMax,int diDistMin){this.quadMin=quadMin;this.quadMax=quadMax;this.diMin=diMin;this.diMax=diMax;this.diDistMin=diDistMin;}
    }
    public PaCoilItem(){super("pa_coil",1,true,List.of("gold","niobium","bscco","chlorophyte"),List.of(0,1,2,3));}
    public Type coil(ItemStack stack){return Type.values()[ordinal(stack)];}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){var type=coil(stack);lines.add(Component.translatable("info.hbm.pa_coil.quad").withStyle(ChatFormatting.BLUE).append(Component.literal(String.format(Locale.US,"%,d - %,d",type.quadMin,type.quadMax)).withStyle(ChatFormatting.RESET)));lines.add(Component.translatable("info.hbm.pa_coil.dipole").withStyle(ChatFormatting.BLUE).append(Component.literal(String.format(Locale.US,"%,d - %,d",type.diMin,type.diMax)).withStyle(ChatFormatting.RESET)));lines.add(Component.translatable("info.hbm.pa_coil.length").withStyle(ChatFormatting.BLUE).append(Component.literal(Integer.toString(type.diDistMin)).withStyle(ChatFormatting.RESET)));for(String name:List.of("minimum","maximum","both"))lines.add(Component.translatable("info.hbm.pa_coil."+name).withStyle(ChatFormatting.RED));}
}
