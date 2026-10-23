package com.hbm.armor.hev;

import com.hbm.armor.powered.SourcePoweredArmor;
import com.hbm.armor.t51.SourceFullSetArmorItem;
import com.hbm.armor.t51.T51Runtime;
import com.hbm.item.battery.BatteryItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Actual ArmorHev; all power, battery mods, full-set effects and damage remain in the sole FSB backend. */
public final class ArmorHev extends SourceFullSetArmorItem implements SourcePoweredArmor {
    private final String name;
    public ArmorHev(Type type, String name) { super(HevArmorContent.MATERIAL, type, 150); this.name = name; }
    @Override public long sourceMaxPower() { return 1_000_000; }
    @Override public long sourceChargeRate() { return 10_000; }
    @Override public long sourceDamageConsumption() { return 2_500; }
    @Override public long sourceDrain() { return 0; }
    @Override public boolean sourceGeiger() { return true; }
    @Override public boolean sourceVats() { return false; }
    @Override public boolean fsbHidesHat() { return getEquipmentSlot() == EquipmentSlot.HEAD; }
    @Override public boolean isArmorEnabled(ItemStack stack) { return SourcePoweredArmor.super.isArmorEnabled(stack); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + name + ".name"); }
    @Override public List<MobEffectInstance> fsbEffects() {
        return List.of(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1, true, true),
            new MobEffectInstance(MobEffects.JUMP, 20, 0, true, true));
    }
    @Override public void setDamage(ItemStack stack, int damage) { dischargeBattery(stack, damage * sourceDamageConsumption()); }
    @Override public boolean isBarVisible(ItemStack stack) { return getCharge(stack) < getMaxCharge(stack); }
    @Override public int getBarWidth(ItemStack stack) { return Math.round(13F * (float) ((double) getCharge(stack) / getMaxCharge(stack))); }
    @Override public int getBarColor(ItemStack stack) { return Mth.hsvToRgb(Math.max(0, (float) ((double) getCharge(stack) / getMaxCharge(stack))) / 3F, 1, 1); }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (entity instanceof Player player && player.getItemBySlot(getEquipmentSlot()) == stack)
            T51Runtime.armorTick(this, stack, level, player);
    }
    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean inner) {
        return HevArmorContent.id("textures/armor/starmetal_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.t51.charge", BatteryItem.shortNumber(getCharge(stack)), BatteryItem.shortNumber(getMaxCharge(stack))));
        lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("effect.minecraft.speed").append(", ").append(Component.translatable("effect.minecraft.jump_boost")).withStyle(ChatFormatting.AQUA));
        lines.add(Component.literal("  ").append(Component.translatable("armor.geigerSound")).withStyle(ChatFormatting.GOLD));
        lines.add(Component.literal("  ").append(Component.translatable("armor.geigerHUD")).withStyle(ChatFormatting.GOLD));
    }
}
