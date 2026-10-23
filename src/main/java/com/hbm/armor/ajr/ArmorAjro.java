package com.hbm.armor.ajr;
import com.hbm.armor.rpa.RpaContent;
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
/** Original ArmorAjro/ArmorFSBPowered: exact shared AJR material, original effects and private power; no PA weapon provider. */
public final class ArmorAjro extends SourceFullSetArmorItem implements SourcePoweredArmor {
 public ArmorAjro(Holder<ArmorMaterial> material,Type slot){super(material,slot,150);}
 @Override public long sourceMaxPower(){return 2_500_000;}@Override public long sourceChargeRate(){return 10_000;}@Override public long sourceDamageConsumption(){return 2_000;}@Override public long sourceDrain(){return 25;}
 @Override public boolean isArmorEnabled(ItemStack stack){return SourcePoweredArmor.super.isArmorEnabled(stack);}
 @Override public String fsbStepSound(){return "step.metal";}@Override public String fsbJumpSound(){return "step.iron_jump";}@Override public String fsbFallSound(){return "step.iron_land";}@Override public boolean fsbHardLanding(){return true;}@Override public boolean fsbHidesHat(){return true;}
 @Override public List<MobEffectInstance> fsbEffects(){return List.of(new MobEffectInstance(MobEffects.JUMP,20,0,true,true),new MobEffectInstance(MobEffects.DAMAGE_BOOST,20,0,true,true));}
 @Override public void setDamage(ItemStack stack,int damage){dischargeBattery(stack,damage*sourceDamageConsumption());}
 @Override public boolean isBarVisible(ItemStack stack){return getCharge(stack)<getMaxCharge(stack);}
 @Override public int getBarWidth(ItemStack stack){return Math.round(13F*(float)((double)getCharge(stack)/getMaxCharge(stack)));}
 @Override public int getBarColor(ItemStack stack){return net.minecraft.util.Mth.hsvToRgb(Math.max(0,(float)((double)getCharge(stack)/getMaxCharge(stack)))/3F,1,1);}
 @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){if(entity instanceof Player player&&player.getItemBySlot(getEquipmentSlot())==stack)T51Runtime.armorTick(this,stack,level,player);}
 @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return RpaContent.id("textures/armor/starmetal_"+(slot==EquipmentSlot.LEGS?2:1)+".png");}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){lines.add(Component.translatable("tooltip.hbm.t51.charge",BatteryItem.shortNumber(getCharge(stack)),BatteryItem.shortNumber(getMaxCharge(stack))));lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("effect.minecraft.jump_boost").append(", ").append(Component.translatable("effect.minecraft.strength")).withStyle(ChatFormatting.AQUA));lines.add(Component.literal("  ").append(Component.translatable("armor.geigerSound")).withStyle(ChatFormatting.GOLD));for(String key:new String[]{"armor.vats","armor.hardLanding"})lines.add(Component.literal("  ").append(Component.translatable(key)).withStyle(ChatFormatting.RED));}
}
