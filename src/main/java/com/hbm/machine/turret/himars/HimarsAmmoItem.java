package com.hbm.machine.turret.himars;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Actual ItemAmmoHIMARS, eight ordered types, one pod per stack; metadata is independent of durability. */
public final class HimarsAmmoItem extends Item {
 public HimarsAmmoItem(){super(new Properties().stacksTo(1));}
 public static int rawMetadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
 public static int metadata(ItemStack stack){int value=rawMetadata(stack);return value==Integer.MIN_VALUE?0:Math.abs(value)%8;}
 public static ItemStack make(int type,int count){var stack=new ItemStack(HimarsTurretContent.AMMO.get(),count);if(type!=0)stack.set(HbmHazards.LEGACY_METADATA.get(),type);return stack;}
 @Override public String getDescriptionId(ItemStack stack){return "item.hbm.ammo_himars_"+HimarsRocketTypes.type(metadata(stack)).name();}
 private static void line(List<Component> list,String key,ChatFormatting color,Object...args){list.add(Component.translatable("item.hbm.himars."+key,args).withStyle(color));}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> list,TooltipFlag flag){int type=metadata(stack);line(list,"strength",ChatFormatting.YELLOW,type==1||type==5?50:20);switch(type){
  case 0->{line(list,"damage",ChatFormatting.YELLOW,"3x");line(list,"no_blocks",ChatFormatting.BLUE);}
  case 2->{line(list,"damage",ChatFormatting.YELLOW,"3x");line(list,"blocks",ChatFormatting.RED);}
  case 3->{line(list,"damage",ChatFormatting.YELLOW,"3x");line(list,"phosphorus",ChatFormatting.RED);line(list,"no_blocks",ChatFormatting.BLUE);}
  case 4->{line(list,"damage",ChatFormatting.YELLOW,"10x");line(list,"blocks",ChatFormatting.RED);}
  case 6->{line(list,"nuclear",ChatFormatting.RED);line(list,"blocks",ChatFormatting.RED);}
  case 7->{line(list,"lava",ChatFormatting.RED);line(list,"blocks",ChatFormatting.RED);}
  case 1->{line(list,"damage",ChatFormatting.YELLOW,"5x");line(list,"blocks",ChatFormatting.RED);}
  case 5->{line(list,"damage",ChatFormatting.YELLOW,"12x");line(list,"blocks",ChatFormatting.RED);}
  default->throw new IllegalStateException();}}
 public static List<ItemStack> creative(){return java.util.stream.IntStream.of(0,2,3,4,7,6,1,5).mapToObj(type->make(type,1)).toList();}
}
