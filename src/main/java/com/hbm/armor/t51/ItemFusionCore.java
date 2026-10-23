package com.hbm.armor.t51;
import com.hbm.item.battery.*;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Original ItemFusionCore is an instant consumable, not a rechargeable BatteryItem. */
public final class ItemFusionCore extends Item {
 public final int charge;
 public ItemFusionCore(int charge,int stackSize){super(new Properties().stacksTo(stackSize));this.charge=charge;}
 @Override public InteractionResultHolder<ItemStack> use(Level world,Player player,InteractionHand hand){
  var stack=player.getItemInHand(hand);boolean eligible=T51Runtime.fullSet(player,false)&&player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof com.hbm.damage.PoweredFullSuitArmor;
  if(!world.isClientSide&&eligible){
   for(var slot:T51Runtime.ORDER){var armor=player.getItemBySlot(slot);if(armor.getItem() instanceof HbmBattery battery)battery.setCharge(armor,Math.min(battery.getCharge(armor)+(long)charge,battery.getMaxCharge(armor)));}
   stack.shrink(1);world.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.battery"),SoundSource.PLAYERS,1,1);
  }
  // Old use had a single held slot. Consume a successful native interaction so
  // modern main/offhand fallback cannot apply two cores for one right click.
  return eligible?InteractionResultHolder.consume(stack):InteractionResultHolder.pass(stack);
 }
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){lines.add(Component.translatable("tooltip.hbm.fusion_core.charge",BatteryItem.shortNumber(charge)).withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable("tooltip.hbm.fusion_core.requires_set"));}
}
