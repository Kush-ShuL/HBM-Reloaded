package com.hbm.armor.robes;
import com.hbm.armor.t51.SourceFullSetArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
/** Unmodified zero-bonus ArmorFSB(CHAIN) contract and true chain source durability. */
public final class RobesArmorItem extends SourceFullSetArmorItem {
 public RobesArmorItem(Holder<ArmorMaterial> material,Type slot){super(material,slot,15);}
 /** All source ArmorFSB(CHAIN) pieces share the original material, independently of native texture metadata. */
 @Override public Object fsbMaterialIdentity(){return ArmorMaterials.CHAIN.value();}
 @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return layer.texture(inner);}
}
