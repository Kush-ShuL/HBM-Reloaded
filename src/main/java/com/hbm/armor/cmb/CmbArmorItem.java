package com.hbm.armor.cmb;

import com.hbm.armor.t51.SourceFullSetArmorItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Source ArmorFSB CMB quartet and its real cloned full-set effect list. */
public final class CmbArmorItem extends SourceFullSetArmorItem {
    private final String name;
    public CmbArmorItem(Type slot, String name) { super(CmbArmorContent.MATERIAL, slot, 60); this.name = name; }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + name + ".name"); }
    @Override public List<MobEffectInstance> fsbEffects() {
        // ArmorFSB.handleTick recreates its templates with ambient=true.
        return List.of(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2, true, true),
            new MobEffectInstance(MobEffects.DIG_SPEED, 20, 2, true, true),
            new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 4, true, true));
    }
    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity wearer, EquipmentSlot slot,
        ArmorMaterial.Layer layer, boolean inner) {
        return ResourceLocation.fromNamespaceAndPath("hbm", "textures/armor/cmb_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));
        // Source lists effect names, without inferred amplifier/duration lore.
        lines.add(Component.translatable("effect.minecraft.speed").append(", ")
            .append(Component.translatable("effect.minecraft.haste")).append(", ")
            .append(Component.translatable("effect.minecraft.strength")).withStyle(ChatFormatting.AQUA));
    }
}
