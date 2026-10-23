package com.hbm.armor.blackjack;

import com.hbm.armor.powered.SourcePoweredArmor;
import com.hbm.armor.t51.SourceFullSetArmorItem;
import com.hbm.armor.t51.T51Runtime;
import com.hbm.damage.HbmDamageSources;
import com.hbm.equipment.jetpack.JetpackNetwork;
import com.hbm.equipment.jetpack.JetpackRuntime;
import com.hbm.exposure.ExposureEffects;
import com.hbm.item.battery.BatteryItem;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Actual ArmorBJ and its ArmorBJJetpack specialization on the existing powered-FSB tick/charge machinery. */
public final class ArmorBlackjack extends SourceFullSetArmorItem implements SourcePoweredArmor {
    public final boolean jetpack;
    private final String name;
    public ArmorBlackjack(Type slot, boolean jetpack) {
        super(BlackjackContent.MATERIAL, slot, 150);
        this.jetpack = jetpack;
        name = "bj_" + (jetpack ? "plate_jetpack" : switch (slot) {
            case HELMET -> "helmet";
            case CHESTPLATE -> "plate";
            case LEGGINGS -> "legs";
            default -> "boots";
        });
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + name + ".name"); }
    @Override public long sourceMaxPower() { return 10_000_000; }
    @Override public long sourceChargeRate() { return 10_000; }
    @Override public long sourceDamageConsumption() { return 1_000; }
    @Override public long sourceDrain() { return 100; }
    @Override public boolean isArmorEnabled(ItemStack stack) { return SourcePoweredArmor.super.isArmorEnabled(stack); }
    @Override public boolean fsbThermal() { return true; }
    @Override public String fsbStepSound() { return "step.metal"; }
    @Override public String fsbJumpSound() { return "step.iron_jump"; }
    @Override public String fsbFallSound() { return "step.iron_land"; }
    @Override public boolean fsbHardLanding() { return true; }
    @Override public List<MobEffectInstance> fsbEffects() {
        return List.of(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1), new MobEffectInstance(MobEffects.JUMP, 20, 0),
                new MobEffectInstance(MobEffects.SATURATION, 20, 0), new MobEffectInstance(ExposureEffects.RADX, 20, 0));
    }
    @Override public void setDamage(ItemStack stack, int damage) { dischargeBattery(stack, damage * sourceDamageConsumption()); }
    @Override public boolean isBarVisible(ItemStack stack) { return getCharge(stack) < getMaxCharge(stack); }
    @Override public int getBarWidth(ItemStack stack) { return Math.round(13F * (float) ((double) getCharge(stack) / getMaxCharge(stack))); }
    @Override public int getBarColor(ItemStack stack) { return net.minecraft.util.Mth.hsvToRgb(Math.max(0, (float) ((double) getCharge(stack) / getMaxCharge(stack))) / 3F, 1, 1); }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player) || player.getItemBySlot(getEquipmentSlot()) != stack) return;
        T51Runtime.armorTick(this, stack, level, player);
        if (getEquipmentSlot() == EquipmentSlot.HEAD && T51Runtime.fullSet(player, false) && !T51Runtime.fullSet(player, true)) {
            var helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!player.getInventory().add(helmet)) player.drop(helmet, false);
            player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            player.hurt(HbmDamageSources.source(level, HbmDamageSources.lunar), 1000F);
        }
        if (!jetpack || !T51Runtime.fullSet(player, true)) return;
        boolean active = JetpackNetwork.enabled(player) && JetpackNetwork.jump(player);
        if (active && player instanceof ServerPlayer server) JetpackNetwork.exhaust(server, 2);
        JetpackRuntime.resetFlight(player);
        var motion = player.getDeltaMovement();
        if (active) {
            if (motion.y < .4D) player.setDeltaMovement(motion.add(0, .1D, 0));
            player.fallDistance = 0;
            level.playSound(null, player.getX(), player.getY(), player.getZ(), HbmSounds.get("weapon.immolatorShoot"), SoundSource.PLAYERS, .125F, 1.5F);
        } else if (player.isShiftKeyDown() && motion.y < -.08D) {
            double amount = motion.y * -.4D;
            // Source order first adds vertical braking, then the scaled full look vector (including its Y).
            player.setDeltaMovement(motion.add(0, amount, 0).add(player.getLookAngle().scale(amount)));
        }
    }
    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean inner) {
        return BlackjackContent.id("textures/armor/starmetal_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.t51.charge", BatteryItem.shortNumber(getCharge(stack)), BatteryItem.shortNumber(getMaxCharge(stack))));
        lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("effect.minecraft.speed").append(", ").append(Component.translatable("effect.minecraft.jump_boost"))
                .append(", ").append(Component.translatable("effect.minecraft.saturation")).append(", ").append(Component.translatable("effect.hbm.radx")).withStyle(ChatFormatting.AQUA));
        lines.add(Component.literal("  ").append(Component.translatable("armor.geigerSound")).withStyle(ChatFormatting.GOLD));
        for (String key : new String[]{"armor.vats", "armor.thermal", "armor.hardLanding"}) lines.add(Component.literal("  ").append(Component.translatable(key)).withStyle(ChatFormatting.RED));
        if (jetpack) {
            lines.add(Component.literal("  + ").append(Component.translatable("armor.electricJetpack")).withStyle(ChatFormatting.RED));
            lines.add(Component.literal("  + ").append(Component.translatable("armor.glider")).withStyle(ChatFormatting.GRAY));
        }
    }
}
