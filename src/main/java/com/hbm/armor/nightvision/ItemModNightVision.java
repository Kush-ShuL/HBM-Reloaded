package com.hbm.armor.nightvision;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.powered.SourcePoweredArmor;
import com.hbm.armor.t51.T51Network;
import com.hbm.armor.t51.T51Runtime;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

/** Source15-second night vision, enabled by the existing head-toggle flag and real powered full set. */
public final class ItemModNightVision extends ArmorModItem {
    private static final String ACTIVE = "ITEM_MOD_NV_ACTIVE";
    public ItemModNightVision() { super(ArmorModStorage.HELMET, true, false, false, false); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.night_vision.name"); }
    @Override public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (entity.level().isClientSide || !(entity instanceof Player player)
                || !(armor.getItem() instanceof SourcePoweredArmor) || !T51Runtime.fullSet(player, true)) return;
        var data = armor.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (T51Network.enabled(player)) {
            entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0));
            if (!data.contains(ACTIVE)) {
                data.putBoolean(ACTIVE, true);
                armor.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
            }
            if (entity.getRandom().nextInt(200) == 0) armor.hurtAndBreak(1, entity, EquipmentSlot.HEAD);
        } else if (data.contains(ACTIVE)) {
            entity.removeEffect(MobEffects.NIGHT_VISION);
            data.remove(ACTIVE);
            armor.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        }
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("item.night_vision.description.in_armor", stack.getHoverName()).withStyle(ChatFormatting.YELLOW);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("item.night_vision.description.item").withStyle(ChatFormatting.AQUA));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
