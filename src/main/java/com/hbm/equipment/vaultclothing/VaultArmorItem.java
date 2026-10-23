package com.hbm.equipment.vaultclothing;
import com.hbm.armor.LegacyArmorDurability;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Actual unpowered ModArmor/ArmorModel ItemArmor base; deliberately not source ArmorFSB. */
public class VaultArmorItem extends ArmorItem implements LegacyArmorDurability {
    private final String name;private final ResourceLocation texture;
    public VaultArmorItem(Holder<ArmorMaterial> material,Type slot,int durability,String name,String texture){super(material,slot,new Properties().durability(slot.getDurability(durability)));this.name=name;this.texture=ResourceLocation.fromNamespaceAndPath("hbm",texture);}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return texture;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!player.getItemBySlot(getEquipmentSlot()).isEmpty())return InteractionResultHolder.pass(stack);player.setItemSlot(getEquipmentSlot(),stack.copy());stack.setCount(0);return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
}
