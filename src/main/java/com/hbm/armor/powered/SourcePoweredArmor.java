package com.hbm.armor.powered;
import com.hbm.armor.*;
import com.hbm.damage.PoweredFullSuitArmor;
import com.hbm.item.battery.HbmBattery;
import com.hbm.armor.t51.ItemModBattery;
import com.hbm.weapon.sedna.gun.GunArmorPerks.FullSetArmor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
/** Stateless original ArmorFSBPowered contract; the sole charge value remains the source charge NBT. */
public interface SourcePoweredArmor extends HbmBattery,PoweredFullSuitArmor,FullSetArmor {
 long sourceMaxPower();long sourceChargeRate();long sourceDamageConsumption();long sourceDrain();
 default boolean sourceGeiger(){return true;}default boolean sourceVats(){return true;}
 @Override default boolean fsbVats(){return sourceVats();}
 private CompoundTag sourceData(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
 private void sourceWrite(ItemStack stack,long amount){var tag=sourceData(stack);tag.putLong("charge",amount);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
 @Override default void chargeBattery(ItemStack stack,long amount){sourceWrite(stack,(stack.has(DataComponents.CUSTOM_DATA)?sourceData(stack).getLong("charge"):0)+amount);}
 @Override default void setCharge(ItemStack stack,long amount){sourceWrite(stack,amount);}
 @Override default void dischargeBattery(ItemStack stack,long amount){sourceWrite(stack,Math.max(0,(stack.has(DataComponents.CUSTOM_DATA)?sourceData(stack).getLong("charge"):getMaxCharge(stack))-amount));}
 @Override default long getCharge(ItemStack stack){if(!stack.has(DataComponents.CUSTOM_DATA)){sourceWrite(stack,getMaxCharge(stack));return sourceData(stack).getLong("charge");}return Math.min(sourceData(stack).getLong("charge"),getMaxCharge(stack));}
 @Override default long getMaxCharge(ItemStack stack){var mod=ArmorModStorage.pryMod(stack,ArmorModStorage.BATTERY,null);return mod.getItem() instanceof ItemModBattery battery?(long)(sourceMaxPower()*battery.multiplier):sourceMaxPower();}
 @Override default long getChargeRate(ItemStack stack){return sourceChargeRate();}
 @Override default long getDischargeRate(ItemStack stack){return 0;}
 @Override default boolean isArmorEnabled(ItemStack stack){return getCharge(stack)>0;}
}
