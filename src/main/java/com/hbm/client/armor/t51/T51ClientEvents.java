package com.hbm.client.armor.t51;

import com.hbm.armor.t51.*;
import com.hbm.client.armor.t51.mixin.T51MovementAccess;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.registry.HbmSounds;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Original ArmorFSB health labels, source ARMOR-layer energy bars, V-key and metal footsteps. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class T51ClientEvents {
 public static final KeyMapping HUD=new KeyMapping("key.hbm.fsb_hud",KeyConflictContext.IN_GAME,InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_V,"key.categories.hbm");
 static boolean hudEnabled=true;
 public static boolean sourceHudEnabled(){return hudEnabled;}
 @SubscribeEvent public static void keyTick(ClientTickEvent.Post event){var mc=Minecraft.getInstance();while(HUD.consumeClick())if(mc.player!=null&&mc.level!=null&&mc.screen==null)PacketDistributor.sendToServer(new T51Network.Toggle());}
 @SubscribeEvent public static void pre(PlayerTickEvent.Pre event){step(event.getEntity());}
 @SubscribeEvent public static void post(PlayerTickEvent.Post event){step(event.getEntity());}
 private static void step(Player player){var mc=Minecraft.getInstance();if(player!=mc.player||!player.level().isClientSide||!player.onGround()||!T51Runtime.fullSet(player,true)||!(player.getItemBySlot(EquipmentSlot.CHEST).getItem()instanceof com.hbm.weapon.sedna.gun.GunArmorPerks.FullSetArmor armor)||armor.fsbStepSound()==null)return;
  float next=((T51MovementAccess)player).hbm$t51NextStep();var data=player.getPersistentData();if(data.getFloat("hfr_nextStepDistance")==0)data.putFloat("hfr_nextStepDistance",next);
  if(!player.level().getBlockState(BlockPos.containing(player.getX(),player.getY()-.2,player.getZ())).isAir()&&data.getFloat("hfr_nextStepDistance")<=player.moveDist)player.level().playLocalSound(player.getX(),player.getY(),player.getZ(),HbmSounds.get(armor.fsbStepSound()),net.minecraft.sounds.SoundSource.PLAYERS,.25F,1,false);
  data.putFloat("hfr_nextStepDistance",next);
 }
 @SubscribeEvent public static void energy(RenderGuiLayerEvent.Post event){com.hbm.client.jetpack.JetpackArmorBars.render(event);}
 private static void quad(VertexConsumer vertices,org.joml.Matrix4f matrix,float left,float top,float right,float bottom,int color){vertices.addVertex(matrix,left,top,0).setColor(color);vertices.addVertex(matrix,left,bottom,0).setColor(color);vertices.addVertex(matrix,right,bottom,0).setColor(color);vertices.addVertex(matrix,right,top,0).setColor(color);}
 @SubscribeEvent public static void vats(RenderLivingEvent.Pre<?,?> event){var mc=Minecraft.getInstance();var player=mc.player;var entity=event.getEntity();if(player==null||!hudEnabled||mc.options.hideGui||!T51Runtime.active(player)||entity==mc.getCameraEntity()||entity.isInvisibleTo(player)||!entity.getPassengers().isEmpty())return;
  double range=entity.isCrouching()?32:64;if(entity.distanceToSqr(player)>=range*range||entity.getMaxHealth()<=0)return;
  boolean thermal=player.getItemBySlot(EquipmentSlot.CHEST).getItem()instanceof com.hbm.weapon.sedna.gun.GunArmorPerks.FullSetArmor armor&&armor.fsbThermal();
  int count=(int)Math.min(entity.getMaxHealth(),100),bars=(int)Math.ceil(entity.getHealth()*count/entity.getMaxHealth());StringBuilder text=new StringBuilder("\u00a7c");for(int index=0;index<count;index++){if(index==bars)text.append("\u00a7r");text.append('|');}
  var font=LegacyFonts.get();String label=text.toString();int center=font.width(label)/2;var poses=event.getPoseStack();poses.pushPose();try{
   poses.translate(0,entity.getBbHeight()+.75F-(entity instanceof Player&&entity.isSleeping()?1.5:0),0);var camera=mc.gameRenderer.getMainCamera();poses.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));poses.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));float scale=.016666668F*1.6F;poses.scale(-scale,-scale,scale);
   var matrix=poses.last().pose();var buffers=event.getMultiBufferSource();quad(buffers.getBuffer(T51VatsTypes.background(thermal)),matrix,-center-1,-1,center+1,8,0x3f000000);
   font.drawInBatch(label,-center,0,0x20ffffff,false,matrix,buffers,thermal?Font.DisplayMode.SEE_THROUGH:Font.DisplayMode.NORMAL,0,15728880);font.drawInBatch(label,-center,0,-1,false,matrix,buffers,Font.DisplayMode.NORMAL,0,15728880);
  }finally{poses.popPose();}
 }
 private T51ClientEvents(){}
}
