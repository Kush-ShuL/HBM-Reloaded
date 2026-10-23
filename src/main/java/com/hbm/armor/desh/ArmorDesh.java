package com.hbm.armor.desh;
import com.hbm.armor.fueled.SourceFueledFullSetArmorItem;
import com.hbm.fluid.HbmFluids;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
/** Original ArmorDesh: the true steam-fueled four-piece steamsuit, not a plain Desh biped. */
public final class ArmorDesh extends SourceFueledFullSetArmorItem {
    private final String name;
    public ArmorDesh(Type type,String name){super(DeshContent.MATERIAL,type,150,properties(type),HbmFluids.STEAM,64_000,500,50,1);this.name=name;}
    private static Properties properties(Type type){
        var group=switch(type){case HELMET->EquipmentSlotGroup.HEAD;case CHESTPLATE->EquipmentSlotGroup.CHEST;case LEGGINGS->EquipmentSlotGroup.LEGS;case BOOTS->EquipmentSlotGroup.FEET;default->throw new IllegalArgumentException("No source steamsuit body slot");};
        var id=DeshContent.id("steamsuit_armor_"+type.name().toLowerCase(java.util.Locale.ROOT));
        var attributes=ItemAttributeModifiers.builder().add(Attributes.ARMOR,new AttributeModifier(id,DeshContent.MATERIAL.value().getDefense(type),AttributeModifier.Operation.ADD_VALUE),group)
            .add(Attributes.MOVEMENT_SPEED,new AttributeModifier(id,-.025,AttributeModifier.Operation.ADD_MULTIPLIED_BASE),group).build();
        return new Properties().component(DataComponents.ATTRIBUTE_MODIFIERS,attributes);
    }
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return DeshContent.id("textures/armor/"+(slot==EquipmentSlot.LEGS?"starmetal_2":"starmetal_1")+".png");}
    @Override public boolean fsbHardLanding(){return true;}
    @Override public boolean fsbHidesHat(){return true;}
    @Override public List<MobEffectInstance> fsbEffects(){return List.of(new MobEffectInstance(MobEffects.DIG_SPEED,20,4,true,true));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){super.appendHoverText(stack,context,lines,flags);lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("effect.minecraft.haste").append(" V").withStyle(ChatFormatting.AQUA));lines.add(Component.literal("  ").append(Component.translatable("armor.hardLanding")).withStyle(ChatFormatting.RED));}
}
