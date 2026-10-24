package com.hbm.entity.aircraft.bomber;
import com.hbm.weapon.sedna.gun.quadro.QuadroTargetRay;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
/** Fixed ordinary/polarized source rangefinders and the keyed five-second info inbox. */
public final class ItemRangefinder extends Item implements com.hbm.item.FlatSourceItem {
 private final boolean polarized;
 public ItemRangefinder(boolean polarized){super(new Properties().stacksTo(1));this.polarized=polarized;}
 public boolean polarized(){return polarized;}
 @Override public Component getName(ItemStack stack){var name=super.getName(stack);return polarized?name.copy().withStyle(ChatFormatting.LIGHT_PURPLE):name;}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
  var stack=player.getItemInHand(hand);if(player instanceof ServerPlayer server){var start=new Vec3(player.getX(),player.getY()+player.getEyeHeight(),player.getZ());var hit=QuadroTargetRay.traceSolid(player,300);if(hit!=null){double distance=(double)(float)Math.sqrt(start.distanceToSqr(hit));RangefinderNotice.send(server,((int)(distance*10D))/10D+"m",polarized);}}
  return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
 }
}
