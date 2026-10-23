package com.hbm.machine.massstorage;

import com.hbm.hazard.HbmHazards;
import com.hbm.machine.storage.CrateStackData;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Source ItemBlockStorageCrate explicitly excludes mass storage from in-hand opening. */
public final class MassStorageItem extends BlockItem {
    public MassStorageItem(MassStorageBlock block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    public static int tier(ItemStack stack){return Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)%4);}
    public ItemStack stack(int tier){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),Math.abs(tier%4));return stack;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        if(context.registries()==null)return;var tag=CrateStackData.data(stack);var filter=CrateStackData.slot(tag,1,context.registries());
        if(!filter.isEmpty()){lines.add(filter.getHoverName().copy().withStyle(ChatFormatting.GOLD));lines.add(Component.literal(String.format(Locale.US,"%,d / %,d",tag.getInt("stack"),MassStorageBlock.capacity(tier(stack)))));}
    }
}
