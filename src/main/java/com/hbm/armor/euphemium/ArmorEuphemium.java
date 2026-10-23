package com.hbm.armor.euphemium;

import com.hbm.armor.LegacyArmorDamageHandler;
import com.hbm.armor.LegacyArmorDurability;
import com.hbm.armor.trenchmaster.TrenchmasterRuntime;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Actual ArmorEuphemium extends ItemArmor, not ArmorFSB/Powered; existing armor paths own damage and rendering. */
public final class ArmorEuphemium extends ArmorItem implements LegacyArmorDurability, LegacyArmorDamageHandler {
    public ArmorEuphemium(Holder<ArmorMaterial> material, Type slot) {
        super(material, slot, new Properties().durability(slot.getDurability(15_000_000)).rarity(Rarity.EPIC));
    }

    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean inner) {
        return layer.texture(inner);
    }

    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player) || player.getItemBySlot(getEquipmentSlot()) != stack || !EuphemiumArmorContent.fullSet(player)) return;
        // Source runs this actual native armor tick for each of the four worn pieces, not a new global FSB/effects ticker.
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 5, 127, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, 127, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 5, 127, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 5, 127, true, true));
        var motion = player.getDeltaMovement();
        if (motion.y < -.25D) {
            player.setDeltaMovement(motion.x, -.25D, motion.z);
            player.fallDistance = 0;
        }
    }

    @Override public boolean sourceCancelAttack(Player player, ItemStack armor, DamageSource source, float amount) {
        // ModEventHandler674 applied the exact four-item check once before the ordinary armor handlers.
        // The existing unique HIGH legacy-armor dispatcher reaches this chest exactly once.
        if (getEquipmentSlot() != EquipmentSlot.CHEST || player.getItemBySlot(EquipmentSlot.CHEST) != armor || !EuphemiumArmorContent.fullSet(player)) return false;
        // Source nextFloat is evaluated even when the shared10tick plink cooldown suppresses playback.
        TrenchmasterRuntime.plink(player, 1F + player.getRandom().nextFloat() * .5F);
        return true;
    }

    /** Original setDamage is deliberately empty; no invented HE/unbreakable component or repair behavior. */
    @Override public void setDamage(ItemStack stack, int damage) { }
    @Override public boolean legacyUsesPostSetDamage() { return true; }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!player.getItemBySlot(getEquipmentSlot()).isEmpty()) return InteractionResultHolder.pass(stack);
        // The original ItemArmor equips into an empty slot only and consumes the held stack even in creative.
        player.setItemSlot(getEquipmentSlot(), stack.copy()); stack.setCount(0);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
