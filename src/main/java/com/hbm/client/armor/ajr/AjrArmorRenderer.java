package com.hbm.client.armor.ajr;
import com.hbm.armor.ajr.*;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ArmorItem;
/** Original ModelArmorAJR/ModelArmorAJRO deliberately share this authored mesh, but bind their distinct four PNGs. */
public final class AjrArmorRenderer {
 public static final ResourceLocation MODEL=AjrContent.id("models/armor/ajr.obj");
 public static ResourceLocation texture(boolean overlay,String part){return AjrContent.id("textures/armor/"+(overlay?"ajro_":"ajr_")+part+".png");}
 public static boolean render(PoseStack poses,MultiBufferSource buffers,int light,LivingEntity entity,EquipmentSlot slot,HumanoidModel<?> parent){
  var stack=entity.getItemBySlot(slot);if(!(stack.getItem() instanceof ArmorItem item)||item.getEquipmentSlot()!=slot||!(item instanceof ArmorAjr||item instanceof ArmorAjro))return false;
  boolean overlay=item instanceof ArmorAjro,foil=stack.hasFoil();var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
  var helmet=texture(overlay,"helmet");var chest=texture(overlay,"chest");var arm=texture(overlay,"arm");var leg=texture(overlay,"leg");
  switch(slot){
   case HEAD->T51ArmorRenderer.part(mesh,poses,buffers,light,foil,helmet,parent.head,0,0,0,0,"Head");
   case CHEST->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,chest,parent.body,0,0,0,0,"Body");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,arm,parent.leftArm,5,2,0,0,"LeftArm");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,arm,parent.rightArm,-5,2,0,0,"RightArm");}
   case LEGS->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,leg,parent.leftLeg,1.9F,12,0,0,"LeftLeg");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,leg,parent.rightLeg,-1.9F,12,0,0,"RightLeg");}
   case FEET->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,leg,parent.leftLeg,1.9F,12,0,0,"LeftBoot");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,leg,parent.rightLeg,-1.9F,12,0,0,"RightBoot");}
   default->{}
  }
  return true;
 }
 private AjrArmorRenderer(){}
}
