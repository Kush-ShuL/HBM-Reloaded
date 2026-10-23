package com.hbm.machine.reactor.pile;

import com.hbm.hazard.HbmHazards;
import java.util.*;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

/** Genuine nine-kind ItemPileRodMK2 with source double depletion, exact reaction and fresh-stack transmutation. */
public final class PileFuelItem extends Item {
    public static final String KEY_NBT_DEPLETION="depletion";
    public static Function<Component,List<Component>> WRAP=component->List.of(component);
    public PileFuelItem(){super(new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    public static PileFuelType type(ItemStack stack){return PileFuelType.from(metadata(stack));}
    public ItemStack stack(PileFuelType type){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),type.ordinal());return stack;}
    @Override public String getDescriptionId(ItemStack stack){return "item.hbm.pile_rod."+type(stack).serializedName();}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.pile_rod."+type(stack).serializedName()+".name");}
    public static double getDepletion(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getDouble(KEY_NBT_DEPLETION);}
    public static void setDepletion(ItemStack stack,double depletion){CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.putDouble(KEY_NBT_DEPLETION,depletion));}
    public static double getDepletionPercent(ItemStack stack){if(stack==null||stack.isEmpty())return 0;double life=type(stack).life;return life<=0?0:getDepletion(stack)/life*100;}
    public static double getDurabilityForDisplay(ItemStack stack){double life=type(stack).life;return life<=0?0:getDepletion(stack)/life;}
    public static double getReactivity(ItemStack stack,double inFlux){var type=type(stack);double output=type.neutronSource;if(type.reactionMult>0)output+=(Math.sqrt(inFlux+1D/((inFlux+2D)*(inFlux+2D)))-1D/(inFlux+2D))*type.reactionMult;return output;}
    public static double getHeatPerNeutron(ItemStack stack){return type(stack).heatMult;}
    public static ItemStack react(ItemStack stack,double inFlux){var type=type(stack);if(type.life<=0)return stack;double depletion=getDepletion(stack)+inFlux;if(depletion<type.life){setDepletion(stack,depletion);return stack;}var result=new ItemStack(stack.getItem(),1);result.set(HbmHazards.LEGACY_METADATA.get(),type.turnsInto);return result;}
    @Override public boolean isBarVisible(ItemStack stack){return getDurabilityForDisplay(stack)>0;}
    @Override public int getBarWidth(ItemStack stack){return (int)Math.round(13-getDurabilityForDisplay(stack)*13);}
    @Override public int getBarColor(ItemStack stack){int green=(int)Math.round(255-getDurabilityForDisplay(stack)*255);return(255-green)<<16|green<<8;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var type=type(stack);if(type.life>0){lines.add(Component.translatable("tooltip.hbm.pile_rod.life",(int)Math.round(type.life)));double depletion=getDepletionPercent(stack);if(depletion>0)lines.add(Component.translatable("tooltip.hbm.pile_rod.depletion",(int)Math.round(depletion)));}for(var line:WRAP.apply(Component.translatable("item.pile_rod."+type.serializedName()+".desc")))lines.add(line.copy().withStyle(ChatFormatting.YELLOW));}
}
