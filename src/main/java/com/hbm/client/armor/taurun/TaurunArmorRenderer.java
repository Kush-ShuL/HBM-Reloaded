package com.hbm.client.armor.taurun;

import com.hbm.armor.taurun.ArmorTaurun;
import com.hbm.armor.taurun.TaurunContent;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

/** Source ModelArmorTaurun+ModelRendererObj:8groups,4textures, original object pivots and Z/Y/X rotations. */
public final class TaurunArmorRenderer {
    public static final ResourceLocation MODEL=TaurunContent.id("models/armor/taurun.obj");
    public static final ResourceLocation HELMET=texture("helmet"),CHEST=texture("chest"),ARM=texture("arm"),LEG=texture("leg");
    private static ResourceLocation texture(String part){return TaurunContent.id("textures/armor/taurun_"+part+".png");}
    public static boolean render(PoseStack poses,MultiBufferSource buffers,int light,LivingEntity entity,EquipmentSlot slot,HumanoidModel<?> parent){
        var stack=entity.getItemBySlot(slot);if(!(stack.getItem()instanceof ArmorTaurun item)||item.getEquipmentSlot()!=slot)return false;
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        switch(slot){
            case HEAD->part(mesh,poses,buffers,light,stack.hasFoil(),HELMET,parent.head,0,0,0,0,"Helmet");
            case CHEST->{part(mesh,poses,buffers,light,stack.hasFoil(),CHEST,parent.body,0,0,0,0,"Chest");part(mesh,poses,buffers,light,stack.hasFoil(),ARM,parent.leftArm,5,2,0,0,"LeftArm");part(mesh,poses,buffers,light,stack.hasFoil(),ARM,parent.rightArm,-5,2,0,0,"RightArm");}
            case LEGS->{part(mesh,poses,buffers,light,stack.hasFoil(),LEG,parent.leftLeg,1.9F,12,0,-.01,"LeftLeg");part(mesh,poses,buffers,light,stack.hasFoil(),LEG,parent.rightLeg,-1.9F,12,0,.01,"RightLeg");}
            case FEET->{part(mesh,poses,buffers,light,stack.hasFoil(),LEG,parent.leftLeg,1.9F,12,0,-.01,"LeftBoot");part(mesh,poses,buffers,light,stack.hasFoil(),LEG,parent.rightLeg,-1.9F,12,0,.01,"RightBoot");}
            default->{}
        }
        return true;
    }
    private static void part(WavefrontMesh mesh,PoseStack poses,MultiBufferSource buffers,int light,boolean foil,ResourceLocation texture,ModelPart part,float ox,float oy,float oz,double extraX,String group){
        poses.pushPose();try{
            poses.translate(extraX,0,0);poses.translate(part.x/16F,part.y/16F,part.z/16F);
            if(part.zRot!=0)poses.mulPose(Axis.ZP.rotation(part.zRot));if(part.yRot!=0)poses.mulPose(Axis.YP.rotation(part.yRot));if(part.xRot!=0)poses.mulPose(Axis.XP.rotation(part.xRot));
            poses.translate(-ox/16F,-oy/16F,-oz/16F);poses.scale(1F/16F,1F/16F,1F/16F);
            LegacyObjRenderer.renderOnly(mesh,poses,ItemRenderer.getArmorFoilBuffer(buffers,RenderType.armorCutoutNoCull(texture),foil),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,group);
        }finally{poses.popPose();}
    }
    private TaurunArmorRenderer(){}
}
