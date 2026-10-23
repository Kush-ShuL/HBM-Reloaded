package com.hbm.client.entity.dummy;

import com.hbm.client.armor.LegacyBipedModel;
import com.hbm.client.model.legacy.LegacyModelRenderContext;
import com.hbm.client.model.legacy.ModelRenderer;
import com.hbm.entity.mob.dummy.EntityDummy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

/** Source ModelBiped(0), using the sole old box/UV/pose implementation. Native parts expose its pose to real equipment layers. */
final class TestDummyModel extends HumanoidModel<EntityDummy> {
    private final LegacyBipedModel original=new LegacyBipedModel(0);
    TestDummyModel(){super(LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE,0),64,32).bakeRoot());}
    @Override public void setupAnim(EntityDummy mob,float swing,float amount,float age,float yaw,float pitch){
        original.isChild=young;original.isRiding=riding;original.isSneak=mob.isCrouching();original.onGround=attackTime;
        original.heldItemRight=mob.getMainHandItem().isEmpty()?0:1;original.heldItemLeft=0;original.aimedBow=false;
        original.setRotationAngles(swing,amount,age,yaw,pitch,1F/16F,mob);
        copy(original.head,head);copy(original.headwear,hat);copy(original.body,body);
        copy(original.rightArm,rightArm);copy(original.leftArm,leftArm);copy(original.rightLeg,rightLeg);copy(original.leftLeg,leftLeg);
        crouching=original.isSneak;
    }
    private static void copy(ModelRenderer original,ModelPart view){view.setPos(original.rotationPointX,original.rotationPointY,original.rotationPointZ);view.setRotation(original.rotateAngleX,original.rotateAngleY,original.rotateAngleZ);}
    @Override public void renderToBuffer(PoseStack poses,VertexConsumer vertices,int light,int overlay,int color){
        // Pose is evaluated once, as in the existing Undead bridge; rendering it again would add arm pivot math twice.
        try(var ignored=LegacyModelRenderContext.push(poses,vertices,light,overlay,color)){
            original.head.render(1F/16F);original.body.render(1F/16F);original.rightArm.render(1F/16F);original.leftArm.render(1F/16F);
            original.rightLeg.render(1F/16F);original.leftLeg.render(1F/16F);original.headwear.render(1F/16F);
        }
    }
}
