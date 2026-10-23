package com.hbm.client.entity.ghost;

import com.hbm.client.armor.LegacyBipedModel;
import com.hbm.client.model.legacy.*;
import com.hbm.entity.mob.ghost.EntityGhost;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;

/** Real old ordinary biped0.0/64x32, sharing the existing complete box/UV/animation kernel. */
public final class GhostModel extends HumanoidModel<EntityGhost> {
    private final LegacyBipedModel original=new LegacyBipedModel(0);
    public GhostModel(){super(LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE,0),64,32).bakeRoot(),GhostRenderTypes::body);}
    @Override public void setupAnim(EntityGhost entity,float limb,float amount,float age,float yaw,float pitch){
        original.isChild=young;original.isRiding=riding;original.isSneak=entity.isCrouching();original.onGround=attackTime;original.heldItemRight=entity.getMainHandItem().isEmpty()?0:1;original.heldItemLeft=0;original.aimedBow=false;
        original.setRotationAngles(limb,amount,age,yaw,pitch,1F/16F,entity);copy(original.head,head);copy(original.headwear,hat);copy(original.body,body);copy(original.rightArm,rightArm);copy(original.leftArm,leftArm);copy(original.rightLeg,rightLeg);copy(original.leftLeg,leftLeg);crouching=original.isSneak;
    }
    private static void copy(ModelRenderer original,ModelPart part){part.setPos(original.rotationPointX,original.rotationPointY,original.rotationPointZ);part.setRotation(original.rotateAngleX,original.rotateAngleY,original.rotateAngleZ);}
    @Override public void renderToBuffer(PoseStack poses,VertexConsumer output,int light,int overlay,int color){try(var ignored=LegacyModelRenderContext.push(poses,output,light,overlay,color)){original.head.render(1F/16F);original.body.render(1F/16F);original.rightArm.render(1F/16F);original.leftArm.render(1F/16F);original.rightLeg.render(1F/16F);original.leftLeg.render(1F/16F);original.headwear.render(1F/16F);}}
}
