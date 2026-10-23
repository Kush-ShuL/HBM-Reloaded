package com.hbm.armor.ncrpa;
import com.hbm.armor.rpa.*;
import com.hbm.armor.powered.SourcePoweredArmor;
import com.hbm.armor.t51.*;
import com.hbm.item.battery.BatteryItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Real ArmorNCRPA/ArmorFSBPowered with exact AJR material, power, full-set effects and PA provider. */
public final class ArmorNcrpa extends SourceFullSetArmorItem implements SourcePoweredArmor,PAWeaponsProvider {
 public ArmorNcrpa(Holder<ArmorMaterial> material,Type slot){super(material,slot,150);}
 @Override public long sourceMaxPower(){return 2_500_000;}@Override public long sourceChargeRate(){return 10_000;}@Override public long sourceDamageConsumption(){return 2_000;}@Override public long sourceDrain(){return 25;}
 @Override public boolean isArmorEnabled(ItemStack stack){return SourcePoweredArmor.super.isArmorEnabled(stack);}
 @Override public String fsbStepSound(){return "step.powered";}@Override public String fsbJumpSound(){return "step.powered";}@Override public String fsbFallSound(){return "step.powered";}@Override public boolean fsbHardLanding(){return true;}@Override public boolean fsbHidesHat(){return true;}
 @Override public List<MobEffectInstance> fsbEffects(){return List.of(new MobEffectInstance(MobEffects.DAMAGE_BOOST,20,3,true,true));}
 @Override public void setDamage(ItemStack stack,int damage){dischargeBattery(stack,damage*sourceDamageConsumption());}
 @Override public boolean isBarVisible(ItemStack stack){return getCharge(stack)<getMaxCharge(stack);}
 @Override public int getBarWidth(ItemStack stack){return Math.round(13F*(float)((double)getCharge(stack)/getMaxCharge(stack)));}
 @Override public int getBarColor(ItemStack stack){return net.minecraft.util.Mth.hsvToRgb(Math.max(0,(float)((double)getCharge(stack)/getMaxCharge(stack)))/3F,1,1);}
 private static final ResourceLocation SPEED=ResourceLocation.parse("hbm:ncrpa_speed_6ab858ba_d712_485c_bae9_e5e765fc555a");
 @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){
  if(!(entity instanceof Player player)||player.getItemBySlot(getEquipmentSlot())!=stack)return;
  T51Runtime.armorTick(this,stack,level,player);
  if(getEquipmentSlot()!=EquipmentSlot.CHEST)return;
  // Original onArmorTick removes then conditionally reapplies UUID6ab858ba... +0.1 ADD_VALUE, regardless of charge/fullset.
  var movement=player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
  if(movement!=null){movement.removeModifier(SPEED);if(player.isSprinting())movement.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(SPEED,.1,net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE));}
  // Native server effect synchronization replaces duplicate client prediction; the original enableHUD is the sole switch.
  if(!level.isClientSide&&T51Runtime.fullSet(player,true)&&level.getGameTime()%20==0&&T51Network.enabled(player))player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,300,0,true,true));
 }
 @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return RpaContent.id("textures/armor/starmetal_"+(slot==EquipmentSlot.LEGS?2:1)+".png");}
 @Override public PowerArmorMelee getMeleeComponent(Player player){return T51Runtime.fullSet(player,false)?NcrMelee.INSTANCE:null;}
 @Override public PowerArmorRanged getRangedComponent(Player player){return T51Runtime.fullSet(player,false)?NcrRanged.INSTANCE:null;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){lines.add(Component.translatable("tooltip.hbm.t51.charge",BatteryItem.shortNumber(getCharge(stack)),BatteryItem.shortNumber(getMaxCharge(stack))));lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("effect.minecraft.strength").withStyle(ChatFormatting.AQUA));lines.add(Component.literal("  ").append(Component.translatable("armor.geigerSound")).withStyle(ChatFormatting.GOLD));for(String key:new String[]{"armor.vats","armor.hardLanding"})lines.add(Component.literal("  ").append(Component.translatable(key)).withStyle(ChatFormatting.RED));}
}
