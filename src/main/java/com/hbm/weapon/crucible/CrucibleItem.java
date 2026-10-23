package com.hbm.weapon.crucible;
import com.hbm.item.toolability.*;
import com.hbm.machine.woodprocessing.WoodEffects;
import com.hbm.weapon.legacymelee.LegacySwordWear;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;
/** The actual ItemCrucible source sword: three durability charges, not an HE battery or generic plain sword. */
public final class CrucibleItem extends ItemSwordAbility {
 public static final float SOURCE_DAMAGE=5000F;public static final double SOURCE_MOVEMENT=1D;
 public static final UUID SOURCE_SWING_REFILL=UUID.fromString("609268ad-5b34-49c2-abba-a9d83229af03");
 public CrucibleItem(){super("crucible",SOURCE_DAMAGE,SOURCE_MOVEMENT,CrucibleTier.INSTANCE,new AvailableAbilities());}
 public static boolean active(ItemStack stack){return stack.getDamageValue()<stack.getMaxDamage();}
 private static boolean sourceFreeWear(LivingEntity entity){if(!(entity instanceof Player player))return false;String name=player.getDisplayName().getString();return name.equals("Tankish")||name.equals("Tankish020");}
 public static void equip(ServerPlayer player,ItemStack stack){if(active(player.getMainHandItem())){player.level().playSound(null,player.getX(),player.getY(),player.getZ(),com.hbm.registry.HbmSounds.get("weapon.cDeploy"),SoundSource.PLAYERS,1F,1F);CrucibleAnimationPacket.send(player,com.hbm.client.animation.bus.AnimationEnums.ToolAnimation.EQUIP);}}
 @Override public boolean onEntitySwing(ItemStack stack,LivingEntity entity,InteractionHand hand){
  if(hand!=InteractionHand.MAIN_HAND||!(entity instanceof ServerPlayer player))return false;
  if(player.getUUID().equals(SOURCE_SWING_REFILL))stack.setDamageValue(0);
  if(!active(stack))return false;
  CrucibleAnimationPacket.send(player,com.hbm.client.animation.bus.AnimationEnums.ToolAnimation.SWING);return false;
 }
 @Override public boolean hurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){
  if(active(stack)){
   attacker.level().playSound(null,victim.getX(),victim.getY(),victim.getZ(),SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,SoundSource.HOSTILE,1F,.75F+victim.getRandom().nextFloat()*.2F);
   if(!attacker.level().isClientSide&&!victim.isAlive()&&victim.level() instanceof net.minecraft.server.level.ServerLevel level){
    int count=Math.min((int)Math.ceil(victim.getMaxHealth()/3D),250)*4;double y=victim.getY()+victim.getBbHeight()*.5;
    // Existing original vanillaburst blockdust packet/client implementation has the same redstone/.1/50..99life semantics.
    var packet=new WoodEffects(victim.getX(),y,victim.getZ(),count);
    for(var player:level.players())if(player.distanceToSqr(victim.getX(),y,victim.getZ())<=2500)PacketDistributor.sendToPlayer(player,packet);
   }
   if(sourceFreeWear(attacker))return true;
   return super.hurtEnemy(stack,victim,attacker);
  }
  if(!attacker.level().isClientSide&&attacker instanceof Player player)player.displayClientMessage(Component.translatable("message.hbm.crucible.no_charge").withStyle(ChatFormatting.RED),false);
  return false;
 }
 @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){if(active(stack)&&!sourceFreeWear(attacker))LegacySwordWear.damage(stack,1,attacker);}
 @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,BlockPos pos,LivingEntity owner){if(state.getDestroySpeed(level,pos)!=0)LegacySwordWear.damage(stack,2,owner);return true;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var charge=new StringBuilder();for(int i=2;i>=0;i--)charge.append(stack.getDamageValue()<=i?"||||||":"   ");lines.add(Component.translatable("tooltip.hbm.crucible.charge",charge.toString()).withStyle(ChatFormatting.RED));}
}
