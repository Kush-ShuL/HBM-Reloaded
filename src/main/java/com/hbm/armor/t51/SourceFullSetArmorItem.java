package com.hbm.armor.t51;
import com.hbm.armor.LegacyArmorDurability;
import com.hbm.weapon.sedna.gun.GunArmorPerks.FullSetArmor;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Actual reusable unpowered ArmorFSB material/enabled contract, used by true Titanium prerequisite. */
public class SourceFullSetArmorItem extends ArmorItem implements LegacyArmorDurability,FullSetArmor {
 public int dashCount=0;
 /** Source ArmorFSB.setDashCount and cloneStats value; per-item configuration, not stack NBT or HE. */
 public SourceFullSetArmorItem setDashCount(int count){dashCount=count;return this;}
 @Override public int fsbDashCount(){return dashCount;}
 public SourceFullSetArmorItem(Holder<ArmorMaterial> material,Type slot,int baseDurability){this(material,slot,baseDurability,new Properties());}
 protected SourceFullSetArmorItem(Holder<ArmorMaterial> material,Type slot,int baseDurability,Properties properties){super(material,slot,baseDurability>0?properties.durability(slot.getDurability(baseDurability)):properties.stacksTo(1));}
 @Override public net.minecraft.resources.ResourceLocation getArmorTexture(ItemStack stack,net.minecraft.world.entity.Entity entity,net.minecraft.world.entity.EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return T51Content.id("textures/armor/"+(this instanceof ArmorT51?"starmetal_1":slot==net.minecraft.world.entity.EquipmentSlot.LEGS?"titanium_2":"titanium_1")+".png");}
 @Override public Object fsbMaterialIdentity(){return getMaterial().value();}
 @Override public boolean isArmorEnabled(ItemStack stack){return true;}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!player.getItemBySlot(getEquipmentSlot()).isEmpty())return InteractionResultHolder.pass(stack);player.setItemSlot(getEquipmentSlot(),stack.copy());stack.setCount(0);return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
}
