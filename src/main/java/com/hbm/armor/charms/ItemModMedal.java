package com.hbm.armor.charms;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.exposure.HbmExposure;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Source medal: PLATE1 but helmet+chest applicability, and literal .5RAD removal every server tick. */
public final class ItemModMedal extends ArmorModItem {
    public ItemModMedal() { super(ArmorModStorage.CHEST, true, true, false, false); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.medal_liquidator.name"); }
    @Override public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (!entity.level().isClientSide) HbmExposure.setRadiation(entity, Math.max(HbmExposure.radiation(entity) - .5F, 0));
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.liquidator_medal.installed", stack.getHoverName()).withStyle(ChatFormatting.GOLD);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.literal("-10 RAD/s").withStyle(ChatFormatting.GOLD));
        super.appendHoverText(stack, context, lines, flags);
    }
}
