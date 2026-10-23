package com.hbm.client.weapon.b92;
import com.hbm.client.model.LegacyItemTransforms;import com.hbm.client.model.legacy.LegacyModelRenderContext;import com.hbm.client.weapon.pepperbox.*;import com.hbm.weapon.b92.*;import com.mojang.blaze3d.vertex.*;import com.mojang.math.Axis;import net.minecraft.client.Minecraft;import net.minecraft.client.renderer.*;import net.minecraft.world.item.*;
/** Original ItemRenderGunAnim common/first-person matrices and exact Techne pump offset, GUI remains its actual2D icon. */
public final class B92ItemRenderer extends BlockEntityWithoutLevelRenderer {
 private final ModelB92 model=new ModelB92();private static final net.minecraft.resources.ResourceLocation TEXTURE=B92Content.id("textures/models/modelb92sm.png"),ICON=B92Content.id("textures/item/gun_b92.png");
 public B92ItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){if(context==ItemDisplayContext.GUI||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE){var vertex=buffers.getBuffer(context==ItemDisplayContext.GUI?RenderType.entityTranslucentEmissive(ICON):RenderType.entityCutoutNoCull(ICON));icon(vertex,poses,0,0,0,1,light,overlay);icon(vertex,poses,1,0,1,1,light,overlay);icon(vertex,poses,1,1,1,0,light,overlay);icon(vertex,poses,0,1,0,0,light,overlay);return;}
  poses.pushPose();try(var reflected=GunHandTransforms.held(poses,context)){
   boolean left=context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
   if(context.firstPerson()){
    LegacyItemTransforms.held(poses,context);poses.mulPose(Axis.ZP.rotationDegrees(-135));poses.translate(-.5F,0,-.2F);poses.scale(.5F,.5F,.5F);poses.scale(.5F,.5F,.5F);poses.translate(-.2F,-.1F,-.1F);
    if(B92Gun.rotation(stack)>0){float off=B92Gun.rotation(stack)*2;poses.mulPose(Axis.ZP.rotationDegrees(B92Gun.rotation(stack)*-90));poses.translate(off*-.5F,off*-.5F,0);}
   }else{
    // ItemEntityRenderer contributes+.25Y and ItemRenderer contributes(-.5,-.5,-.5); old ENTITY declined all helpers.
    if(context==ItemDisplayContext.GROUND)poses.translate(.5,.25,.5);
    poses.mulPose(Axis.ZP.rotationDegrees(-200));poses.mulPose(Axis.YP.rotationDegrees(75));poses.mulPose(Axis.XP.rotationDegrees(-30));poses.translate(0,-.2F,-.5F);poses.mulPose(Axis.ZP.rotationDegrees(-5));poses.scale(.5F,.5F,.5F);poses.translate(-.3F,-.4F,.15F);
   }
   // Reflection in native LEFT contexts reverses Java-box winding; no-cull preserves the mirrored outer faces with authored normals/UV.
   var vertices=buffers.getBuffer(left?RenderType.entityCutoutNoCull(TEXTURE):RenderType.entityCutout(TEXTURE));try(var draw=LegacyModelRenderContext.push(poses,vertices,light,overlay,0xffffffff)){model.renderAnim(GunRenderOwner.current(),0,0,0,0,0,.0625F,B92Gun.translation(stack));}
  }finally{poses.popPose();}
 }
 private static void icon(VertexConsumer vertex,PoseStack poses,float x,float y,float u,float v,int light,int overlay){vertex.addVertex(poses.last().pose(),x,y,.5F).setColor(0xffffffff).setUv(u,v).setOverlay(overlay).setLight(light).setNormal(poses.last(),0,0,1);}
}
