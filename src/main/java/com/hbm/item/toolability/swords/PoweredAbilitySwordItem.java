package com.hbm.item.toolability.swords;
import com.hbm.armor.LegacyArmorDurabilityRules;
import com.hbm.item.battery.*;
import com.hbm.item.toolability.*;
import com.hbm.weapon.legacymelee.LegacyMeleeMaterials;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** True ItemSwordAbilityPower: untagged starts full, original charge NBT, passive gate and wear routed to native HE rather than fake durability. */
public final class PoweredAbilitySwordItem extends ItemSwordAbility implements HbmBattery {
 public static final long MAX_POWER=500000,CHARGE_RATE=1000,CONSUMPTION=100;
 private static final class PoweredProperties extends SourceProperties {@Override public Item.Properties durability(int ignored){return super.durability(1);}}
 public PoweredAbilitySwordItem(){super("elec_sword",12.5F,0,LegacyMeleeMaterials.ELECTRIC,new AvailableAbilities().add(AvailableAbilities.Ability.STUN,2),new PoweredProperties());}
 private long charge(ItemStack stack,long untagged){var data=stack.get(DataComponents.CUSTOM_DATA);return data==null?untagged:data.copyTag().getLong("charge");}
 @Override public void setCharge(ItemStack stack,long amount){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.putLong("charge",amount);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
 @Override public void chargeBattery(ItemStack stack,long amount){setCharge(stack,charge(stack,0)+amount);}
 @Override public void dischargeBattery(ItemStack stack,long amount){setCharge(stack,Math.max(0,charge(stack,MAX_POWER)-amount));}
 @Override public long getCharge(ItemStack stack){if(!stack.has(DataComponents.CUSTOM_DATA))setCharge(stack,MAX_POWER);return charge(stack,MAX_POWER);}
 @Override public long getMaxCharge(ItemStack stack){return MAX_POWER;}@Override public long getChargeRate(ItemStack stack){return CHARGE_RATE;}@Override public long getDischargeRate(ItemStack stack){return 0;}
 @Override public boolean canOperate(ItemStack stack){return getCharge(stack)>=CONSUMPTION;}
 @Override public void setDamage(ItemStack stack,int damage){dischargeBattery(stack,damage*CONSUMPTION);}
 private void wear(ItemStack stack,int amount,LivingEntity owner){if(!(owner.level() instanceof ServerLevel server)||owner instanceof Player player&&player.getAbilities().instabuild)return;dischargeBattery(stack,LegacyArmorDurabilityRules.damageAfterUnbreaking(server,stack,amount,owner.getRandom())*CONSUMPTION);}
 @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){wear(stack,1,attacker);}
 @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,BlockPos pos,LivingEntity miner){if(state.getDestroySpeed(level,pos)!=0)wear(stack,2,miner);return true;}
 @Override public boolean isBarVisible(ItemStack stack){return getCharge(stack)<MAX_POWER;}
 @Override public int getBarWidth(ItemStack stack){return Mth.clamp(Math.round(13F*(float)((double)getCharge(stack)/MAX_POWER)),0,13);}
 @Override public int getBarColor(ItemStack stack){return Mth.hsvToRgb(Math.max(0,(float)((double)getCharge(stack)/MAX_POWER))/3,1,1);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){tooltip.add(Component.translatable("tooltip.hbm.source_sword.charge",BatteryItem.shortNumber(getCharge(stack)),BatteryItem.shortNumber(MAX_POWER)));super.appendHoverText(stack,context,tooltip,flag);}
}
