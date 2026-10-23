package com.hbm.item.sourceapple;

import com.hbm.damage.HbmDamageSources;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.medicine.MedicineEffects;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Complete ItemAppleSchrabidium/ItemAppleEuphemium, using the existing native food/effect/damage identities. */
public final class SourceMetalAppleItem extends Item {
    public enum Kind {
        LEAD("apple_lead", 5, 0F),
        SCHRABIDIUM("apple_schrabidium", 20, 100F),
        EUPHEMIUM("apple_euphemium", 20, 100F);

        final String id;
        final int nutrition;
        final float saturation;
        Kind(String id, int nutrition, float saturation) {
            this.id = id;
            this.nutrition = nutrition;
            this.saturation = saturation;
        }
    }

    private final Kind kind;
    public SourceMetalAppleItem(Kind kind) {
        super(properties(kind));
        this.kind = kind;
    }

    private static Properties properties(Kind kind) {
        var properties = new Properties().component(HbmHazards.LEGACY_METADATA.get(), 0)
                .food(new FoodProperties.Builder().nutrition(kind.nutrition)
                .saturationModifier(kind.saturation).alwaysEdible().build());
        if (kind == Kind.EUPHEMIUM) return properties.stacksTo(1).rarity(Rarity.EPIC);
        return properties.rarity(Rarity.UNCOMMON);
    }

    public static int metadata(ItemStack stack) {
        // These are raw source ItemDamage branches, not ItemEnumMulti modulo ordinals.
        return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0);
    }

    public ItemStack stack(int metadata) {
        var stack = new ItemStack(this);
        if (kind != Kind.EUPHEMIUM) stack.set(HbmHazards.LEGACY_METADATA.get(), metadata);
        return stack;
    }

    public Rarity sourceRarity(ItemStack stack) {
        if (kind == Kind.EUPHEMIUM) return Rarity.EPIC;
        return switch (metadata(stack)) {
            case 0 -> Rarity.UNCOMMON;
            case 1 -> Rarity.RARE;
            case 2 -> Rarity.EPIC;
            default -> Rarity.COMMON;
        };
    }

    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + kind.id + ".name"); }
    @Override public boolean isFoil(ItemStack stack) { return kind == Kind.EUPHEMIUM || metadata(stack) == 2; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.EAT; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 32; }

    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        if (!(consumer instanceof Player player)) return stack;
        int metadata = metadata(stack);
        // Source ItemFood.onEaten consumes even in creative. Capture subtype before native EMPTY loses identity.
        stack.shrink(1);
        player.getFoodData().eat(kind.nutrition, kind.saturation);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP,
                SoundSource.PLAYERS, .5F, level.random.nextFloat() * .1F + .9F);
        if (!level.isClientSide) applySourceEffects(player, metadata);
        player.awardStat(Stats.ITEM_USED.get(this));
        return stack;
    }

    private void applySourceEffects(Player player, int metadata) {
        if (kind == Kind.EUPHEMIUM) {
            effect(player, MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 120);
            effect(player, MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0);
            effect(player, MobEffects.SATURATION, Integer.MAX_VALUE, 120);
        } else if (kind == Kind.LEAD) {
            if (metadata == 0) effect(player, MedicineEffects.LEAD, 15 * 20, 2);
            if (metadata == 1) effect(player, MedicineEffects.LEAD, 60 * 20, 4);
            if (metadata == 2) player.hurt(HbmDamageSources.source(player.level(), HbmDamageSources.lead), 500F);
        } else if (metadata == 0) {
            effect(player, MobEffects.REGENERATION, 600, 4);
            effect(player, MobEffects.DAMAGE_RESISTANCE, 6000, 0);
            effect(player, MobEffects.FIRE_RESISTANCE, 6000, 0);
        } else if (metadata == 1) {
            effect(player, MobEffects.REGENERATION, 1200, 4);
            effect(player, MobEffects.DAMAGE_RESISTANCE, 1200, 4);
            effect(player, MobEffects.FIRE_RESISTANCE, 1200, 0);
            effect(player, MobEffects.DAMAGE_BOOST, 1200, 4);
            effect(player, MobEffects.DIG_SPEED, 1200, 2);
            effect(player, MobEffects.MOVEMENT_SPEED, 1200, 2);
            effect(player, MobEffects.JUMP, 1200, 4);
            effect(player, MobEffects.HEALTH_BOOST, 1200, 9);
            effect(player, MobEffects.ABSORPTION, 1200, 4);
            effect(player, MobEffects.SATURATION, 1200, 9);
        } else if (metadata == 2) {
            effect(player, MobEffects.REGENERATION, Integer.MAX_VALUE, 4);
            effect(player, MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1);
            effect(player, MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0);
            effect(player, MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, 9);
            effect(player, MobEffects.DIG_SPEED, Integer.MAX_VALUE, 4);
            effect(player, MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 3);
            effect(player, MobEffects.JUMP, Integer.MAX_VALUE, 4);
            effect(player, MobEffects.HEALTH_BOOST, Integer.MAX_VALUE, 24);
            effect(player, MobEffects.ABSORPTION, Integer.MAX_VALUE, 14);
            effect(player, MobEffects.SATURATION, Integer.MAX_VALUE, 99);
        }
    }

    private static void effect(Player player, Holder<MobEffect> effect, int duration, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, duration, amplifier));
    }
}
