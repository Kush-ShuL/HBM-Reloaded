package com.hbm.client.armor.rpa;
import com.hbm.armor.rpa.*;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
/** Original ModelArmorRPA: source mesh/pivots, separate fullbright Glow and real-time rotating chest Fan. */
public final class RpaArmorRenderer {
 public static final ResourceLocation MODEL=RpaContent.id("models/armor/remnant.obj"),HEAD=texture("helmet"),CHEST=texture("chest"),ARM=texture("arm"),LEG=texture("leg");
 private static ResourceLocation texture(String part){return RpaContent.id("textures/armor/rpa_"+part+".png");}
 public static boolean render(PoseStack poses,MultiBufferSource buffers,int light,LivingEntity entity,EquipmentSlot slot,HumanoidModel<?> parent){var stack=entity.getItemBySlot(slot);if(!(stack.getItem() instanceof ArmorRpa armor)||armor.getEquipmentSlot()!=slot)return false;var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);boolean foil=stack.hasFoil();switch(slot){
  case HEAD->T51ArmorRenderer.part(mesh,poses,buffers,light,foil,HEAD,parent.head,0,0,0,0,"Head");
  case CHEST->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,ARM,parent.leftArm,5,2,0,0,"LeftArm");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,ARM,parent.rightArm,-5,2,0,0,"RightArm");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,CHEST,parent.body,0,0,0,0,"Body");glow(mesh,poses,buffers,parent);
   poses.pushPose();try{var body=parent.body;poses.translate(body.x/16F,body.y/16F,body.z/16F);if(body.zRot!=0)poses.mulPose(Axis.ZP.rotation(body.zRot));if(body.yRot!=0)poses.mulPose(Axis.YP.rotation(body.yRot));if(body.xRot!=0)poses.mulPose(Axis.XP.rotation(body.xRot));poses.translate(0,4.875D/16,0);poses.mulPose(Axis.ZP.rotationDegrees((float)(-System.currentTimeMillis()/2D%360)));poses.translate(0,-4.875D/16,0);poses.scale(1F/16,1F/16,1F/16);LegacyObjRenderer.renderOnly(mesh,poses,buffers.getBuffer(RenderType.armorCutoutNoCull(CHEST)),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,"Fan");}finally{poses.popPose();}}
  case LEGS->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.leftLeg,1.9F,12,0,0,"LeftLeg");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.rightLeg,-1.9F,12,0,0,"RightLeg");}
  case FEET->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.leftLeg,1.9F,12,0,0,"LeftBoot");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.rightLeg,-1.9F,12,0,0,"RightBoot");}default->{}
 }return true;}
 private static void glow(WavefrontMesh mesh,PoseStack poses,MultiBufferSource buffers,HumanoidModel<?> parent){poses.pushPose();try{var body=parent.body;poses.translate(body.x/16F,body.y/16F,body.z/16F);if(body.zRot!=0)poses.mulPose(Axis.ZP.rotation(body.zRot));if(body.yRot!=0)poses.mulPose(Axis.YP.rotation(body.yRot));if(body.xRot!=0)poses.mulPose(Axis.XP.rotation(body.xRot));poses.scale(1F/16,1F/16,1F/16);LegacyObjRenderer.renderOnly(mesh,poses,buffers.getBuffer(net.neoforged.neoforge.client.NeoForgeRenderTypes.getUnlitTranslucent(CHEST,false)),0xF000F0,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,"Glow");}finally{poses.popPose();}}
 private RpaArmorRenderer(){}
}
