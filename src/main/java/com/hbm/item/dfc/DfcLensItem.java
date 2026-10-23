package com.hbm.item.dfc;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
/** The source lens has a separate long-valued damage tag, not vanilla integer item damage. */
public final class DfcLensItem extends Item {
    public final long maxDamage;
    public DfcLensItem(long maxDamage){super(new Properties().stacksTo(1));this.maxDamage=maxDamage;}
    public static long getLensDamage(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getLong("damage");}
    public static void setLensDamage(ItemStack stack,long damage){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.putLong("damage",damage);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){long left=maxDamage-getLensDamage(stack);lines.add(Component.translatable("item.hbm.dfc.lens.durability",left,maxDamage,(int)(left*100/maxDamage)));}
    @Override public boolean isBarVisible(ItemStack stack){return getLensDamage(stack)!=0;}
    @Override public int getBarWidth(ItemStack stack){return Mth.clamp((int)Math.round(13*(1D-(double)getLensDamage(stack)/maxDamage)),0,13);}
    @Override public int getBarColor(ItemStack stack){float remaining=Math.max(0F,(float)(maxDamage-getLensDamage(stack))/maxDamage);return Mth.hsvToRgb(remaining/3,1,1);}
}
