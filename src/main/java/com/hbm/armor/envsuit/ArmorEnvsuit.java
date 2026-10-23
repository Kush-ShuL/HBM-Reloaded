package com.hbm.armor.envsuit;

import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.nightvision.ItemModNightVision;
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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Actual source M1TTY powered armor, not a PA gun provider or another armor-tick backend. */
public final class ArmorEnvsuit extends SourceFullSetArmorItem implements SourcePoweredArmor {
    // Original ENV and NCR use the same UUID. Reuse the already translated NCR identifier so swapping source chests removes it.
    private static final ResourceLocation SPEED = ResourceLocation.parse("hbm:ncrpa_speed_6ab858ba_d712_485c_bae9_e5e765fc555a");
    private final String name;
    public ArmorEnvsuit(Type slot, String name) { super(EnvsuitContent.MATERIAL, slot, 150); this.name = name; }
    @Override public long sourceMaxPower() { return 100_000; }
    @Override public long sourceChargeRate() { return 1_000; }
    @Override public long sourceDamageConsumption() { return 250; }
    @Override public long sourceDrain() { return 0; }
    @Override public boolean sourceGeiger() { return false; }
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
        if (!(entity instanceof Player player) || player.getItemBySlot(getEquipmentSlot()) != stack) return;
        T51Runtime.armorTick(this, stack, level, player);
        if (getEquipmentSlot() != EquipmentSlot.CHEST) return;
        var movement = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movement != null) movement.removeModifier(SPEED);
        if (!T51Runtime.fullSet(player, true)) return;
        if (movement != null && player.isSprinting()) {
            movement.addTransientModifier(new AttributeModifier(SPEED, .1, AttributeModifier.Operation.ADD_VALUE));
        }
        if (player.isInWater()) {
            if (!level.isClientSide) {
                player.setAirSupply(300);
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0));
            }
            player.setDeltaMovement(player.getDeltaMovement().add(player.getLookAngle().scale(.1 * player.zza)));
        } else if (!level.isClientSide) {
            var helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            var mod = ArmorModStorage.pryMod(helmet, ArmorModStorage.HELMET, level.registryAccess());
            if (!(mod.getItem() instanceof ItemModNightVision)) player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean inner) {
        return EnvsuitContent.id("textures/armor/starmetal_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.t51.charge", BatteryItem.shortNumber(getCharge(stack)), BatteryItem.shortNumber(getMaxCharge(stack))));
        lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("effect.minecraft.speed").append(", ").append(Component.translatable("effect.minecraft.jump_boost")).withStyle(ChatFormatting.AQUA));
    }
}
