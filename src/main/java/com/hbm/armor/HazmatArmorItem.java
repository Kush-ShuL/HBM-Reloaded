package com.hbm.armor;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Basic ArmorHazmat has no powered FSB effects or inferred energy capabilities. */
public class HazmatArmorItem extends ArmorItem implements LegacyArmorDurability {
    public HazmatArmorItem(Holder<ArmorMaterial> material,Type type){super(material,type,new Properties().durability(type.getDurability(60)));}
    @Override public int getDamage(ItemStack stack){return stack.getOrDefault(DataComponents.DAMAGE,0);}
    @Override public void setDamage(ItemStack stack,int damage){stack.set(DataComponents.DAMAGE,Math.max(0,damage));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        ItemStack held=player.getItemInHand(hand);
        if(player.getItemBySlot(getEquipmentSlot()).isEmpty()){player.setItemSlot(getEquipmentSlot(),held.copy());held.setCount(0);return InteractionResultHolder.sidedSuccess(held,level.isClientSide);}
        return InteractionResultHolder.pass(held); // Original ItemArmor never swaps an occupied armor slot.
    }
}
