package com.hbm.armor.diesel;

import com.hbm.armor.fueled.SourceFueledFullSetArmorItem;
import com.hbm.armor.t51.T51Runtime;
import com.hbm.equipment.jetpack.JetpackNetwork;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

/** Actual ArmorDiesel; fuel storage/fill/wear/drain stays in the existing source-fueled armor base. */
public final class ArmorDiesel extends SourceFueledFullSetArmorItem {
    private final String name;
    public ArmorDiesel(Type type, String name) {
        super(DieselArmorContent.MATERIAL, type, 150, properties(type), HbmFluids.fromName("DIESEL"), 64_000, 500, 50, 1);
        this.name = name;
    }
    private static Properties properties(Type slot) {
        var group = switch (slot) { case HELMET -> EquipmentSlotGroup.HEAD; case CHESTPLATE -> EquipmentSlotGroup.CHEST;
            case LEGGINGS -> EquipmentSlotGroup.LEGS; case BOOTS -> EquipmentSlotGroup.FEET; default -> throw new IllegalArgumentException("No source diesel body slot"); };
        var key = DieselArmorContent.id("dieselsuit_armor_" + slot.name().toLowerCase(java.util.Locale.ROOT));
        var attributes = ItemAttributeModifiers.builder()
            .add(Attributes.ARMOR, new AttributeModifier(key, DieselArmorContent.MATERIAL.value().getDefense(slot), AttributeModifier.Operation.ADD_VALUE), group)
            .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(key, .25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), group).build();
        return new Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, attributes);
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + name + ".name"); }
    @Override public boolean acceptsFluid(HbmFluidType type, ItemStack stack) {
        return type == HbmFluids.fromName("DIESEL") || type == HbmFluids.fromName("DIESEL_CRACK");
    }
    @Override public boolean fsbVats() { return true; }
    @Override public boolean fsbThermal() { return true; }
    @Override public List<MobEffectInstance> fsbEffects() {
        return List.of(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2, true, true),
            new MobEffectInstance(MobEffects.JUMP, 20, 2, true, true));
    }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (getEquipmentSlot() == EquipmentSlot.LEGS && entity instanceof ServerPlayer player
            && player.getItemBySlot(EquipmentSlot.LEGS) == stack && T51Runtime.fullSet(player, true) && level.getGameTime() % 3 == 0)
            JetpackNetwork.exhaust(player, 3);
    }
    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean inner) {
        return DieselArmorContent.id("textures/armor/starmetal_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("effect.minecraft.speed").append(", ").append(Component.translatable("effect.minecraft.jump_boost")).withStyle(ChatFormatting.AQUA));
        for (String key : new String[] {"armor.vats", "armor.thermal"})
            lines.add(Component.literal("  ").append(Component.translatable(key)).withStyle(ChatFormatting.RED));
    }
}
