package com.hbm.machine.arcfurnace;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
/** Original ItemArcElectrode/ItemArcElectrodeBurnt, four metadata variants and independent durability NBT. */
public final class ArcElectrodeItem extends Item {
 public static final List<String> NAMES=List.of("graphite","lanthanium","desh","saturnite");private static final int[] LIFE={10,100,500,1500};public final boolean burnt;
 public ArcElectrodeItem(boolean burnt){super(new Properties().stacksTo(burnt?64:1).component(HbmHazards.LEGACY_METADATA.get(),0));this.burnt=burnt;}
 public static int variant(ItemStack stack){int meta=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);return Math.abs(meta%4);}
 public ItemStack stack(int variant){var result=new ItemStack(this);result.set(HbmHazards.LEGACY_METADATA.get(),variant);return result;}
 public static int durability(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("durability");}
 public static boolean damage(ItemStack stack){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();int amount=tag.getInt("durability")+1;tag.putInt("durability",amount);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return amount>=LIFE[variant(stack)];}
 @Override public Component getName(ItemStack stack){return Component.translatable("item.hbm.arc_electrode"+(burnt?"_burnt":"")+"."+NAMES.get(variant(stack)));}
 @Override public boolean isBarVisible(ItemStack stack){return !burnt&&durability(stack)>0;}
 @Override public int getBarWidth(ItemStack stack){return Math.round(13-13F*durability(stack)/LIFE[variant(stack)]);}
 @Override public int getBarColor(ItemStack stack){return net.minecraft.util.Mth.hsvToRgb(Math.max(0,1F-(float)durability(stack)/LIFE[variant(stack)])/3F,1,1);}
}
