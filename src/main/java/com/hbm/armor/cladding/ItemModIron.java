package com.hbm.armor.cladding;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Source ItemModIron adds .5 knockback resistance per real armor slot, using the original UUIDs. */
public final class ItemModIron extends ArmorModItem {
    private static final ResourceLocation[] UUIDS = {
        ResourceLocation.parse("hbm:8d6e5c77-133e-4056-9c80-a9e42a1a0b65"),
        ResourceLocation.parse("hbm:b1b7ee0e-1d14-4400-8037-f7f2e02f21ca"),
        ResourceLocation.parse("hbm:30b50d2a-4858-4e5b-88d4-3e3612224238"),
        ResourceLocation.parse("hbm:426ee0d0-7587-4697-aaef-4772ab202e78")
    };
    public ItemModIron() { super(ArmorModStorage.CLADDING, true, true, true, true); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.cladding_iron.name"); }
    @Override public Map<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack armor) {
        if (!(armor.getItem() instanceof ArmorItem item)) return Map.of();
        int type = switch (item.getEquipmentSlot()) { case HEAD -> 0; case CHEST -> 1; case LEGS -> 2; case FEET -> 3; default -> -1; };
        return type < 0 ? Map.of() : Map.of(Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(UUIDS[type], .5, AttributeModifier.Operation.ADD_VALUE));
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.armor_cladding.iron_installed", stack.getHoverName()).withStyle(ChatFormatting.WHITE);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.armor_cladding.iron").withStyle(ChatFormatting.WHITE));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
