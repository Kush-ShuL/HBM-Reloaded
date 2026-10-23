package com.hbm.equipment.npcheadgear;

import com.hbm.armor.LegacyArmorDurability;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Source ItemArmor/ArmorModel ancestry, deliberately not FSB or powered armor. */
public class NpcHeadgearItem extends ArmorItem implements LegacyArmorDurability {
    private final String sourceName;
    private final ResourceLocation texture;
    protected NpcHeadgearItem(Holder<ArmorMaterial> material, int durability, String name, String texture) {
        super(material, Type.HELMET, durability > 0 ? new Properties().durability(durability) : new Properties().stacksTo(1));
        sourceName = name;
        this.texture = ResourceLocation.fromNamespaceAndPath("hbm", texture);
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + sourceName + ".name"); }
    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean inner) { return texture; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) return InteractionResultHolder.pass(stack);
        player.setItemSlot(EquipmentSlot.HEAD, stack.copy());
        stack.setCount(0);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
