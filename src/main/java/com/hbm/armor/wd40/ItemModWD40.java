package com.hbm.armor.wd40;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/** Actual VT-40: source repair-one-point quirk, literal health4 and the real old dust emitter. */
public final class ItemModWD40 extends ArmorModItem {
    private static final ResourceLocation[] UUIDS = {
        ResourceLocation.parse("hbm:8d6e5c77-133e-4056-9c80-a9e42a1a0b65"),
        ResourceLocation.parse("hbm:b1b7ee0e-1d14-4400-8037-f7f2e02f21ca"),
        ResourceLocation.parse("hbm:30b50d2a-4858-4e5b-88d4-3e3612224238"),
        ResourceLocation.parse("hbm:426ee0d0-7587-4697-aaef-4772ab202e78")
    };
    public ItemModWD40() { super(ArmorModStorage.EXTRA, true, true, true, true); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.wd40.name"); }
    private ChatFormatting color() { return System.currentTimeMillis() % 1000 < 500 ? ChatFormatting.BLUE : ChatFormatting.YELLOW; }
    @Override public void modDamage(LivingIncomingDamageEvent event, ItemStack armor) {
        var entity = event.getEntity();
        if (!entity.level().isClientSide && armor.getDamageValue() > 0 && entity.getRandom().nextInt(5) != 0)
            armor.setDamageValue(armor.getDamageValue() - 1);
    }
    @Override public Map<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack armor) {
        if (!(armor.getItem() instanceof ArmorItem item)) return Map.of();
        int type = switch (item.getEquipmentSlot()) { case HEAD -> 0; case CHEST -> 1; case LEGS -> 2; case FEET -> 3; default -> -1; };
        return type < 0 ? Map.of() : Map.of(Attributes.MAX_HEALTH,
                new AttributeModifier(UUIDS[type], 4, AttributeModifier.Operation.ADD_VALUE));
    }
    @Override public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (!entity.level().isClientSide || entity.hurtTime <= 0) return;
        var random = entity.getRandom();
        Wd40Content.CLIENT_DUST.accept(entity.level(), new Vec3(
                entity.getX() + (random.nextDouble() - .5) * entity.getBbWidth() * 2,
                entity.getY() + random.nextDouble() * entity.getBbHeight(),
                entity.getZ() + (random.nextDouble() - .5) * entity.getBbWidth() * 2));
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.wd40.installed", stack.getHoverName()).withStyle(color());
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.wd40.description").withStyle(color()));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
