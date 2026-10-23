package com.hbm.client.armor.t51;

import com.hbm.armor.t51.ArmorT51;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;

/** Exact source ItemRenderBase outer matrix, ArmorFSB.renderStandard. */
public final class T51ItemRenderer extends BlockEntityWithoutLevelRenderer {
    public T51ItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        if(!(stack.getItem()instanceof ArmorT51 item))return;var slot=item.getEquipmentSlot();poses.pushPose();try{
            if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);
            if(context==ItemDisplayContext.GUI){
                poses.scale(1F/16F,-1F/16F,1F/16F);poses.translate(0,2,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);
                poses.translate(0,-1.5,0);poses.scale(3.25F,3.25F,3.25F);poses.mulPose(Axis.XP.rotationDegrees(180));poses.mulPose(Axis.YP.rotationDegrees(-135));poses.mulPose(Axis.XP.rotationDegrees(-20));
            }else{
                boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;
                boolean equipped=context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                if(entity)poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(!equipped)poses.mulPose(Axis.YP.rotationDegrees(90));
                poses.mulPose(Axis.XP.rotationDegrees(180));poses.scale(.75F,.75F,.75F);poses.mulPose(Axis.YP.rotationDegrees(-90));
            }
            switch(slot){
                case HEAD->{poses.scale(.3125F,.3125F,.3125F);poses.translate(0,1,0);draw(poses,buffers,light,overlay,T51ArmorRenderer.HELMET,false,"Helmet");}
                case CHEST->{poses.scale(.225F,.225F,.225F);poses.translate(0,-10,0);draw(poses,buffers,light,overlay,T51ArmorRenderer.CHEST,false,"Chest");poses.translate(0,0,.1);draw(poses,buffers,light,overlay,T51ArmorRenderer.ARM,false,"LeftArm","RightArm");}
                case LEGS->{poses.scale(.25F,.25F,.25F);poses.translate(0,-20,0);draw(poses,buffers,light,overlay,T51ArmorRenderer.LEG,true,"LeftLeg");poses.translate(0,0,.1);draw(poses,buffers,light,overlay,T51ArmorRenderer.LEG,true,"RightLeg");}
                case FEET->{poses.scale(.25F,.25F,.25F);poses.translate(0,-22,0);draw(poses,buffers,light,overlay,T51ArmorRenderer.LEG,true,"LeftBoot");poses.translate(0,0,.1);draw(poses,buffers,light,overlay,T51ArmorRenderer.LEG,true,"RightBoot");}
                default->{}
            }
        }finally{poses.popPose();}
    }
    private static void draw(PoseStack poses,MultiBufferSource buffers,int light,int overlay,ResourceLocation texture,boolean noCull,String...parts){
        var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),T51ArmorRenderer.MODEL);
        LegacyObjRenderer.renderOnly(model,poses,buffers.getBuffer(noCull?RenderType.entityCutoutNoCull(texture):RenderType.entityCutout(texture)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,parts);
    }
}
