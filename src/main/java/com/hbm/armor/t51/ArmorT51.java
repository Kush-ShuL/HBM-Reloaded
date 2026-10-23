package com.hbm.armor.t51;
import com.hbm.armor.*;
import com.hbm.damage.PoweredFullSuitArmor;
import com.hbm.item.battery.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
/** Complete source ArmorFSBPowered battery semantics plus original ArmorT51 full-set properties. */
public final class ArmorT51 extends SourceFullSetArmorItem implements com.hbm.armor.powered.SourcePoweredArmor {
 public static final long MAX_POWER=1_000_000,CHARGE_RATE=10_000,CONSUMPTION=1_000,DRAIN=5;
 @Override public long sourceMaxPower(){return MAX_POWER;}@Override public long sourceChargeRate(){return CHARGE_RATE;}@Override public long sourceDamageConsumption(){return CONSUMPTION;}@Override public long sourceDrain(){return DRAIN;}
 public ArmorT51(Holder<ArmorMaterial> material,Type slot){super(material,slot,150);}
 private CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
 private void write(ItemStack stack,long amount){var data=data(stack);data.putLong("charge",amount);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}
 @Override public void chargeBattery(ItemStack stack,long amount){write(stack,(stack.has(DataComponents.CUSTOM_DATA)?data(stack).getLong("charge"):0)+amount);}
 @Override public void setCharge(ItemStack stack,long amount){write(stack,amount);}
 @Override public void dischargeBattery(ItemStack stack,long amount){write(stack,Math.max(0,(stack.has(DataComponents.CUSTOM_DATA)?data(stack).getLong("charge"):getMaxCharge(stack))-amount));}
 @Override public long getCharge(ItemStack stack){if(!stack.has(DataComponents.CUSTOM_DATA)){write(stack,getMaxCharge(stack));return data(stack).getLong("charge");}return Math.min(data(stack).getLong("charge"),getMaxCharge(stack));}
 @Override public long getMaxCharge(ItemStack stack){var mod=ArmorModStorage.pryMod(stack,ArmorModStorage.BATTERY,null);return mod.getItem() instanceof ItemModBattery battery?(long)(MAX_POWER*battery.multiplier):MAX_POWER;}
 @Override public long getChargeRate(ItemStack stack){return CHARGE_RATE;}
 @Override public long getDischargeRate(ItemStack stack){return 0;}
 @Override public boolean isArmorEnabled(ItemStack stack){return getCharge(stack)>0;}
 @Override public String fsbStepSound(){return "step.metal";}
 @Override public String fsbJumpSound(){return "step.iron_jump";}
 @Override public String fsbFallSound(){return "step.iron_land";}
 @Override public boolean fsbHardLanding(){return true;}
 @Override public void setDamage(ItemStack stack,int damage){dischargeBattery(stack,damage*CONSUMPTION);}
 @Override public boolean isBarVisible(ItemStack stack){return getCharge(stack)<getMaxCharge(stack);}
 @Override public int getBarWidth(ItemStack stack){return Math.round(13F*(float)((double)getCharge(stack)/getMaxCharge(stack)));}
 @Override public int getBarColor(ItemStack stack){return net.minecraft.util.Mth.hsvToRgb(Math.max(0,(float)((double)getCharge(stack)/getMaxCharge(stack)))/3F,1,1);}
 @Override public void inventoryTick(ItemStack stack,net.minecraft.world.level.Level level,net.minecraft.world.entity.Entity entity,int slot,boolean selected){if(entity instanceof net.minecraft.world.entity.player.Player player&&player.getItemBySlot(getEquipmentSlot())==stack)T51Runtime.armorTick(this,stack,level,player);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){
  lines.add(Component.translatable("tooltip.hbm.t51.charge",BatteryItem.shortNumber(getCharge(stack)),BatteryItem.shortNumber(getMaxCharge(stack))));
  lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("effect.minecraft.strength").withStyle(ChatFormatting.AQUA));
  lines.add(Component.literal("  ").append(Component.translatable("armor.geigerSound")).withStyle(ChatFormatting.GOLD));lines.add(Component.literal("  ").append(Component.translatable("armor.vats")).withStyle(ChatFormatting.RED));lines.add(Component.literal("  ").append(Component.translatable("armor.hardLanding")).withStyle(ChatFormatting.RED));
 }
}
