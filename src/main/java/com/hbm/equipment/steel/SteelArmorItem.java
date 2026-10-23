package com.hbm.equipment.steel;
import com.hbm.armor.t51.SourceFullSetArmorItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
/** Actual X5846 unpowered ArmorFSB steel set. No source potion/hazard/HE/set-bonus flags were configured. */
public final class SteelArmorItem extends SourceFullSetArmorItem {
    private final String name;
    public SteelArmorItem(Type slot,String name){super(SteelEquipmentContent.MATERIAL,slot,30);this.name=name;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity wearer,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return ResourceLocation.fromNamespaceAndPath("hbm","textures/armor/steel_"+(slot==EquipmentSlot.LEGS?2:1)+".png");}
}
