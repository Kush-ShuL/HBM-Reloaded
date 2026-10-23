package com.hbm.armor.trenchmaster;

import com.hbm.armor.LegacyArmorDamageHandler;
import com.hbm.armor.t51.SourceFullSetArmorItem;
import com.hbm.weapon.sedna.gun.GunArmorPerks;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

/** Actual unpowered ArmorTrenchmaster/ArmorFSB: source setMaxDamage(0), not an HE armor. */
public final class ArmorTrenchmaster extends SourceFullSetArmorItem implements LegacyArmorDamageHandler {
    public ArmorTrenchmaster(Holder<ArmorMaterial> material, Type slot) {
        super(material, slot, 0);
    }

    @Override public boolean fsbHidesHat() { return true; }
    @Override public boolean fsbVats() { return true; }
    @Override public List<MobEffectInstance> fsbEffects() {
        return List.of(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 2, true, true),
                new MobEffectInstance(MobEffects.DIG_SPEED, 20, 1, true, true),
                new MobEffectInstance(MobEffects.JUMP, 20, 1, true, true),
                new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 0, true, true));
    }

    private boolean sourceChest(Player player, ItemStack armor) {
        // Original ModEventHandler calls ArmorFSB.handleAttack/Hurt once, on the chest.
        return getEquipmentSlot() == EquipmentSlot.CHEST
                && player.getItemBySlot(EquipmentSlot.CHEST) == armor
                && GunArmorPerks.isTrenchMaster(player);
    }

    @Override public boolean sourceCancelAttack(Player player, ItemStack armor, DamageSource source, float amount) {
        if (!sourceChest(player, armor) || player.getRandom().nextInt(3) != 0) return false;
        // Argument evaluation consumes this float even when the source sound cooldown is active.
        TrenchmasterRuntime.plink(player, 1F + player.getRandom().nextFloat() * .5F);
        return true;
    }

    @Override public float sourceHurt(Player player, ItemStack armor, DamageSource source, float amount) {
        // getSourceOfDamage() is the direct entity, not a projectile's responsible owner.
        if (sourceChest(player, armor) && source.is(DamageTypeTags.IS_EXPLOSION)
                && source.getDirectEntity() == player) return 0;
        return amount;
    }

    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
                                                     ArmorMaterial.Layer layer, boolean inner) {
        return TrenchmasterContent.id("textures/armor/starmetal_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
    }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        super.appendHoverText(stack, context, lines, flags);
        lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));
        var effects = Component.empty();
        for (var effect : fsbEffects()) {
            if (!effects.getSiblings().isEmpty()) effects.append(", ");
            effects.append(Component.translatable(effect.getEffect().value().getDescriptionId()));
        }
        lines.add(effects.withStyle(ChatFormatting.AQUA));
        lines.add(Component.literal("  ").append(Component.translatable("armor.vats")).withStyle(ChatFormatting.RED));
        lines.add(Component.literal("  ").append(Component.translatable("armor.stepSize", 1)).withStyle(ChatFormatting.BLUE));
        // Original fasterReload lore is commented out; its real gun reload effect is already in ItemGunBaseNT.
        lines.add(Component.literal("  ").append(Component.translatable("armor.moreAmmo")).withStyle(ChatFormatting.RED));
    }
}
