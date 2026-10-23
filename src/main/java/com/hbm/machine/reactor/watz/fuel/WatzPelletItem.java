package com.hbm.machine.reactor.watz.fuel;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.EnumMultiItem;
import java.util.*;
import java.util.stream.IntStream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/** Real ItemWatzPellet lazy source yield NBT, crafted reset, depletion bar and actual fuel-function descriptions. */
public final class WatzPelletItem extends EnumMultiItem {
    public static final List<String> NAMES=Arrays.stream(WatzFuelType.values()).map(WatzFuelType::serialized).toList();
    private final boolean depleted;
    public WatzPelletItem(boolean depleted){super(depleted?"watz_pellet_depleted":"watz_pellet",16,true,NAMES,IntStream.range(0,NAMES.size()).boxed().toList());this.depleted=depleted;}
    public static int metadata(ItemStack stack){return Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)%WatzFuelType.values().length);}
    public static WatzFuelType type(ItemStack stack){return WatzFuelType.fromMeta(metadata(stack));}
    private static void defaults(ItemStack stack){var data=new CompoundTag();data.putDouble("yield",type(stack).yield);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}
    public static double getDouble(ItemStack stack,String key){if(!stack.has(DataComponents.CUSTOM_DATA))defaults(stack);return stack.get(DataComponents.CUSTOM_DATA).copyTag().getDouble(key);}
    public static void setDouble(ItemStack stack,String key,double value){if(!stack.has(DataComponents.CUSTOM_DATA))defaults(stack);var data=stack.get(DataComponents.CUSTOM_DATA).copyTag();data.putDouble(key,value);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}
    public static double getYield(ItemStack stack){return getDouble(stack,"yield");}public static void setYield(ItemStack stack,double value){setDouble(stack,"yield",value);}public static double getEnrichment(ItemStack stack){return getYield(stack)/type(stack).yield;}
    @Override public void onCraftedBy(ItemStack stack,Level level,Player player){super.onCraftedBy(stack,level,player);if(!depleted)defaults(stack);}
    @Override public boolean isBarVisible(ItemStack stack){return !depleted&&1-getEnrichment(stack)>0;}@Override public int getBarWidth(ItemStack stack){return(int)Math.round(13*getEnrichment(stack));}@Override public int getBarColor(ItemStack stack){return Mth.hsvToRgb((float)getEnrichment(stack)/3,1,1);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(depleted)return;var type=type(stack);lines.add(Component.translatable("tooltip.hbm.watz.depletion",String.format(Locale.US,"%.1f",(1-getEnrichment(stack))*100)).withStyle(ChatFormatting.GREEN));if(type.passive>0){lines.add(Component.translatable("tooltip.hbm.watz.base_flux",type.passive).withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("tooltip.hbm.watz.self_igniting").withStyle(ChatFormatting.RED));}if(type.heatEmission>0)lines.add(Component.translatable("tooltip.hbm.watz.heat",type.heatEmission).withStyle(ChatFormatting.GOLD));if(type.burnFunc!=null){lines.add(Component.translatable("tooltip.hbm.watz.reaction",type.burnFunc.label()).withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("tooltip.hbm.watz.type",type.burnFunc.danger()).withStyle(ChatFormatting.GOLD));}if(type.heatDiv!=null)lines.add(Component.translatable("tooltip.hbm.watz.thermal",type.heatDiv.label()).withStyle(ChatFormatting.GOLD));if(type.absorbFunc!=null)lines.add(Component.translatable("tooltip.hbm.watz.capture",type.absorbFunc.label()).withStyle(ChatFormatting.GOLD));}
}
