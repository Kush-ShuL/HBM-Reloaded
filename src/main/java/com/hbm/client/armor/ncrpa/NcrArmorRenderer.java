package com.hbm.client.armor.ncrpa;
import com.hbm.armor.ncrpa.*;
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
/** Real ModelArmorNCRPA nine groups and four textures; Eyes copies head pose with source fullbrightness. */
public final class NcrArmorRenderer {
 public static final ResourceLocation MODEL=NcrContent.id("models/armor/ncrpa.obj"),HEAD=texture("helmet"),CHEST=texture("chest"),ARM=texture("arm"),LEG=texture("leg");
 private static ResourceLocation texture(String name){return NcrContent.id("textures/armor/ncrpa_"+name+".png");}
 public static boolean render(PoseStack poses,MultiBufferSource buffers,int light,LivingEntity entity,EquipmentSlot slot,HumanoidModel<?> parent){
  var stack=entity.getItemBySlot(slot);if(!(stack.getItem() instanceof ArmorNcrpa armor)||armor.getEquipmentSlot()!=slot)return false;
  var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);boolean foil=stack.hasFoil();
  switch(slot){
   case HEAD->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,HEAD,parent.head,0,0,0,0,"Helmet");eyes(mesh,poses,buffers,parent);}
   case CHEST->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,ARM,parent.leftArm,5,2,0,0,"LeftArm");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,ARM,parent.rightArm,-5,2,0,0,"RightArm");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,CHEST,parent.body,0,0,0,0,"Chest");}
   case LEGS->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.leftLeg,1.9F,12,0,0,"LeftLeg");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.rightLeg,-1.9F,12,0,0,"RightLeg");}
   case FEET->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.leftLeg,1.9F,12,0,0,"LeftBoot");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.rightLeg,-1.9F,12,0,0,"RightBoot");}
   default->{}
  }
  return true;
 }
 private static void eyes(WavefrontMesh mesh,PoseStack poses,MultiBufferSource buffers,HumanoidModel<?> parent){
  poses.pushPose();try{var head=parent.head;poses.translate(head.x/16F,head.y/16F,head.z/16F);if(head.zRot!=0)poses.mulPose(Axis.ZP.rotation(head.zRot));if(head.yRot!=0)poses.mulPose(Axis.YP.rotation(head.yRot));if(head.xRot!=0)poses.mulPose(Axis.XP.rotation(head.xRot));poses.scale(1F/16,1F/16,1F/16);LegacyObjRenderer.renderOnly(mesh,poses,buffers.getBuffer(net.neoforged.neoforge.client.NeoForgeRenderTypes.getUnlitTranslucent(HEAD,false)),0xF000F0,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,"Eyes");}finally{poses.popPose();}
 }
 private NcrArmorRenderer(){}
}
