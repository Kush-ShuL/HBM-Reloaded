package com.hbm.entity.aircraft.bomber;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.quadro.QuadroTargetRay;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Source ItemBombCaller: eight raw metadata branches, five creative entries and unconditional consumption. */
public final class ItemBombCaller extends Item {
 public ItemBombCaller(){super(new Properties().stacksTo(1).component(HbmHazards.LEGACY_METADATA.get(),0));}
 public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
 public static ItemStack stack(int meta){var stack=new ItemStack(BomberContent.CALLER.get());stack.set(HbmHazards.LEGACY_METADATA.get(),meta);return stack;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("item.hbm.bomb_caller.instructions"));int type=metadata(stack);if(type>=0&&type<=7)lines.add(Component.translatable("item.hbm.bomb_caller.type."+type));}
 @Override public boolean isFoil(ItemStack stack){return metadata(stack)>=4;}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
  var stack=player.getItemInHand(hand);var hit=QuadroTargetRay.traceBlock(player,500);
  // Original Library trace can return null; do not invent a modern range endpoint.
  if(hit==null)return InteractionResultHolder.pass(stack);
  if(level instanceof ServerLevel server){
   var bomber=EntityBomber.create(server,metadata(stack),hit.getX(),hit.getY(),hit.getZ());
   int x=net.minecraft.util.Mth.floor(bomber.getX()/16D),z=net.minecraft.util.Mth.floor(bomber.getZ()/16D);
   for(int dx=x-2;dx<=x+2;dx++)for(int dz=z-2;dz<=z+2;dz++)server.getChunk(dx,dz);
   server.addFreshEntity(bomber);player.sendSystemMessage(Component.translatable("item.hbm.bomb_caller.called"));
   level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.techBleep"),SoundSource.PLAYERS,1F,1F);
  }
  stack.shrink(1);return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
 }
}
