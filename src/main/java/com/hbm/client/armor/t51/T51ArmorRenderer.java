package com.hbm.client.armor.t51;

import com.hbm.armor.t51.ArmorT51;
import com.hbm.armor.t51.T51Content;
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

/** Source ModelModelArmorT51+ModelRendererObj:8groups,4textures, original object pivots and Z/Y/X rotations. */
public final class T51ArmorRenderer {
    @FunctionalInterface public interface SourceRenderer {boolean render(PoseStack poses,MultiBufferSource buffers,int light,LivingEntity entity,EquipmentSlot slot,HumanoidModel<?> parent);}
    private static final java.util.Map<net.minecraft.world.item.Item,SourceRenderer> SOURCE_RENDERERS=new java.util.IdentityHashMap<>();
    public static void register(net.minecraft.world.item.Item item,SourceRenderer renderer){SOURCE_RENDERERS.put(item,renderer);}
    public static final ResourceLocation MODEL=T51Content.id("models/armor/t51.obj");
    public static final ResourceLocation HELMET=texture("helmet"),CHEST=texture("chest"),ARM=texture("arm"),LEG=texture("leg");
    private static ResourceLocation texture(String part){return T51Content.id("textures/armor/t51_"+part+".png");}
    public static boolean render(PoseStack poses,MultiBufferSource buffers,int light,LivingEntity entity,EquipmentSlot slot,HumanoidModel<?> parent){
        var stack=entity.getItemBySlot(slot);var custom=SOURCE_RENDERERS.get(stack.getItem());if(custom!=null)return custom.render(poses,buffers,light,entity,slot,parent);if(!(stack.getItem()instanceof ArmorT51 item)||item.getEquipmentSlot()!=slot)return false;
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        switch(slot){
            case HEAD->part(mesh,poses,buffers,light,stack.hasFoil(),HELMET,parent.head,0,0,0,0,"Helmet");
            case CHEST->{part(mesh,poses,buffers,light,stack.hasFoil(),CHEST,parent.body,0,0,0,0,"Chest");part(mesh,poses,buffers,light,stack.hasFoil(),ARM,parent.leftArm,5,2,0,0,"LeftArm");part(mesh,poses,buffers,light,stack.hasFoil(),ARM,parent.rightArm,-5,2,0,0,"RightArm");}
            case LEGS->{part(mesh,poses,buffers,light,stack.hasFoil(),LEG,parent.leftLeg,1.9F,12,0,0,"LeftLeg");part(mesh,poses,buffers,light,stack.hasFoil(),LEG,parent.rightLeg,-1.9F,12,0,0,"RightLeg");}
            case FEET->{part(mesh,poses,buffers,light,stack.hasFoil(),LEG,parent.leftLeg,1.9F,12,0,0,"LeftBoot");part(mesh,poses,buffers,light,stack.hasFoil(),LEG,parent.rightLeg,-1.9F,12,0,0,"RightBoot");}
            default->{}
        }
        return true;
    }
    public static void part(WavefrontMesh mesh,PoseStack poses,MultiBufferSource buffers,int light,boolean foil,ResourceLocation texture,ModelPart part,float ox,float oy,float oz,double extraX,String group){
        poses.pushPose();try{
            poses.translate(extraX,0,0);poses.translate(part.x/16F,part.y/16F,part.z/16F);
            if(part.zRot!=0)poses.mulPose(Axis.ZP.rotation(part.zRot));if(part.yRot!=0)poses.mulPose(Axis.YP.rotation(part.yRot));if(part.xRot!=0)poses.mulPose(Axis.XP.rotation(part.xRot));
            poses.translate(-ox/16F,-oy/16F,-oz/16F);poses.scale(1F/16F,1F/16F,1F/16F);
            LegacyObjRenderer.renderOnly(mesh,poses,ItemRenderer.getArmorFoilBuffer(buffers,RenderType.armorCutoutNoCull(texture),foil),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,group);
        }finally{poses.popPose();}
    }
    private T51ArmorRenderer(){}
}
