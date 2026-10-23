package com.hbm.client.weapon.crucible;
import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.model.*;
import com.hbm.client.weapon.accelerator.AcceleratorModelRenderTypes;
import com.hbm.client.weapon.pepperbox.GunHandTransforms;
import com.hbm.client.weapon.pepperbox.mixin.GunHandAccess;
import com.hbm.weapon.crucible.CrucibleItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
/** Original HFR crucible item, folding guards, exact source bus/pivots and actual unlit fullbright blade. */
public final class CrucibleItemRenderer extends BlockEntityWithoutLevelRenderer {
 private static final ResourceLocation MODEL=id("models/weapons/crucible.obj"),HILT=id("textures/models/weapons/crucible_hilt.png"),GUARD=id("textures/models/weapons/crucible_guard.png"),BLADE=id("textures/models/weapons/crucible_blade.png");
 private static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}
 public CrucibleItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  poses.pushPose();try(var hand=GunHandTransforms.heldFull3D(poses,context)){
   boolean active=CrucibleItem.active(stack);
   if(context.firstPerson()){
    LegacyItemTransforms.heldFull3D(poses,context);
    var player=Minecraft.getInstance().player;
    boolean physicalLeft=context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
    var renderHand=player==null||physicalLeft==(player.getMainArm()==net.minecraft.world.entity.HumanoidArm.LEFT)?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND;
    try(var animation=HbmAnimations.hand(renderHand)){first(stack,poses,buffers,light,overlay,active,renderHand);}
   }else if(context==ItemDisplayContext.GUI){
    poses.translate(0,1,.3125);poses.scale(1F/16,-1F/16,1F/16);
    poses.translate(2,14,0);poses.mulPose(Axis.ZP.rotationDegrees(-135));poses.mulPose(Axis.YP.rotationDegrees(90));poses.scale(1.5F,1.5F,1.5F);
    body(poses,buffers,light,overlay,active?0:90);poses.translate(.005,0,0);if(active)part(poses,buffers,light,overlay,BLADE,"Blade",false,true);
   }else{
    if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
     // Actual native entity bob/rotation replace precisely the two source ENTITY helpers.
     poses.translate(.5,context==ItemDisplayContext.GROUND?.25:.5,.5);poses.scale(.5F,.5F,.5F);
     poses.translate(-.75,.6,0);poses.mulPose(Axis.ZP.rotationDegrees(-45));
    }
    // Source ENTITY deliberately falls through to EQUIPPED.
    poses.mulPose(Axis.ZP.rotationDegrees(45));poses.translate(.75,-.4,0);poses.mulPose(Axis.YP.rotationDegrees(90));poses.scale(.15F,.15F,.15F);
    body(poses,buffers,light,overlay,active?0:90);if(active)blade(poses,buffers,light,overlay);
   }
  }finally{poses.popPose();}
 }
 private static void first(ItemStack stack,PoseStack poses,MultiBufferSource buffers,int light,int overlay,boolean active,InteractionHand renderHand){
  var client=Minecraft.getInstance();var player=client.player;boolean blocking=player!=null&&player.isUsingItem()&&player.getUseItem()==stack;
  poses.translate(1.5,-.3,0);if(blocking)poses.translate(-.125,-.25,0);
  float eq=1,prev=1;
  if(player!=null){
   if(player.swingingArm==renderHand)player.swinging=false;
   var accessor=(GunHandAccess)client.gameRenderer.itemInHandRenderer;
   boolean main=renderHand==InteractionHand.MAIN_HAND;
   prev=main?accessor.hbm$oldGunHeight():accessor.hbm$oldOffGunHeight();eq=main?accessor.hbm$gunHeight():accessor.hbm$offGunHeight();
   if(eq<prev){if(main){accessor.hbm$oldGunHeight(0);accessor.hbm$gunHeight(0);}else{accessor.hbm$oldOffGunHeight(0);accessor.hbm$offGunHeight(0);}}
   else if(eq>prev){if(main){accessor.hbm$oldGunHeight(1);accessor.hbm$gunHeight(1);}else{accessor.hbm$oldOffGunHeight(1);accessor.hbm$offGunHeight(1);}}
  }
  poses.scale(.3F,.3F,.3F);poses.mulPose(Axis.ZP.rotationDegrees(45));poses.mulPose(Axis.YP.rotationDegrees(90));boolean swinging=false;
  if(!blocking){var rotation=HbmAnimations.getRelevantTransformation("SWING_ROT");var translation=HbmAnimations.getRelevantTransformation("SWING_TRANS");poses.translate(translation[0],translation[1],translation[2]);poses.mulPose(Axis.XP.rotationDegrees((float)rotation[0]));poses.mulPose(Axis.ZP.rotationDegrees((float)rotation[2]));poses.mulPose(Axis.YP.rotationDegrees((float)rotation[1]));if(rotation[0]!=0)swinging=true;}
  var guard=HbmAnimations.getRelevantTransformation("GUARD_ROT");double rotation=guard[0];if(!swinging&&!active)rotation=90;
  body(poses,buffers,light,overlay,rotation);
  if(eq==1&&prev==1&&guard[2]==0&&(swinging||active))blade(poses,buffers,light,overlay);
 }
 private static void body(PoseStack poses,MultiBufferSource buffers,int light,int overlay,double rotation){
  part(poses,buffers,light,overlay,HILT,"Hilt",false,true);
  poses.pushPose();poses.translate(0,3,.5);poses.mulPose(Axis.XN.rotationDegrees((float)rotation));poses.translate(0,-3,-.5);part(poses,buffers,light,overlay,GUARD,"GuardLeft",false,true);poses.popPose();
  poses.pushPose();poses.translate(0,3,-.5);poses.mulPose(Axis.XP.rotationDegrees((float)rotation));poses.translate(0,-3,.5);part(poses,buffers,light,overlay,GUARD,"GuardRight",false,true);poses.popPose();
 }
 private static void blade(PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.005,0,0);part(poses,buffers,light,overlay,BLADE,"Blade",true,false);poses.popPose();}
 private static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,ResourceLocation texture,String name,boolean fullbright,boolean cull){
  var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
  var out=buffers.getBuffer(AcceleratorModelRenderTypes.model(texture,fullbright,cull,.1F));
  LegacyObjRenderer.renderOnly(mesh,poses,out,fullbright?LightTexture.FULL_BRIGHT:light,overlay,-1,LegacyObjRenderer.Convention.HFR,name);
 }
}
