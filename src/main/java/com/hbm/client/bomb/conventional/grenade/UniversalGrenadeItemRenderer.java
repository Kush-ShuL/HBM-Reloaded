package com.hbm.client.bomb.conventional.grenade;

import com.hbm.bomb.conventional.grenade.*;
import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

/** Original ItemRenderGrenade: HFR authored mesh, four textured colour passes and true pin/cap equip buses. */
public final class UniversalGrenadeItemRenderer extends BlockEntityWithoutLevelRenderer{
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/weapons/grenades.obj");
 public UniversalGrenadeItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();LegacyItemTransforms.held(poses,context);
  if(context.firstPerson()){poses.scale(.125F,.125F,.125F);poses.translate(3,1,-3);poses.mulPose(Axis.YP.rotationDegrees(-180));firstPerson(stack,poses,buffers,light,overlay);}
  else if(context==ItemDisplayContext.GUI){poses.translate(0,1,.5);poses.scale(1/16F,-1/16F,1/16F);poses.translate(8,8,0);poses.scale(-1,-1,-1);poses.mulPose(Axis.ZP.rotationDegrees(45));poses.mulPose(Axis.YP.rotationDegrees(150));poses.mulPose(Axis.XP.rotationDegrees(15));grenade(stack,context,poses,buffers,light,overlay);}
  else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){poses.scale(.125F,.125F,.125F);poses.translate(3,1,-.5);grenade(stack,context,poses,buffers,light,overlay);}
  else{poses.translate(.5,context==ItemDisplayContext.GROUND?0:.5,.5);poses.scale(.125F,.125F,.125F);grenade(stack,context,poses,buffers,light,overlay);}poses.popPose();}
 public static void grenade(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var shell=UniversalGrenadeItem.shell(stack);boolean inventory=context==ItemDisplayContext.GUI,held=context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
  switch(shell){case FRAG->{if(inventory){poses.scale(3,3,3);poses.translate(0,-2,0);}if(context==null)poses.translate(0,-2,0);body(stack,poses,buffers,light,overlay);if(context!=null)part(poses,buffers,light,overlay,"frag",-1,false,"FragSpoon","FragRing");}
   case STICK->{if(inventory){poses.scale(2,2,2);poses.translate(0,-4.5,0);}if(held||context==null)poses.translate(0,-2,0);body(stack,poses,buffers,light,overlay);if(context!=null)part(poses,buffers,light,overlay,"stick_body",UniversalGrenadeItem.filling(stack).bodyColor,true,"StickCap");}
   case TECH->{if(inventory){poses.scale(3.5F,3.5F,3.5F);poses.translate(0,-1.75,0);}if(held){poses.scale(1.5F,1.5F,1.5F);poses.translate(.5,-1,.5);}if(context==null){poses.scale(1.5F,1.5F,1.5F);poses.translate(0,-1,0);}body(stack,poses,buffers,light,overlay);if(context!=null)part(poses,buffers,light,overlay,"tech",-1,false,"TechRing");}
   case NUKE->{if(inventory){poses.scale(2.5F,2.5F,2.5F);poses.translate(0,-2.75,0);}if(held){poses.scale(1.5F,1.5F,1.5F);poses.translate(.5,-3,.5);}if(context==null){poses.scale(1.5F,1.5F,1.5F);poses.translate(0,-3,0);}body(stack,poses,buffers,light,overlay);if(context!=null)part(poses,buffers,light,overlay,"nuka",-1,false,"NukaSpoon","NukaRing");}}
 }
 private static void firstPerson(ItemStack stack,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var shell=UniversalGrenadeItem.shell(stack);var move=HbmAnimations.getRelevantTransformation("BODYMOVE");var turn=HbmAnimations.getRelevantTransformation("BODYTURN");var ringMove=HbmAnimations.getRelevantTransformation("RINGMOVE");var ringTurn=HbmAnimations.getRelevantTransformation("RINGTURN");var show=HbmAnimations.getRelevantTransformation("RENDERRING");poses.translate(move[0],move[1],move[2]);poses.mulPose((shell==GrenadeParts.Shell.FRAG||shell==GrenadeParts.Shell.TECH?Axis.XP:Axis.ZP).rotationDegrees((float)turn[2]));body(stack,poses,buffers,light,overlay);
  if(shell==GrenadeParts.Shell.FRAG)part(poses,buffers,light,overlay,"frag",-1,false,"FragSpoon");if(shell==GrenadeParts.Shell.NUKE)part(poses,buffers,light,overlay,"nuka",-1,false,"NukaSpoon");if(show[0]==0)return;poses.translate(ringMove[0],ringMove[1],ringMove[2]);
  switch(shell){case FRAG->{poses.mulPose(Axis.XP.rotationDegrees((float)ringTurn[2]));part(poses,buffers,light,overlay,"frag",-1,false,"FragRing");}
   case STICK->{poses.mulPose(Axis.YP.rotationDegrees((float)ringTurn[1]));part(poses,buffers,light,overlay,"stick",-1,true,"StickCap");part(poses,buffers,light,overlay,"stick_body",UniversalGrenadeItem.filling(stack).bodyColor,true,"StickCap");}
   case TECH->{poses.mulPose(Axis.XP.rotationDegrees((float)ringTurn[2]));part(poses,buffers,light,overlay,"tech",-1,false,"TechRing");}
   case NUKE->{poses.translate(-1,5,0);poses.mulPose(Axis.ZN.rotationDegrees((float)ringTurn[2]));poses.translate(1,-5,0);part(poses,buffers,light,overlay,"nuka",-1,false,"NukaRing");}}
 }
 private static void body(ItemStack stack,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var shell=UniversalGrenadeItem.shell(stack);var filling=UniversalGrenadeItem.filling(stack);String stem=shell==GrenadeParts.Shell.NUKE?"nuka":shell.name().toLowerCase(Locale.ROOT),part=switch(shell){case FRAG->"Frag";case STICK->"Stick";case TECH->"Tech";case NUKE->"Nuka";};part(poses,buffers,light,overlay,stem,-1,false,part);part(poses,buffers,light,overlay,stem+"_body",filling.bodyColor,true,part);if(shell==GrenadeParts.Shell.TECH){part(poses,buffers,light,overlay,stem+"_fuze",UniversalGrenadeItem.fuze(stack).bandColor,true,part);part(poses,buffers,LightTexture.FULL_BRIGHT,overlay,stem+"_lights",filling.labelColor,true,part);}else{part(poses,buffers,light,overlay,stem+"_label",filling.labelColor,true,part);part(poses,buffers,light,overlay,stem+"_fuze",UniversalGrenadeItem.fuze(stack).bandColor,true,part);}}
 private static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String texture,int color,boolean blend,String... parts){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var tex=ResourceLocation.parse("hbm:textures/models/grenades/"+texture+".png");LegacyObjRenderer.renderOnly(mesh,poses,buffers.getBuffer(blend?RenderType.entityTranslucentCull(tex):RenderType.entityCutout(tex)),light,overlay,0xFF000000|color,LegacyObjRenderer.Convention.HFR,parts);}
}
