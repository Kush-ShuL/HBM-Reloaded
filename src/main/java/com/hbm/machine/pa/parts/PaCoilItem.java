package com.hbm.machine.pa.parts;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Actual ItemPACoil, independent of the older nine BlockHadronCoils. */
public final class PaCoilItem extends Item{
    public enum Type{
        GOLD(0,2200,0,2200,15),NIOBIUM(1500,8400,1500,8400,21),BSCCO(7500,15000,7500,15000,27),CHLOROPHYTE(14500,75000,14500,75000,51);
        public final int quadMin,quadMax,diMin,diMax,diDistMin;Type(int quadMin,int quadMax,int diMin,int diMax,int diDistMin){this.quadMin=quadMin;this.quadMax=quadMax;this.diMin=diMin;this.diMax=diMax;this.diDistMin=diDistMin;}
    }
    private final Type type;
    public PaCoilItem(Type type){super(new Item.Properties().stacksTo(1));this.type=Objects.requireNonNull(type);}
    public Type coil(){return type;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){var type=coil();lines.add(Component.translatable("info.hbm.pa_coil.quad").withStyle(ChatFormatting.BLUE).append(Component.literal(String.format(Locale.US,"%,d - %,d",type.quadMin,type.quadMax)).withStyle(ChatFormatting.RESET)));lines.add(Component.translatable("info.hbm.pa_coil.dipole").withStyle(ChatFormatting.BLUE).append(Component.literal(String.format(Locale.US,"%,d - %,d",type.diMin,type.diMax)).withStyle(ChatFormatting.RESET)));lines.add(Component.translatable("info.hbm.pa_coil.length").withStyle(ChatFormatting.BLUE).append(Component.literal(Integer.toString(type.diDistMin)).withStyle(ChatFormatting.RESET)));for(String name:List.of("minimum","maximum","both"))lines.add(Component.translatable("info.hbm.pa_coil."+name).withStyle(ChatFormatting.RED));}
}
