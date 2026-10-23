package com.hbm.armor.dns;

import com.hbm.armor.LegacyArmorDamageHandler;
import com.hbm.armor.powered.SourcePoweredArmor;
import com.hbm.armor.t51.SourceFullSetArmorItem;
import com.hbm.armor.t51.T51Runtime;
import com.hbm.armor.trenchmaster.TrenchmasterRuntime;
import com.hbm.equipment.jetpack.JetpackNetwork;
import com.hbm.equipment.jetpack.JetpackRuntime;
import com.hbm.item.battery.BatteryItem;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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

/** Genuine ArmorDNT / HBM_DNT_NANO, distinct from the unpowered weak DNT quartet. */
public final class ArmorDns extends SourceFullSetArmorItem implements SourcePoweredArmor, LegacyArmorDamageHandler {
    // Source 6ab858ba-d712-485c-bae9-e5e765fc555a; modern attribute identities are ResourceLocations.
    private static final ResourceLocation SPEED = DnsArmorContent.id("dnt_speed");
    private final String name;
    public ArmorDns(Type type, String name) { super(DnsArmorContent.MATERIAL, type, 150); this.name = name; }
    @Override public long sourceMaxPower() { return 1_000_000_000; }
    @Override public long sourceChargeRate() { return 1_000_000; }
    @Override public long sourceDamageConsumption() { return 100_000; }
    @Override public long sourceDrain() { return 115; }
    @Override public boolean isArmorEnabled(ItemStack stack) { return SourcePoweredArmor.super.isArmorEnabled(stack); }
    @Override public boolean fsbThermal() { return true; }
    @Override public boolean fsbHardLanding() { return true; }
    @Override public boolean fsbHidesHat() { return getEquipmentSlot() == EquipmentSlot.HEAD; }
    @Override public String fsbStepSound() { return "step.metal"; }
    @Override public String fsbJumpSound() { return "step.iron_jump"; }
    @Override public String fsbFallSound() { return "step.iron_land"; }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + name + ".name"); }
    @Override public List<MobEffectInstance> fsbEffects() {
        return List.of(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 9, true, true),
            new MobEffectInstance(MobEffects.DIG_SPEED, 20, 7, true, true), new MobEffectInstance(MobEffects.JUMP, 20, 2, true, true));
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
        if (movement != null) {
            // Source onArmorTick removes this identity, then adds the operation-0 modifier only while sprinting.
            movement.removeModifier(SPEED);
            if (player.isSprinting()) movement.addTransientModifier(new AttributeModifier(SPEED, .25D, AttributeModifier.Operation.ADD_VALUE));
        }
        if (!T51Runtime.fullSet(player, true)) return;
        boolean active = JetpackNetwork.enabled(player) && JetpackNetwork.jump(player);
        boolean hover = !player.onGround() && !player.isShiftKeyDown() && JetpackNetwork.enabled(player);
        if ((active || hover) && player instanceof ServerPlayer server) JetpackNetwork.exhaust(server, 4);
        JetpackRuntime.resetFlight(player);
        var velocity = player.getDeltaMovement();
        if (active) {
            if (velocity.y < .6D) velocity = velocity.add(0, .2D, 0);
            player.fallDistance = 0;
            sound(level, player);
        } else if (hover) {
            player.fallDistance = 0;
            double y = velocity.y;
            if (y < -1) y += .4D;
            else if (y < -.1D) y += .2D;
            else if (y < 0) y = 0;
            velocity = new net.minecraft.world.phys.Vec3(velocity.x * 1.05D, y, velocity.z * 1.05D);
            if (player.zza != 0) {
                var look = player.getLookAngle();
                velocity = velocity.add(look.x * .25D * player.zza, 0, look.z * .25D * player.zza);
            }
            sound(level, player);
        }
        if (player.isShiftKeyDown() && !player.onGround()) velocity = velocity.add(0, -.1D, 0);
        player.setDeltaMovement(velocity);
    }
    private static void sound(Level level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), HbmSounds.get("weapon.immolatorShoot"), SoundSource.PLAYERS, .125F, 1.5F);
    }
    private boolean chest(Player player, ItemStack armor) {
        return getEquipmentSlot() == EquipmentSlot.CHEST && player.getItemBySlot(EquipmentSlot.CHEST) == armor && T51Runtime.fullSet(player, true);
    }
    @Override public boolean sourceCancelAttack(Player player, ItemStack armor, DamageSource source, float amount) {
        if (!chest(player, armor) || source.is(DamageTypeTags.IS_EXPLOSION)) return false;
        TrenchmasterRuntime.plink(player, 1F + player.getRandom().nextFloat() * .5F);
        return true;
    }
    @Override public float sourceHurt(Player player, ItemStack armor, DamageSource source, float amount) {
        if (!chest(player, armor)) return amount;
        return source.is(DamageTypeTags.IS_EXPLOSION) ? amount * .001F : 0;
    }
    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean inner) {
        return DnsArmorContent.id("textures/armor/starmetal_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.t51.charge", BatteryItem.shortNumber(getCharge(stack)), BatteryItem.shortNumber(getMaxCharge(stack))));
        lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));
        for (var effect : fsbEffects()) lines.add(Component.literal("  ").append(Component.translatable(effect.getEffect().value().getDescriptionId())).withStyle(ChatFormatting.AQUA));
        for (String key : new String[]{"armor.vats", "armor.thermal", "armor.hardLanding"}) lines.add(Component.literal("  ").append(Component.translatable(key)).withStyle(ChatFormatting.RED));
        for (String key : new String[]{"armor.rocketBoots", "armor.fastFall", "armor.sprintBoost"}) lines.add(Component.literal("  ").append(Component.translatable(key)).withStyle(ChatFormatting.AQUA));
    }
}
