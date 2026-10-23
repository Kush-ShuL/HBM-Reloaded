package com.hbm.armor.security;

import com.hbm.armor.t51.SourceFullSetArmorItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

/** Actual unpowered X5846 ArmorFSB: no overlay, special model or configured FSB effects. */
public final class SecurityArmorItem extends SourceFullSetArmorItem {
    private final String sourceName;

    public SecurityArmorItem(Type type, String sourceName) {
        super(SecurityContent.MATERIAL, type, 100);
        this.sourceName = sourceName;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item." + sourceName + ".name");
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity wearer, EquipmentSlot slot,
            ArmorMaterial.Layer layer, boolean inner) {
        return ResourceLocation.fromNamespaceAndPath("hbm",
                "textures/armor/security_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
    }
}
