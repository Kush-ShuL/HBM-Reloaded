package com.hbm.item.lock;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

/** Source key/lock pin configuration; counterfeit keys can be neither changed nor copied. */
public class KeyPinItem extends Item {
    public final boolean fake;
    public KeyPinItem(boolean fake){super(new Item.Properties().stacksTo(1));this.fake=fake;}
    public boolean canTransfer(){return !fake;}
    public boolean isKey(){return true;}
    public static int getPins(ItemStack stack){var data=stack.get(DataComponents.CUSTOM_DATA);if(data==null){stack.set(DataComponents.CUSTOM_DATA,CustomData.EMPTY);return 0;}return data.copyTag().getInt("pins");}
    public static void setPins(ItemStack stack,int pins){var data=stack.get(DataComponents.CUSTOM_DATA);CompoundTag tag=data==null?new CompoundTag():data.copyTag();tag.putInt("pins",pins);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){int pins=getPins(stack);lines.add(pins==0?Component.translatable("tooltip.hbm.key.pins_unset"):Component.translatable("tooltip.hbm.key.pins",pins));if(fake){lines.add(Component.empty());lines.add(Component.translatable("tooltip.hbm.key.immutable"));}}
}
