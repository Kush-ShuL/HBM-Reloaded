package com.hbm.armor.servos;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Original servos: the equipped armor slot, not the servo item alone, selects each effect. */
public final class ItemModServos extends ArmorModItem {
    private static final ResourceLocation CHEST_UUID = ResourceLocation.parse("hbm:b1b7ee0e-1d14-4400-8037-f7f2e02f21ca");
    private static final ResourceLocation LEGS_UUID = ResourceLocation.parse("hbm:30b50d2a-4858-4e5b-88d4-3e3612224238");
    public final boolean desh;
    public ItemModServos(boolean desh) {
        super(ArmorModStorage.SERVOS, false, true, true, false);
        this.desh = desh;
    }
    private String symbol() { return desh ? "servo_set_desh" : "servo_set"; }
    private String description(EquipmentSlot slot) {
        return "tooltip.hbm.servos." + (slot == EquipmentSlot.CHEST ? "chest" : "legs") + (desh ? "_desh" : "");
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + symbol() + ".name"); }
    @Override public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (!(armor.getItem() instanceof ArmorItem item)) return;
        if (item.getEquipmentSlot() == EquipmentSlot.CHEST)
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 60, desh ? 2 : 0));
        if (item.getEquipmentSlot() == EquipmentSlot.LEGS)
            entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, desh ? 2 : 1));
    }
    @Override public Map<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack armor) {
        if (!(armor.getItem() instanceof ArmorItem item)) return Map.of();
        return switch (item.getEquipmentSlot()) {
            case CHEST -> Map.of(Attributes.ATTACK_DAMAGE, new AttributeModifier(CHEST_UUID, desh ? 1.5 : .5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            case LEGS -> Map.of(Attributes.MOVEMENT_SPEED, new AttributeModifier(LEGS_UUID, desh ? .5 : .25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            default -> Map.of();
        };
    }
    @Override public Component installedDescription(ItemStack stack, ItemStack armor) {
        if (!(armor.getItem() instanceof ArmorItem item)) return super.installedDescription(stack, armor);
        var slot = item.getEquipmentSlot();
        if (slot != EquipmentSlot.CHEST && slot != EquipmentSlot.LEGS) return super.installedDescription(stack, armor);
        return Component.translatable("tooltip.hbm.servos.installed", stack.getHoverName(), Component.translatable(description(slot)))
                .withStyle(ChatFormatting.DARK_PURPLE);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.servos.chest_item", Component.translatable(description(EquipmentSlot.CHEST))).withStyle(ChatFormatting.DARK_PURPLE));
        lines.add(Component.translatable("tooltip.hbm.servos.legs_item", Component.translatable(description(EquipmentSlot.LEGS))).withStyle(ChatFormatting.DARK_PURPLE));
        lines.add(Component.empty());
        super.appendHoverText(stack, context, lines, flags);
    }
}
