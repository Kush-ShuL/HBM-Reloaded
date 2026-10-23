package com.hbm.item.meteorgavel;

import com.hbm.exposure.HbmExposure;
import com.hbm.item.medicine.MedicineEffects;
import com.mojang.authlib.GameProfile;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ResolvableProfile;

/** The five real IWeaponAbility entries selected by X5846's Mese gavel. Other abilities are outside this group. */
final class GavelAbilities {
    private enum Ability {
        RADIATION(1, "radiation", " (500.0)"), VAMPIRE(2, "vampire", " (50.0)"), STUN(3, "stun", " (10)"), PHOSPHORUS(4, "phosphorus", " (60)"), BEHEADER(8, "beheader", "");
        final int order; final String key, extension;
        Ability(int order, String key, String extension) { this.order = order; this.key = key; this.extension = extension; }
    }
    private final HashMap<Ability, Integer> abilities = new HashMap<>();
    GavelAbilities() {
        abilities.put(Ability.PHOSPHORUS, 0); abilities.put(Ability.RADIATION, 2); abilities.put(Ability.STUN, 3); abilities.put(Ability.VAMPIRE, 4); abilities.put(Ability.BEHEADER, 0);
    }
    void onHit(Player attacker, LivingEntity victim) {
        // Source AvailableAbilities stores a HashMap, then getWeaponAbilities collects another HashMap.
        // Do not invent an ordered chain that changes vampire/beheader interaction on a killing hit.
        abilities.keySet().stream().collect(Collectors.toMap(key -> key, abilities::get)).forEach((ability, level) -> {
            switch (ability) {
                case PHOSPHORUS -> victim.addEffect(new MobEffectInstance(MedicineEffects.PHOSPHORUS, 60 * 20, 4));
                case RADIATION -> HbmExposure.contaminate(victim, HbmExposure.Contamination.CREATIVE, 500F);
                case STUN -> { victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 4)); victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 4)); }
                case VAMPIRE -> {
                    if (victim.getHealth() <= 0) return;
                    victim.setHealth(victim.getHealth() - 50F);
                    if (victim.getHealth() <= 0) victim.die(victim.damageSources().magic());
                    attacker.heal(50F);
                }
                case BEHEADER -> { if (victim.getHealth() <= 0) com.hbm.item.ability.SourceWeaponDrops.behead(victim); }
            }
        });
    }
    void tooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.hbm.gavel.weapon_modifiers"));
        abilities.keySet().stream().sorted(Comparator.comparingInt(ability -> ability.order)).forEach(ability -> tooltip.add(Component.literal("  ").append(Component.translatable("weapon.ability." + ability.key).append(ability.extension).withStyle(ChatFormatting.RED))));
    }
}
