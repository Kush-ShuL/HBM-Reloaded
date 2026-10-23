package com.hbm.armor.liquidator;

import com.hbm.armor.t51.SourceFullSetArmorItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/** Original ArmorLiquidator: real unpowered FSB, per-piece attributes and original M65/biped skins. */
public class ArmorLiquidator extends SourceFullSetArmorItem {
    private final String name;
    public ArmorLiquidator(Type type,String name){
        super(LiquidatorContent.MATERIAL,type,750,properties(type));this.name=name;
    }
    private static Properties properties(Type type){
        var group=switch(type){case HELMET->EquipmentSlotGroup.HEAD;case CHESTPLATE->EquipmentSlotGroup.CHEST;case LEGGINGS->EquipmentSlotGroup.LEGS;case BOOTS->EquipmentSlotGroup.FEET;default->throw new IllegalArgumentException("No source liquidator body slot");};
        // ItemArmor's old defense is carried by native ARMOR; source override's two attributes apply on each equipped piece, not only a full set.
        var id=LiquidatorContent.id("liquidator_armor_"+type.name().toLowerCase(java.util.Locale.ROOT));
        var attributes=ItemAttributeModifiers.builder()
            .add(Attributes.ARMOR,new AttributeModifier(id,LiquidatorContent.MATERIAL.value().getDefense(type),AttributeModifier.Operation.ADD_VALUE),group)
            .add(Attributes.KNOCKBACK_RESISTANCE,new AttributeModifier(id,100,AttributeModifier.Operation.ADD_VALUE),group)
            .add(Attributes.MOVEMENT_SPEED,new AttributeModifier(id,-.1,AttributeModifier.Operation.ADD_MULTIPLIED_BASE),group).build();
        // Components must be present before Item builds and freezes its defaults.
        return new Properties().component(DataComponents.ATTRIBUTE_MODIFIERS,attributes);
    }
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return LiquidatorContent.id("textures/armor/"+(slot==EquipmentSlot.HEAD?"liquidator_helmet":slot==EquipmentSlot.LEGS?"liquidator_2":"liquidator_1")+".png");}
    @Override public String fsbStepSound(){return "step.metal";}
    @Override public String fsbJumpSound(){return "step.iron_jump";}
    @Override public String fsbFallSound(){return "step.iron_land";}
}
