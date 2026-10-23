package com.hbm.item.sourcedrinks;

import com.hbm.item.structureloot.StructureFoodItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Actual ItemMuchoMango:200tick drink and existing legacy food/burp/stat behavior. */
public final class ItemMuchoMango extends StructureFoodItem {
    public ItemMuchoMango() { super("mucho_mango", 10, .6F, 64, true); }
    @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 200; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
    @Override protected void onFoodEaten(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide) player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200));
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.mucho_mango.large_can"));
    }
}
