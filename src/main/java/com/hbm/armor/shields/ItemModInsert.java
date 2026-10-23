package com.hbm.armor.shields;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.exposure.HbmExposure;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/** All11 source inserts, including the original sequential multipliers, wear and source quirks. */
public final class ItemModInsert extends ArmorModItem {
    private static final ResourceLocation[] UUIDS = {
        ResourceLocation.parse("hbm:8d6e5c77-133e-4056-9c80-a9e42a1a0b65"),
        ResourceLocation.parse("hbm:b1b7ee0e-1d14-4400-8037-f7f2e02f21ca"),
        ResourceLocation.parse("hbm:30b50d2a-4858-4e5b-88d4-3e3612224238"),
        ResourceLocation.parse("hbm:426ee0d0-7587-4697-aaef-4772ab202e78")
    };
    public final String name;
    public final float damageMod, projectileMod, explosionMod, speed;
    public ItemModInsert(String name, int durability, float damageMod, float projectileMod, float explosionMod, float speed) {
        super(new Properties().durability(durability), ArmorModStorage.KEVLAR, false, true, false, false);
        this.name = name; this.damageMod = damageMod; this.projectileMod = projectileMod; this.explosionMod = explosionMod; this.speed = speed;
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + name + ".name"); }
    @Override public void modDamage(LivingIncomingDamageEvent event, ItemStack armor) {
        float amount = event.getAmount() * damageMod;
        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) amount *= projectileMod;
        if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)) amount *= explosionMod;
        event.setAmount(amount);
        var entity = event.getEntity();
        var insert = ArmorModStorage.pryMod(armor, ArmorModStorage.KEVLAR, entity.registryAccess());
        if (insert.isEmpty()) return;
        // Source direct setItemDamage, not damageItem: one point even for zero damage/creative/Unbreaking.
        insert.setDamageValue(insert.getDamageValue() + 1);
        if (!entity.level().isClientSide && name.equals("insert_era")) {
            // Modern entity Y is already feet Y (old posY-yOffset). No terrain destruction/fire.
            entity.level().explode(entity, entity.getX(), entity.getY() + entity.getBbHeight() * .5,
                    entity.getZ(), .05F, false, Level.ExplosionInteraction.NONE);
        }
        if (insert.getDamageValue() >= insert.getMaxDamage()) ArmorModStorage.removeMod(armor, ArmorModStorage.KEVLAR);
        else ArmorModStorage.applyMod(armor, insert, entity.registryAccess());
    }
    @Override public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (!entity.level().isClientSide && name.equals("insert_polonium")) HbmExposure.incrementRadiation(entity, 100F);
    }
    @Override public Map<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack armor) {
        if (speed == 1 || !(armor.getItem() instanceof ArmorItem item)) return Map.of();
        int type = switch (item.getEquipmentSlot()) { case HEAD -> 0; case CHEST -> 1; case LEGS -> 2; case FEET -> 3; default -> -1; };
        return type < 0 ? Map.of() : Map.of(Attributes.MOVEMENT_SPEED,
                new AttributeModifier(UUIDS[type], -1F + speed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
    private static String damage(float multiplier) { return (multiplier < 1 ? "-" : "+") + Math.abs(Math.round((1F - multiplier) * 100)) + "%"; }
    private static String reduction(float multiplier) { return "-" + Math.round((1F - multiplier) * 100) + "%"; }
    @Override public Component installedDescription(ItemStack stack) {
        var parts = new ArrayList<Component>();
        if (damageMod != 1F) parts.add(Component.translatable("tooltip.hbm.armor_insert.dmg_short", damage(damageMod)));
        if (projectileMod != 1F) parts.add(Component.translatable("tooltip.hbm.armor_insert.proj_short", reduction(projectileMod)));
        if (explosionMod != 1F) parts.add(Component.translatable("tooltip.hbm.armor_insert.exp_short", reduction(explosionMod)));
        // Preserve the source installed-description check: it tests explosionMod again, not speed.
        if (explosionMod != 1F) parts.add(Component.translatable("tooltip.hbm.armor_insert.speed", reduction(speed)));
        if (name.equals("insert_polonium")) parts.add(Component.literal("+100 RAD/s"));
        var joined = Component.empty();
        for (var part : parts) { if (!joined.getSiblings().isEmpty()) joined.append(" / "); joined.append(part); }
        return Component.translatable("tooltip.hbm.armor_insert.installed", stack.getHoverName(), joined,
                stack.getMaxDamage() - stack.getDamageValue()).withStyle(ChatFormatting.DARK_PURPLE);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        if (damageMod != 1F) lines.add(Component.translatable("tooltip.hbm.armor_insert.damage", damage(damageMod)).withStyle(ChatFormatting.RED));
        if (projectileMod != 1F) lines.add(Component.translatable("tooltip.hbm.armor_insert.projectile", reduction(projectileMod)).withStyle(ChatFormatting.YELLOW));
        if (explosionMod != 1F) lines.add(Component.translatable("tooltip.hbm.armor_insert.explosion", reduction(explosionMod)).withStyle(ChatFormatting.YELLOW));
        if (speed != 1F) lines.add(Component.translatable("tooltip.hbm.armor_insert.speed", reduction(speed)).withStyle(ChatFormatting.BLUE));
        if (name.equals("insert_polonium")) lines.add(Component.literal("+100 RAD/s").withStyle(ChatFormatting.DARK_RED));
        lines.add(Component.translatable("tooltip.hbm.armor_insert.hp", stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
