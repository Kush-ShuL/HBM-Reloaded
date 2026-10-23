package com.hbm.item.archineer;

import com.hbm.api.tool.ToolableBlock.ToolType;
import com.hbm.item.tool.ToolingItem;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/** X5846 ItemToolingWeapon(WRENCH,1000,12F), distinct from ModItems.wrench. */
public final class ArchineerWrenchItem extends ToolingItem {
    public ArchineerWrenchItem() {
        super(ToolType.WRENCH,1000,new Properties().attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,12D,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND).build()));
    }
    @Override public boolean hurtEnemy(ItemStack stack,LivingEntity target,LivingEntity attacker){return false;}
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity target,LivingEntity attacker){/* Source hitEntity returns false and does not charge durability. */}
}
