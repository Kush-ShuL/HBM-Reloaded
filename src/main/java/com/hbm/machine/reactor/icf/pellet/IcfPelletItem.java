package com.hbm.machine.reactor.icf.pellet;
import com.hbm.fluid.*;
import com.hbm.item.battery.BatteryItem;
import com.hbm.material.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
/** Original13 ICF fuels, two-color icon, source compound fuel/depletion and reaction arithmetic. */
public final class IcfPelletItem extends Item {
 public enum Fuel {
HYDROGEN(	0x4040FF,	1.00D,	0.85D,	1.00D),
		DEUTERIUM(	0x2828CB,	1.25D,	1.00D,	1.00D),
		TRITIUM(	0x000092,	1.50D,	1.00D,	1.05D),
		HELIUM3(	0xFFF09F,	1.75D,	1.00D,	1.25D),
		HELIUM4(	0xFF9B60,	2.00D,	1.00D,	1.50D),
		LITHIUM(	0xE9E9E9,	1.25D,	0.85D,	2.00D),
		BERYLLIUM(	0xA79D80,	2.00D,	1.00D,	2.50D),
		BORON(		0x697F89,	3.00D,	0.50D,	3.50D),
		CARBON(		0x454545,	2.00D,	1.00D,	5.00D),
		OXYGEN(		0xB4E2FF,	1.25D,	1.50D,	7.50D),
		SODIUM(		0xDFE4E7,	3.00D,	0.75D,	8.75D),
		
		CHLORINE(	0xDAE598,	2.50D,	1.00D,	9.25D),
		CALCIUM(	0xD2C7A9,	3.00D,	1.00D,	9.75D),
		
		;
 public final int color;public final double reactionMult,depletionSpeed,fusingDifficulty;
 Fuel(int color,double reaction,double depletion,double difficulty){this.color=color;reactionMult=reaction;depletionSpeed=depletion;fusingDifficulty=difficulty;}
 public Component displayName(){return Component.translatable("icffuel."+name().toLowerCase(Locale.US));}
 }
 public IcfPelletItem(){super(new Properties().stacksTo(1));}
 private static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
 public static Fuel getType(ItemStack stack,boolean first){var data=data(stack);if(!stack.has(DataComponents.CUSTOM_DATA))return first?Fuel.DEUTERIUM:Fuel.TRITIUM;return Fuel.values()[Math.abs(data.getByte(first?"type1":"type2")%Fuel.values().length)];}
 public static boolean isMuon(ItemStack stack){return data(stack).getBoolean("muon");}
 public static long getMaxDepletion(ItemStack stack){long base=50_000_000_000L;base/=getType(stack,true).depletionSpeed;base/=getType(stack,false).depletionSpeed;return base;}
 public static long getFusingDifficulty(ItemStack stack){long base=10_000_000L;base*=getType(stack,true).fusingDifficulty*getType(stack,false).fusingDifficulty;if(isMuon(stack))base/=4;return base;}
 public static long getDepletion(ItemStack stack){return data(stack).getLong("depletion");}
 public static long react(ItemStack stack,long heat){CustomData.update(DataComponents.CUSTOM_DATA,stack,data->data.putLong("depletion",data.getLong("depletion")+heat));return (long)(heat*getType(stack,true).reactionMult*getType(stack,false).reactionMult);}
 public static ItemStack setup(Fuel first,Fuel second,boolean muon){return setup(new ItemStack(IcfPelletPressContent.PELLET.get()),first,second,muon);}
 public static ItemStack setup(ItemStack stack,Fuel first,Fuel second,boolean muon){CustomData.update(DataComponents.CUSTOM_DATA,stack,data->{data.putByte("type1",(byte)first.ordinal());data.putByte("type2",(byte)second.ordinal());data.putBoolean("muon",muon);});return stack;}
 public static Fuel fluidFuel(HbmFluidType type){return switch(type.getName()){case "HYDROGEN"->Fuel.HYDROGEN;case "DEUTERIUM"->Fuel.DEUTERIUM;case "TRITIUM"->Fuel.TRITIUM;case "HELIUM3"->Fuel.HELIUM3;case "HELIUM4"->Fuel.HELIUM4;case "OXYGEN"->Fuel.OXYGEN;case "CHLORINE"->Fuel.CHLORINE;default->null;};}
 public static Fuel materialFuel(NtmMaterial material){return switch(material.symbol()){case "MAT_LITHIUM"->Fuel.LITHIUM;case "MAT_BERYLLIUM"->Fuel.BERYLLIUM;case "MAT_BORON"->Fuel.BORON;case "MAT_GRAPHITE"->Fuel.CARBON;case "MAT_SODIUM"->Fuel.SODIUM;case "MAT_CALCIUM"->Fuel.CALCIUM;default->null;};}
 public static List<ItemStack> creativeStacks(){return List.of(setup(Fuel.DEUTERIUM,Fuel.TRITIUM,false),setup(Fuel.HELIUM3,Fuel.HELIUM4,false),setup(Fuel.LITHIUM,Fuel.OXYGEN,false),setup(Fuel.SODIUM,Fuel.CHLORINE,true),setup(Fuel.BERYLLIUM,Fuel.CALCIUM,true));}
 public static double depletionRatio(ItemStack stack){return (double)getDepletion(stack)/getMaxDepletion(stack);}
 public static int color(ItemStack stack){int a=getType(stack,true).color,b=getType(stack,false).color;return (((a>>16&255)+(b>>16&255))/2)<<16|(((a>>8&255)+(b>>8&255))/2)<<8|((a&255)+(b&255))/2;}
 @Override public boolean isBarVisible(ItemStack stack){return depletionRatio(stack)>0;}
 @Override public int getBarWidth(ItemStack stack){return Math.round(13-(float)depletionRatio(stack)*13);}
 @Override public int getBarColor(ItemStack stack){return net.minecraft.util.Mth.hsvToRgb(Math.max(0F,1F-(float)depletionRatio(stack))/3F,1,1);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.icf.depletion",String.format(Locale.US,"%.1f",depletionRatio(stack)*100)).withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("tooltip.hbm.icf.fuel",getType(stack,true).displayName(),getType(stack,false).displayName()).withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable("tooltip.hbm.icf.heat",BatteryItem.shortNumber(getFusingDifficulty(stack))).withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable("tooltip.hbm.icf.reactivity",(int)(getType(stack,true).reactionMult*getType(stack,false).reactionMult*100)/100D).withStyle(ChatFormatting.YELLOW));if(isMuon(stack))lines.add(Component.translatable("tooltip.hbm.icf.muon").withStyle(ChatFormatting.DARK_AQUA));}
}
