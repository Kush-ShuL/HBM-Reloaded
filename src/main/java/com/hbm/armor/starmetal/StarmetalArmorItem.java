package com.hbm.armor.starmetal;

import com.hbm.armor.t51.SourceFullSetArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

/** Plain original ArmorFSB: no inferred full-set flags, bespoke model, charge, movement or immunity effect. */
public final class StarmetalArmorItem extends SourceFullSetArmorItem {
    private final String name,texture;
    public StarmetalArmorItem(Holder<ArmorMaterial> material,Type type,int durability,String name,String texture){super(material,type,durability);this.name=name;this.texture=texture;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return StarmetalArmorContent.id("textures/armor/"+texture+".png");}
}
